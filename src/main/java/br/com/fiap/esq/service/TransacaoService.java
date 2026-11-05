package br.com.fiap.esq.service;

import br.com.fiap.esq.exception.EntidadeNaoEncontradaException;
import br.com.fiap.esq.exception.RegraNegocioException;
import br.com.fiap.esq.model.Categoria;
import br.com.fiap.esq.model.Conta;
import br.com.fiap.esq.model.Transacao;
import br.com.fiap.esq.repository.CategoriaRepository;
import br.com.fiap.esq.repository.ContaRepository;
import br.com.fiap.esq.repository.TransacaoRepository;
import br.com.fiap.esq.repository.UsuarioRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionTemplate;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.List;

@Service
public class TransacaoService {

    private static final Logger log = LoggerFactory.getLogger(TransacaoService.class);
    private static final int MAXIMO_PARCELAS = 60;
    private static final int MESES_FIXOS = 12; //quantos meses de um gasto fixo ficam criados à frente

    private final TransacaoRepository transacaoRepository;
    private final ContaRepository contaRepository;
    private final CategoriaRepository categoriaRepository;
    private final UsuarioRepository usuarioRepository;
    private final TransactionTemplate tx;

    public TransacaoService(TransacaoRepository transacaoRepository, ContaRepository contaRepository,
                            CategoriaRepository categoriaRepository, UsuarioRepository usuarioRepository,
                            PlatformTransactionManager transactionManager) {
        this.transacaoRepository = transacaoRepository;
        this.contaRepository = contaRepository;
        this.categoriaRepository = categoriaRepository;
        this.usuarioRepository = usuarioRepository;
        this.tx = new TransactionTemplate(transactionManager);
    }

    //Filtros opcionais: usuário e tipo (a tela de gastos pede só as DESPESA)
    public List<Transacao> listar(Integer idUsuario, String tipo) {
        List<Transacao> lista = idUsuario == null
                ? transacaoRepository.findAll()
                : transacaoRepository.findByIdUsuarioOrderByDtTransacaoDescIdTransacaoDesc(idUsuario);
        if (tipo == null || tipo.isBlank()) return lista;
        return lista.stream().filter(t -> t.getTpTransacao().equalsIgnoreCase(tipo)).toList();
    }

    public Transacao buscar(int id) {
        return transacaoRepository.findById(id)
                .orElseThrow(() -> new EntidadeNaoEncontradaException("Transação não encontrada"));
    }

    /*
     * Cadastra a transação. Ela pode ser:
     *  - parcelada (só despesa, nrParcelas > 1): o valor informado é o TOTAL e é dividido em uma parcela por mês;
     *  - fixa (flFixo, despesa ou receita, ex.: Netflix ou salário): o mesmo valor se repete todo mês; ficam criados os próximos 12 meses e o sistema
     *    renova sozinho (veja aplicarParcelasVencidas).
     * Devolve a primeira ocorrência.
     */
    @Transactional
    public Transacao cadastrar(Transacao transacao) {
        int parcelas = Math.max(1, transacao.getNrParcelas());
        boolean fixo = transacao.isFlFixo();
        validar(transacao, false);
        if (parcelas > 1 && !transacao.getTpTransacao().equals("DESPESA"))
            throw new RegraNegocioException("Só despesas podem ser parceladas");
        if (parcelas > 1 && fixo)
            throw new RegraNegocioException("Um gasto não pode ser parcelado e fixo ao mesmo tempo");
        if (parcelas > MAXIMO_PARCELAS)
            throw new RegraNegocioException("O número máximo de parcelas é " + MAXIMO_PARCELAS);

        BigDecimal total = transacao.getVlTransacao();
        BigDecimal valorParcela = total.divide(BigDecimal.valueOf(parcelas), 2, RoundingMode.DOWN);
        if (valorParcela.compareTo(BigDecimal.ZERO) <= 0)
            throw new RegraNegocioException("O valor é pequeno demais para " + parcelas + " parcelas");
        //os centavos que sobram da divisão ficam na primeira parcela
        BigDecimal sobra = total.subtract(valorParcela.multiply(BigDecimal.valueOf(parcelas)));

        int ocorrencias = fixo ? MESES_FIXOS : parcelas;
        Transacao primeira = null;
        for (int i = 1; i <= ocorrencias; i++) {
            Transacao ocorrencia = ocorrencias == 1 ? transacao : copiar(transacao);
            ocorrencia.setIdTransacao(0);
            ocorrencia.setNrParcela(fixo ? 1 : i);
            ocorrencia.setNrParcelas(fixo ? 1 : parcelas);
            ocorrencia.setFlFixo(fixo);
            if (ocorrencias > 1) {
                ocorrencia.setDtTransacao(transacao.getDtTransacao().plusMonths(i - 1));
                if (!fixo) {
                    ocorrencia.setDsTransacao(transacao.getDsTransacao() + " (" + i + "/" + parcelas + ")");
                    ocorrencia.setVlTransacao(i == 1 ? valorParcela.add(sobra) : valorParcela);
                }
                if (primeira != null) ocorrencia.setIdGrupo(primeira.getIdTransacao());
            }
            //ocorrência futura só mexe no saldo quando a data chegar
            ocorrencia.setFlAplicada(false);
            if (!ocorrencia.getDtTransacao().isAfter(LocalDate.now())) {
                aplicarNoSaldo(ocorrencia, 1);
                ocorrencia.setFlAplicada(true);
            }
            Transacao salva = transacaoRepository.save(ocorrencia);
            if (primeira == null) {
                primeira = salva;
                if (ocorrencias > 1) primeira.setIdGrupo(primeira.getIdTransacao());
            }
        }
        return primeira;
    }

