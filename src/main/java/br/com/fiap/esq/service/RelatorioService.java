package br.com.fiap.esq.service;

import br.com.fiap.esq.dto.ResumoRelatorio;
import br.com.fiap.esq.dto.ResumoRelatorio.EvolucaoMes;
import br.com.fiap.esq.dto.ResumoRelatorio.FluxoDia;
import br.com.fiap.esq.dto.ResumoRelatorio.GastoCategoria;
import br.com.fiap.esq.exception.EntidadeNaoEncontradaException;
import br.com.fiap.esq.exception.RegraNegocioException;
import br.com.fiap.esq.model.Categoria;
import br.com.fiap.esq.model.Contribuicao;
import br.com.fiap.esq.model.Conta;
import br.com.fiap.esq.model.Meta;
import br.com.fiap.esq.model.Transacao;
import br.com.fiap.esq.repository.CategoriaRepository;
import br.com.fiap.esq.repository.ContaRepository;
import br.com.fiap.esq.repository.ContribuicaoRepository;
import br.com.fiap.esq.repository.MetaRepository;
import br.com.fiap.esq.repository.TransacaoRepository;
import br.com.fiap.esq.repository.UsuarioRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class RelatorioService {

    private static final BigDecimal CEM = BigDecimal.valueOf(100);

    private final UsuarioRepository usuarioRepository;
    private final ContaRepository contaRepository;
    private final TransacaoRepository transacaoRepository;
    private final CategoriaRepository categoriaRepository;
    private final MetaRepository metaRepository;
    private final ContribuicaoRepository contribuicaoRepository;

    public RelatorioService(UsuarioRepository usuarioRepository, ContaRepository contaRepository,
                            TransacaoRepository transacaoRepository, CategoriaRepository categoriaRepository,
                            MetaRepository metaRepository, ContribuicaoRepository contribuicaoRepository) {
        this.usuarioRepository = usuarioRepository;
        this.contaRepository = contaRepository;
        this.transacaoRepository = transacaoRepository;
        this.categoriaRepository = categoriaRepository;
        this.metaRepository = metaRepository;
        this.contribuicaoRepository = contribuicaoRepository;
    }

    public ResumoRelatorio resumo(int idUsuario, Integer mes, Integer ano) {
        if (!usuarioRepository.existsById(idUsuario))
            throw new EntidadeNaoEncontradaException("Usuário não encontrado");
        YearMonth hoje = YearMonth.now();
        int mesConsulta = mes == null ? hoje.getMonthValue() : mes;
        int anoConsulta = ano == null ? hoje.getYear() : ano;
        if (mesConsulta < 1 || mesConsulta > 12)
            throw new RegraNegocioException("O mês deve estar entre 1 e 12");
        YearMonth periodo = YearMonth.of(anoConsulta, mesConsulta);

        List<Transacao> doMes = transacaoRepository.findByIdUsuarioAndDtTransacaoBetween(
                idUsuario, periodo.atDay(1), periodo.atEndOfMonth());
        BigDecimal receitas = somar(doMes, "RECEITA");
        BigDecimal despesas = somar(doMes, "DESPESA");
        BigDecimal economia = receitas.subtract(despesas);

        BigDecimal saldo = contaRepository.findByIdUsuario(idUsuario).stream()
                .map(Conta::getVlSaldoConta).reduce(BigDecimal.ZERO, BigDecimal::add);
        //quanto o saldo cresceu no mês em relação ao saldo do início do mês (só conta o que já afetou o saldo)
        List<Transacao> aplicadas = doMes.stream().filter(Transacao::isFlAplicada).toList();
        BigDecimal economiaAplicada = somar(aplicadas, "RECEITA").subtract(somar(aplicadas, "DESPESA"));
        BigDecimal saldoInicial = saldo.subtract(economiaAplicada);

        List<Meta> metas = metaRepository.findByIdUsuario(idUsuario);

        return new ResumoRelatorio(
                mesConsulta, anoConsulta,
                saldo,
                porcentagem(economiaAplicada, saldoInicial),
                receitas, despesas, economia,
                porcentagem(economia, receitas),
                gastosPorCategoria(idUsuario, doMes, despesas),
                fluxoCaixa(doMes, periodo, hoje),
                metas.stream().map(Meta::getVlAtual).reduce(BigDecimal.ZERO, BigDecimal::add),
                metas.stream().map(Meta::getVlObjetivo).reduce(BigDecimal.ZERO, BigDecimal::add),
                evolucaoMetas(idUsuario, periodo));
    }

    private BigDecimal somar(List<Transacao> transacoes, String tipo) {
        return transacoes.stream().filter(t -> t.getTpTransacao().equals(tipo))
                .map(Transacao::getVlTransacao).reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    //parte / total em %, com 1 casa decimal (0 se o total for zero ou negativo)
    private BigDecimal porcentagem(BigDecimal parte, BigDecimal total) {
        if (total.compareTo(BigDecimal.ZERO) <= 0) return BigDecimal.ZERO;
        return parte.multiply(CEM).divide(total, 1, RoundingMode.HALF_UP);
    }

    private List<GastoCategoria> gastosPorCategoria(int idUsuario, List<Transacao> doMes, BigDecimal totalDespesas) {
        Map<Integer, String> nomes = new HashMap<>();
        for (Categoria c : categoriaRepository.findByIdUsuario(idUsuario)) nomes.put(c.getIdCategoria(), c.getNmCategoria());

        Map<Integer, BigDecimal> porCategoria = new HashMap<>();
        doMes.stream().filter(t -> t.getTpTransacao().equals("DESPESA"))
                .forEach(t -> porCategoria.merge(t.getIdCategoria(), t.getVlTransacao(), BigDecimal::add));

        return porCategoria.entrySet().stream()
                .map(e -> new GastoCategoria(e.getKey(), nomes.getOrDefault(e.getKey(), "Sem categoria"),
                        e.getValue(), porcentagem(e.getValue(), totalDespesas)))
                .sorted(Comparator.comparing(GastoCategoria::valor).reversed())
                .toList();
    }

    //Um ponto por dia com o acumulado do mês (no mês atual vai só até hoje)
    private List<FluxoDia> fluxoCaixa(List<Transacao> doMes, YearMonth periodo, YearMonth hoje) {
        int ultimoDia = periodo.equals(hoje) ? LocalDate.now().getDayOfMonth() : periodo.lengthOfMonth();
        List<FluxoDia> fluxo = new ArrayList<>();
        BigDecimal receitas = BigDecimal.ZERO;
        BigDecimal despesas = BigDecimal.ZERO;
        for (int dia = 1; dia <= ultimoDia; dia++) {
            for (Transacao t : doMes) {
                if (t.getDtTransacao().getDayOfMonth() != dia) continue;
                if (t.getTpTransacao().equals("RECEITA")) receitas = receitas.add(t.getVlTransacao());
                else despesas = despesas.add(t.getVlTransacao());
            }
            fluxo.add(new FluxoDia(dia, receitas, despesas));
        }
        return fluxo;
    }

    //Total guardado nas metas em cada um dos últimos 6 meses
    private List<EvolucaoMes> evolucaoMetas(int idUsuario, YearMonth periodo) {
        List<Contribuicao> contribuicoes = contribuicaoRepository.listarDoUsuario(idUsuario);
        List<EvolucaoMes> evolucao = new ArrayList<>();
        for (int i = 5; i >= 0; i--) {
            YearMonth m = periodo.minusMonths(i);
            BigDecimal total = contribuicoes.stream()
                    .filter(c -> YearMonth.from(c.getDtContribuicao()).equals(m))
                    .map(Contribuicao::getVlContribuicao).reduce(BigDecimal.ZERO, BigDecimal::add);
            evolucao.add(new EvolucaoMes(m.getYear(), m.getMonthValue(), total));
        }
        return evolucao;
    }
}
