package br.com.fiap.esq.service;

import br.com.fiap.esq.exception.EntidadeNaoEncontradaException;
import br.com.fiap.esq.exception.RegraNegocioException;
import br.com.fiap.esq.model.Conta;
import br.com.fiap.esq.model.Transferencia;
import br.com.fiap.esq.repository.ContaRepository;
import br.com.fiap.esq.repository.TransferenciaRepository;
import br.com.fiap.esq.repository.UsuarioRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Service
public class TransferenciaService {

    private final TransferenciaRepository transferenciaRepository;
    private final ContaRepository contaRepository;
    private final UsuarioRepository usuarioRepository;

    public TransferenciaService(TransferenciaRepository transferenciaRepository, ContaRepository contaRepository,
                                UsuarioRepository usuarioRepository) {
        this.transferenciaRepository = transferenciaRepository;
        this.contaRepository = contaRepository;
        this.usuarioRepository = usuarioRepository;
    }

    public List<Transferencia> listar(Integer idUsuario) {
        return idUsuario == null
                ? transferenciaRepository.findAll()
                : transferenciaRepository.findByIdUsuarioOrderByDtTransferenciaDescIdTransferenciaDesc(idUsuario);
    }

    public Transferencia buscar(int id) {
        return transferenciaRepository.findById(id)
                .orElseThrow(() -> new EntidadeNaoEncontradaException("Transferência não encontrada"));
    }

    @Transactional
    public Transferencia cadastrar(Transferencia transferencia) {
        validar(transferencia);
        transferencia.setIdTransferencia(0);
        mover(transferencia, 1);
        return transferenciaRepository.save(transferencia);
    }

    @Transactional
    public Transferencia atualizar(int id, Transferencia dados) {
        Transferencia atual = buscar(id);
        validar(dados);
        mover(atual, -1);
        atual.setIdContaOrigem(dados.getIdContaOrigem());
        atual.setIdContaDestino(dados.getIdContaDestino());
        atual.setVlTransferencia(dados.getVlTransferencia());
        atual.setDtTransferencia(dados.getDtTransferencia());
        atual.setDsTransferencia(dados.getDsTransferencia());
        atual.setIdUsuario(dados.getIdUsuario());
        mover(atual, 1);
        return transferenciaRepository.save(atual);
    }

    @Transactional
    public void deletar(int id) {
        Transferencia atual = buscar(id);
        mover(atual, -1);
        transferenciaRepository.deleteById(id);
    }

    private void validar(Transferencia t) {
        if (t.getVlTransferencia().compareTo(BigDecimal.ZERO) <= 0)
            throw new RegraNegocioException("O valor da transferência deve ser maior que zero");
        if (t.getIdContaOrigem() == t.getIdContaDestino())
            throw new RegraNegocioException("A conta de origem e a de destino devem ser diferentes");
        if (t.getDtTransferencia() == null)
            t.setDtTransferencia(LocalDate.now());
        if (t.getDtTransferencia().isAfter(LocalDate.now()))
            throw new RegraNegocioException("A data da transferência não pode ser futura");
        if (!usuarioRepository.existsById(t.getIdUsuario()))
            throw new RegraNegocioException("Usuário informado não existe");
        for (int idConta : new int[]{t.getIdContaOrigem(), t.getIdContaDestino()}) {
            Conta conta = contaRepository.findById(idConta)
                    .orElseThrow(() -> new RegraNegocioException("Conta informada não existe"));
            if (conta.getIdUsuario() != t.getIdUsuario())
                throw new RegraNegocioException("As contas precisam pertencer ao usuário");
        }
    }

    //sentido 1 tira da origem e põe no destino; -1 desfaz
    private void mover(Transferencia t, int sentido) {
        BigDecimal valor = t.getVlTransferencia().multiply(BigDecimal.valueOf(sentido));
        ajustar(t.getIdContaOrigem(), valor.negate());
        ajustar(t.getIdContaDestino(), valor);
    }

    private void ajustar(int idConta, BigDecimal efeito) {
        Conta conta = contaRepository.findById(idConta)
                .orElseThrow(() -> new RegraNegocioException("Conta informada não existe"));
        BigDecimal novoSaldo = conta.getVlSaldoConta().add(efeito);
        if (novoSaldo.compareTo(BigDecimal.ZERO) < 0)
            throw new RegraNegocioException("Saldo insuficiente na conta " + conta.getNmConta());
        conta.setVlSaldoConta(novoSaldo);
        contaRepository.save(conta);
    }
}
