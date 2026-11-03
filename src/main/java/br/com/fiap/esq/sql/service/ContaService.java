package br.com.fiap.esq.sql.service;

import br.com.fiap.esq.sql.exception.EntidadeNaoEncontradaException;
import br.com.fiap.esq.sql.exception.RegraNegocioException;
import br.com.fiap.esq.sql.model.Conta;
import br.com.fiap.esq.sql.repository.ContaRepository;
import br.com.fiap.esq.sql.repository.UsuarioRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
public class ContaService {

    private final ContaRepository contaRepository;
    private final UsuarioRepository usuarioRepository;

    public ContaService(ContaRepository contaRepository, UsuarioRepository usuarioRepository) {
        this.contaRepository = contaRepository;
        this.usuarioRepository = usuarioRepository;
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
        contaRepository.deleteById(id);
    }

    private void validar(Conta conta) {
        if (conta.getVlSaldoConta().compareTo(BigDecimal.ZERO) < 0)
            throw new RegraNegocioException("O saldo da conta não pode ser negativo");
        if (!usuarioRepository.existsById(conta.getIdUsuario()))
            throw new RegraNegocioException("Usuário informado não existe");
    }
}
