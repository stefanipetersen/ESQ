package br.com.fiap.esq.service;

import br.com.fiap.esq.exception.EntidadeNaoEncontradaException;
import br.com.fiap.esq.exception.RegraNegocioException;
import br.com.fiap.esq.model.Investimento;
import br.com.fiap.esq.repository.InvestimentoRepository;
import br.com.fiap.esq.repository.UsuarioRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Service
public class InvestimentoService {

    private final InvestimentoRepository investimentoRepository;
    private final UsuarioRepository usuarioRepository;

    public InvestimentoService(InvestimentoRepository investimentoRepository, UsuarioRepository usuarioRepository) {
        this.investimentoRepository = investimentoRepository;
        this.usuarioRepository = usuarioRepository;
    }

    public List<Investimento> listar() {
        return investimentoRepository.findAll();
    }

    public Investimento buscar(int id) {
        return investimentoRepository.findById(id)
                .orElseThrow(() -> new EntidadeNaoEncontradaException("Investimento não encontrado"));
    }

    public Investimento cadastrar(Investimento investimento) {
        validar(investimento);
        investimento.setIdInvestimento(0);
        return investimentoRepository.save(investimento);
    }

    public Investimento atualizar(int id, Investimento dados) {
        Investimento atual = buscar(id);
        validar(dados);
        atual.setStrTipo(dados.getStrTipo());
        atual.setVlInicialInvestimento(dados.getVlInicialInvestimento());
        atual.setPcRendimento(dados.getPcRendimento());
        atual.setDtAplicacao(dados.getDtAplicacao());
        atual.setIdUsuario(dados.getIdUsuario());
        return investimentoRepository.save(atual);
    }

    public void deletar(int id) {
        buscar(id);
        investimentoRepository.deleteById(id);
    }

    private void validar(Investimento investimento) {
        if (investimento.getVlInicialInvestimento().compareTo(BigDecimal.ZERO) <= 0)
            throw new RegraNegocioException("O valor inicial do investimento deve ser maior que zero");
        if (investimento.getDtAplicacao() == null)
            investimento.setDtAplicacao(LocalDate.now());
        else if (investimento.getDtAplicacao().isAfter(LocalDate.now()))
            throw new RegraNegocioException("A data de aplicação não pode ser futura");
        if (!usuarioRepository.existsById(investimento.getIdUsuario()))
            throw new RegraNegocioException("Usuário informado não existe");
    }
}