    /*
     * Atualiza esta transação. Com proximas = true, em um gasto fixo ou parcelado o valor, a categoria e a conta
     * novos também valem para as ocorrências dos meses seguintes (a data e a numeração de cada uma ficam como estão).
     */
    @Transactional
    public Transacao atualizar(int id, Transacao dados, boolean proximas) {
        Transacao atual = buscar(id);
        LocalDate dataOriginal = atual.getDtTransacao();
        validar(dados, atual.getIdGrupo() != null);
        if (atual.isFlAplicada()) aplicarNoSaldo(atual, -1);
        atual.setDsTransacao(dados.getDsTransacao());
        atual.setVlTransacao(dados.getVlTransacao());
        atual.setTpTransacao(dados.getTpTransacao());
        atual.setDtTransacao(dados.getDtTransacao());
        atual.setIdCategoria(dados.getIdCategoria());
        atual.setIdConta(dados.getIdConta());
        atual.setIdUsuario(dados.getIdUsuario());
        atual.setFlAplicada(false);
        if (!atual.getDtTransacao().isAfter(LocalDate.now())) {
            aplicarNoSaldo(atual, 1);
            atual.setFlAplicada(true);
        }
        Transacao salva = transacaoRepository.save(atual);
        if (proximas && atual.getIdGrupo() != null) propagarParaProximas(atual, dataOriginal, dados);
        return salva;
    }

    private void propagarParaProximas(Transacao origem, LocalDate aPartirDe, Transacao dados) {
        for (Transacao t : transacaoRepository.findByIdGrupoAndDtTransacaoGreaterThanEqual(origem.getIdGrupo(), aPartirDe)) {
            if (t.getIdTransacao() == origem.getIdTransacao()) continue;
            if (t.isFlAplicada()) aplicarNoSaldo(t, -1);
            t.setVlTransacao(dados.getVlTransacao());
            t.setIdCategoria(dados.getIdCategoria());
            t.setIdConta(dados.getIdConta());
            //nas parcelas a descrição leva "(3/10)", por isso só o gasto fixo copia a descrição
            if (origem.isFlFixo()) t.setDsTransacao(dados.getDsTransacao());
            t.setFlAplicada(false);
            if (!t.getDtTransacao().isAfter(LocalDate.now())) {
                aplicarNoSaldo(t, 1);
                t.setFlAplicada(true);
            }
            transacaoRepository.save(t);
        }
    }

    //Com proximas = true apaga esta e as ocorrências seguintes da mesma compra (encerra parcelas ou gasto fixo)
    @Transactional
    public void deletar(int id, boolean proximas) {
        Transacao atual = buscar(id);
        List<Transacao> alvo = proximas && atual.getIdGrupo() != null
                ? transacaoRepository.findByIdGrupoAndDtTransacaoGreaterThanEqual(atual.getIdGrupo(), atual.getDtTransacao())
                : List.of(atual);
        for (Transacao t : alvo) {
            if (t.isFlAplicada()) aplicarNoSaldo(t, -1);
            transacaoRepository.delete(t);
        }
    }

    /*
     * Roda quando o sistema sobe e todo dia à 00:05:
     *  1) mantém os próximos 12 meses criados para cada gasto fixo;
     *  2) desconta do saldo as ocorrências cuja data já chegou. Se o saldo da conta não cobrir, ela fica pendente
     *     e é tentada de novo no dia seguinte.
     * Nunca lança erro: uma falha aqui (ex.: banco fora do ar) não pode derrubar o sistema. Cada item tem a sua transação.
     */
    @EventListener(ApplicationReadyEvent.class)
    @Scheduled(cron = "0 5 0 * * *")
    public void aplicarParcelasVencidas() {
        renovarGastosFixos();
        try {
            for (Transacao t : transacaoRepository.findByFlAplicadaFalseAndDtTransacaoLessThanEqual(LocalDate.now())) {
                try {
                    tx.executeWithoutResult(status -> {
                        aplicarNoSaldo(t, 1);
                        t.setFlAplicada(true);
                        transacaoRepository.save(t);
                    });
                } catch (RuntimeException e) {
                    log.warn("Transação {} continua pendente: {}", t.getIdTransacao(), e.getMessage());
                }
            }
        } catch (RuntimeException e) {
            log.warn("Não foi possível aplicar as transações vencidas (as colunas novas existem no banco?): {}", e.getMessage());
        }
    }

