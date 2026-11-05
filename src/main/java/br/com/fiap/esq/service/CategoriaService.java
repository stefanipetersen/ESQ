package br.com.fiap.esq.service;

import br.com.fiap.esq.exception.EntidadeNaoEncontradaException;
import br.com.fiap.esq.exception.RegraNegocioException;
import br.com.fiap.esq.model.Categoria;
import br.com.fiap.esq.repository.CategoriaRepository;
import br.com.fiap.esq.repository.TransacaoRepository;
import br.com.fiap.esq.repository.UsuarioRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
public class CategoriaService {

    private final CategoriaRepository categoriaRepository;
    private final UsuarioRepository usuarioRepository;
    private final TransacaoRepository transacaoRepository;

    public CategoriaService(CategoriaRepository categoriaRepository, UsuarioRepository usuarioRepository,
                            TransacaoRepository transacaoRepository) {
        this.categoriaRepository = categoriaRepository;
        this.usuarioRepository = usuarioRepository;
        this.transacaoRepository = transacaoRepository;
    }

    //Se vier o idUsuario, lista só as categorias dele
    public List<Categoria> listar(Integer idUsuario) {
        return idUsuario == null ? categoriaRepository.findAll() : categoriaRepository.findByIdUsuario(idUsuario);
    }

    public Categoria buscar(int id) {
        return categoriaRepository.findById(id)
                .orElseThrow(() -> new EntidadeNaoEncontradaException("Categoria não encontrada"));
    }

    public Categoria cadastrar(Categoria categoria) {
        validar(categoria);
        if (categoriaRepository.existsByIdUsuarioAndNmCategoriaIgnoreCaseAndTpCategoria(
                categoria.getIdUsuario(), categoria.getNmCategoria(), categoria.getTpCategoria()))
            throw new RegraNegocioException("Já existe uma categoria com esse nome e tipo");
        categoria.setIdCategoria(0);
        return categoriaRepository.save(categoria);
    }

    public Categoria atualizar(int id, Categoria dados) {
        Categoria atual = buscar(id);
        validar(dados);
        if (categoriaRepository.existsByIdUsuarioAndNmCategoriaIgnoreCaseAndTpCategoriaAndIdCategoriaNot(
                dados.getIdUsuario(), dados.getNmCategoria(), dados.getTpCategoria(), id))
            throw new RegraNegocioException("Já existe uma categoria com esse nome e tipo");
        if (!atual.getTpCategoria().equals(dados.getTpCategoria()) && transacaoRepository.existsByIdCategoria(id))
            throw new RegraNegocioException("Categoria já possui transações e não pode mudar de tipo");
        atual.setNmCategoria(dados.getNmCategoria());
        atual.setTpCategoria(dados.getTpCategoria());
        atual.setIdUsuario(dados.getIdUsuario());
        atual.setVlLimite(dados.getVlLimite());
        atual.setCdCor(dados.getCdCor());
        return categoriaRepository.save(atual);
    }

    public void deletar(int id) {
        buscar(id);
        if (transacaoRepository.existsByIdCategoria(id))
            throw new RegraNegocioException("Categoria possui transações e não pode ser removida");
        categoriaRepository.deleteById(id);
    }

    private void validar(Categoria categoria) {
        String tipo = categoria.getTpCategoria().toUpperCase();
        if (!tipo.equals("RECEITA") && !tipo.equals("DESPESA"))
            throw new RegraNegocioException("Tipo da categoria deve ser RECEITA ou DESPESA");
        categoria.setTpCategoria(tipo);
        if (categoria.getVlLimite() != null) {
            if (categoria.getVlLimite().compareTo(BigDecimal.ZERO) <= 0)
                throw new RegraNegocioException("O limite deve ser maior que zero");
            if (!tipo.equals("DESPESA"))
                throw new RegraNegocioException("Só categorias de despesa têm limite");
        }
        if (categoria.getCdCor() != null && categoria.getCdCor().isBlank()) categoria.setCdCor(null);
        if (categoria.getCdCor() != null && !categoria.getCdCor().matches("#[0-9a-fA-F]{6}"))
            throw new RegraNegocioException("Cor inválida: use o formato #RRGGBB");
        if (!usuarioRepository.existsById(categoria.getIdUsuario()))
            throw new RegraNegocioException("Usuário informado não existe");
    }
}
