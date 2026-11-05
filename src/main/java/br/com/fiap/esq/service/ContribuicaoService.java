package br.com.fiap.esq.service;

import br.com.fiap.esq.exception.EntidadeNaoEncontradaException;
import br.com.fiap.esq.exception.RegraNegocioException;
import br.com.fiap.esq.model.Conta;
import br.com.fiap.esq.model.Contribuicao;
import br.com.fiap.esq.model.Meta;
import br.com.fiap.esq.repository.ContaRepository;
import br.com.fiap.esq.repository.ContribuicaoRepository;
import br.com.fiap.esq.repository.MetaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Service
public class ContribuicaoService {

    private final ContribuicaoRepository contribuicaoRepository;
    private final MetaRepository metaRepository;
    private final ContaRepository contaRepository;

    public ContribuicaoService(ContribuicaoRepository contribuicaoRepository, MetaRepository metaRepository,
                              ContaRepository contaRepository) {
        this.contribuicaoRepository = contribuicaoRepository;
        this.metaRepository = metaRepository;
        this.contaRepository = contaRepository;
    }

    //Filtra por meta ou por usuário (todas as metas dele)
    public List<Contribuicao> listar(Integer idMeta, Integer idUsuario) {
        if (idMeta != null) return contribuicaoRepository.findByIdMetaOrderByDtContribuicaoDescIdContribuicaoDesc(idMeta);
        if (idUsuario != null) return contribuicaoRepository.listarDoUsuario(idUsuario);
        return contribuicaoRepository.findAll();
    }

    public Contribuicao buscar(int id) {
        return contribuicaoRepository.findById(id)
                .orElseThrow(() -> new EntidadeNaoEncontradaException("Contribuição não encontrada"));
    }

    @Transactional
    public Contribuicao cadastrar(Contribuicao contribuicao) {
        validar(contribuicao);
        contribuicao.setIdContribuicao(0);
        somarNaMeta(contribuicao.getIdMeta(), contribuicao.getVlContribuicao());
        return contribuicaoRepository.save(contribuicao);
    }

    @Transactional
    public Contribuicao atualizar(int id, Contribuicao dados) {
        Contribuicao atual = buscar(id);
        validar(dados);
        if (dados.getIdMeta() != atual.getIdMeta())
            throw new RegraNegocioException("Não é possível mudar a meta de uma contribuição");
        somarNaMeta(atual.getIdMeta(), atual.getVlContribuicao().negate());
        somarNaMeta(atual.getIdMeta(), dados.getVlContribuicao());
        atual.setVlContribuicao(dados.getVlContribuicao());
        atual.setDtContribuicao(dados.getDtContribuicao());
        return contribuicaoRepository.save(atual);
    }

    @Transactional
    public void deletar(int id) {
        Contribuicao atual = buscar(id);
        somarNaMeta(atual.getIdMeta(), atual.getVlContribuicao().negate());
        contribuicaoRepository.deleteById(id);
    }

    private void validar(Contribuicao contribuicao) {
        if (contribuicao.getVlContribuicao().compareTo(BigDecimal.ZERO) <= 0)
            throw new RegraNegocioException("O valor da contribuição deve ser maior que zero");
        if (!metaRepository.existsById(contribuicao.getIdMeta()))
            throw new RegraNegocioException("Meta informada não existe");
        if (contribuicao.getDtContribuicao() == null)
            contribuicao.setDtContribuicao(LocalDate.now());
        if (contribuicao.getDtContribuicao().isAfter(LocalDate.now()))
            throw new RegraNegocioException("A data da contribuição não pode ser futura");
    }

    //Soma (ou subtrai, se negativo) no valor guardado da meta, sem passar do objetivo
    private void somarNaMeta(int idMeta, BigDecimal valor) {
        Meta meta = metaRepository.findById(idMeta)
                .orElseThrow(() -> new RegraNegocioException("Meta informada não existe"));
        BigDecimal novoValor = meta.getVlAtual().add(valor);
        if (novoValor.compareTo(meta.getVlObjetivo()) > 0)
            throw new RegraNegocioException("A contribuição ultrapassa o objetivo da meta");
        //meta ligada a uma conta: o aporte sai de lá (e a retirada de uma contribuição devolve o dinheiro)
        if (meta.getIdConta() != null) {
            Conta conta = contaRepository.findById(meta.getIdConta())
                    .orElseThrow(() -> new RegraNegocioException("Conta da meta não existe"));
            BigDecimal saldo = conta.getVlSaldoConta().subtract(valor);
            if (saldo.compareTo(BigDecimal.ZERO) < 0)
                throw new RegraNegocioException("Saldo insuficiente na conta " + conta.getNmConta());
            conta.setVlSaldoConta(saldo);
            contaRepository.save(conta);
        }
        meta.setVlAtual(novoValor);
        metaRepository.save(meta);
    }
}
