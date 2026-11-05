package br.com.fiap.esq.service;

import br.com.fiap.esq.exception.EntidadeNaoEncontradaException;
import br.com.fiap.esq.exception.RegraNegocioException;
import br.com.fiap.esq.model.Meta;
import br.com.fiap.esq.model.Conta;
import br.com.fiap.esq.repository.ContaRepository;
import br.com.fiap.esq.repository.MetaRepository;
import br.com.fiap.esq.repository.UsuarioRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Objects;

@Service
public class MetaService {

    private final MetaRepository metaRepository;
    private final UsuarioRepository usuarioRepository;
    private final ContaRepository contaRepository;

    public MetaService(MetaRepository metaRepository, UsuarioRepository usuarioRepository, ContaRepository contaRepository) {
        this.metaRepository = metaRepository;
        this.usuarioRepository = usuarioRepository;
        this.contaRepository = contaRepository;
    }

    public List<Meta> listar(Integer idUsuario) {
        return idUsuario == null ? metaRepository.findAll() : metaRepository.findByIdUsuario(idUsuario);
    }

    public Meta buscar(int id) {
        return metaRepository.findById(id)
                .orElseThrow(() -> new EntidadeNaoEncontradaException("Meta não encontrada"));
    }

    public Meta cadastrar(Meta meta) {
        validar(meta);
        if (meta.getDtPrazo().isBefore(LocalDate.now()))
            throw new RegraNegocioException("O prazo da meta não pode estar no passado");
        meta.setIdMeta(0);
        meta.setVlAtual(BigDecimal.ZERO);
        return metaRepository.save(meta);
    }

    //O valor guardado não muda aqui, só pelas contribuições
    public Meta atualizar(int id, Meta dados) {
        Meta atual = buscar(id);
        validar(dados);
        if (dados.getVlObjetivo().compareTo(atual.getVlAtual()) < 0)
            throw new RegraNegocioException("O objetivo não pode ser menor que o valor já guardado");
        atual.setNmMeta(dados.getNmMeta());
        atual.setDsMeta(dados.getDsMeta());
        atual.setVlObjetivo(dados.getVlObjetivo());
        atual.setDtPrazo(dados.getDtPrazo());
        if (!Objects.equals(atual.getIdConta(), dados.getIdConta()) && atual.getVlAtual().compareTo(BigDecimal.ZERO) > 0)
            throw new RegraNegocioException("Não dá para trocar a conta de uma meta que já tem valor guardado");
        atual.setIdConta(dados.getIdConta());
        atual.setIdUsuario(dados.getIdUsuario());
        return metaRepository.save(atual);
    }

    //Se a meta tinha conta, o valor guardado volta para ela
    @Transactional
    public void deletar(int id) {
        Meta meta = buscar(id);
        if (meta.getIdConta() != null && meta.getVlAtual().compareTo(BigDecimal.ZERO) > 0) {
            Conta conta = contaRepository.findById(meta.getIdConta()).orElse(null);
            if (conta != null) {
                conta.setVlSaldoConta(conta.getVlSaldoConta().add(meta.getVlAtual()));
                contaRepository.save(conta);
            }
        }
        metaRepository.deleteById(id);
    }

    private void validar(Meta meta) {
        if (meta.getVlObjetivo().compareTo(BigDecimal.ZERO) <= 0)
            throw new RegraNegocioException("O objetivo da meta deve ser maior que zero");
        if (!usuarioRepository.existsById(meta.getIdUsuario()))
            throw new RegraNegocioException("Usuário informado não existe");
        if (meta.getIdConta() != null) {
            Conta conta = contaRepository.findById(meta.getIdConta())
                    .orElseThrow(() -> new RegraNegocioException("Conta informada não existe"));
            if (conta.getIdUsuario() != meta.getIdUsuario())
                throw new RegraNegocioException("A conta informada não pertence ao usuário");
        }
    }
}
