package br.com.fiap.esq.repository;

import br.com.fiap.esq.model.Categoria;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CategoriaRepository extends JpaRepository<Categoria, Integer> {

    List<Categoria> findByIdUsuario(int idUsuario);

    boolean existsByIdUsuarioAndNmCategoriaIgnoreCaseAndTpCategoria(int idUsuario, String nmCategoria, String tpCategoria);

    boolean existsByIdUsuarioAndNmCategoriaIgnoreCaseAndTpCategoriaAndIdCategoriaNot(
            int idUsuario, String nmCategoria, String tpCategoria, int idCategoria);
}
