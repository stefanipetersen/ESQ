package br.com.fiap.esq.service;

import br.com.fiap.esq.exception.EntidadeNaoEncontradaException;
import br.com.fiap.esq.exception.RegraNegocioException;
import br.com.fiap.esq.model.Conta;
import br.com.fiap.esq.repository.ContaRepository;
import br.com.fiap.esq.repository.TransacaoRepository;
import br.com.fiap.esq.repository.TransferenciaRepository;
import br.com.fiap.esq.repository.UsuarioRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
public class ContaService {

    private final ContaRepository contaRepository;
    private final UsuarioRepository usuarioRepository;
    private final TransacaoRepository transacaoRepository;
    private final TransferenciaRepository transferenciaRepository;

    public ContaService(ContaRepository contaRepository, UsuarioRepository usuarioRepository,
                        TransacaoRepository transacaoRepository, TransferenciaRepository transferenciaRepository) {
        this.transferenciaRepository = transferenciaRepository;
        this.contaRepository = contaRepository;
        this.usuarioRepository = usuarioRepository;
        this.transacaoRepository = transacaoRepository;
    }

    public List<Conta> listar() {
        return contaRepository.findAll();
    }

    public Conta buscar(int id) {
        return contaRepository.findById(id)
                .orElseThrow(() -> new EntidadeNaoEncontradaException("Conta não encontrada"));
    }

    public Conta cadastrar(Conta conta) {
        validar(conta);
        conta.setIdConta(0);
        return contaRepository.save(conta);
    }

    public Conta atualizar(int id, Conta dados) {
        Conta atual = buscar(id);
        validar(dados);
        atual.setNmConta(dados.getNmConta());
        atual.setTpConta(dados.getTpConta());
        atual.setVlSaldoConta(dados.getVlSaldoConta());
        atual.setIdUsuario(dados.getIdUsuario());
        return contaRepository.save(atual);
    }

    public void deletar(int id) {
        buscar(id);
        if (transacaoRepository.existsByIdConta(id) || transferenciaRepository.existsByIdContaOrigemOrIdContaDestino(id, id))
            throw new RegraNegocioException("Conta possui transações ou transferências e não pode ser removida");
        contaRepository.deleteById(id);
    }

    private void validar(Conta conta) {
        if (conta.getVlSaldoConta().compareTo(BigDecimal.ZERO) < 0)
            throw new RegraNegocioException("O saldo da conta não pode ser negativo");
        if (!usuarioRepository.existsById(conta.getIdUsuario()))
            throw new RegraNegocioException("Usuário informado não existe");
    }
}