    private void renovarGastosFixos() {
        try {
            LocalDate limite = LocalDate.now().plusMonths(MESES_FIXOS - 1);
            for (Object[] linha : transacaoRepository.ultimasDosGastosFixos()) {
                Integer grupo = (Integer) linha[0];
                LocalDate ultima = (LocalDate) linha[1];
                if (grupo == null || !ultima.isBefore(limite)) continue;
                try {
                    Transacao modelo = transacaoRepository.findFirstByIdGrupoOrderByDtTransacaoDesc(grupo).orElseThrow();
                    tx.executeWithoutResult(status -> {
                        for (LocalDate data = ultima.plusMonths(1); !data.isAfter(limite); data = data.plusMonths(1)) {
                            Transacao nova = copiar(modelo);
                            nova.setNrParcela(1);
                            nova.setNrParcelas(1);
                            nova.setFlFixo(true);
                            nova.setIdGrupo(grupo);
                            nova.setDtTransacao(data);
                            nova.setFlAplicada(false);
                            transacaoRepository.save(nova);
                        }
                    });
                } catch (RuntimeException e) {
                    log.warn("Não foi possível renovar o gasto fixo {}: {}", grupo, e.getMessage());
                }
            }
        } catch (RuntimeException e) {
            log.warn("Não foi possível renovar os gastos fixos (as colunas novas existem no banco?): {}", e.getMessage());
        }
    }

    private Transacao copiar(Transacao origem) {
        Transacao copia = new Transacao();
        copia.setDsTransacao(origem.getDsTransacao());
        copia.setVlTransacao(origem.getVlTransacao());
        copia.setTpTransacao(origem.getTpTransacao());
        copia.setDtTransacao(origem.getDtTransacao());
        copia.setIdCategoria(origem.getIdCategoria());
        copia.setIdConta(origem.getIdConta());
        copia.setIdUsuario(origem.getIdUsuario());
        return copia;
    }

    //permiteFutura: só as ocorrências de um gasto parcelado ou fixo podem ter data no futuro
    private void validar(Transacao t, boolean permiteFutura) {
        if (t.getVlTransacao().compareTo(BigDecimal.ZERO) <= 0)
            throw new RegraNegocioException("O valor da transação deve ser maior que zero");
        String tipo = t.getTpTransacao().toUpperCase();
        if (!tipo.equals("RECEITA") && !tipo.equals("DESPESA"))
            throw new RegraNegocioException("Tipo da transação deve ser RECEITA ou DESPESA");
        t.setTpTransacao(tipo);
        if (t.getDtTransacao() == null)
            t.setDtTransacao(LocalDate.now());
        if (!permiteFutura && t.getDtTransacao().isAfter(LocalDate.now()))
            throw new RegraNegocioException("A data da transação não pode ser futura");
        if (!usuarioRepository.existsById(t.getIdUsuario()))
            throw new RegraNegocioException("Usuário informado não existe");

        Conta conta = contaRepository.findById(t.getIdConta())
                .orElseThrow(() -> new RegraNegocioException("Conta informada não existe"));
        if (conta.getIdUsuario() != t.getIdUsuario())
            throw new RegraNegocioException("A conta informada não pertence ao usuário");

        Categoria categoria = categoriaRepository.findById(t.getIdCategoria())
                .orElseThrow(() -> new RegraNegocioException("Categoria informada não existe"));
        if (categoria.getIdUsuario() != t.getIdUsuario())
            throw new RegraNegocioException("A categoria informada não pertence ao usuário");
        if (!categoria.getTpCategoria().equals(tipo))
            throw new RegraNegocioException("A categoria deve ser do mesmo tipo da transação");
    }

    //sentido 1 aplica a transação na conta, -1 desfaz (receita soma, despesa subtrai)
    private void aplicarNoSaldo(Transacao t, int sentido) {
        Conta conta = contaRepository.findById(t.getIdConta())
                .orElseThrow(() -> new RegraNegocioException("Conta informada não existe"));
        BigDecimal efeito = t.getTpTransacao().equals("DESPESA") ? t.getVlTransacao().negate() : t.getVlTransacao();
        if (sentido < 0) efeito = efeito.negate();
        BigDecimal novoSaldo = conta.getVlSaldoConta().add(efeito);
        if (novoSaldo.compareTo(BigDecimal.ZERO) < 0)
            throw new RegraNegocioException("Saldo insuficiente na conta " + conta.getNmConta());
        conta.setVlSaldoConta(novoSaldo);
        contaRepository.save(conta);
    }
}
