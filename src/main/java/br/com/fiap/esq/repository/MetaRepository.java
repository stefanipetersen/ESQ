package br.com.fiap.esq.repository;

import br.com.fiap.esq.model.Meta;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MetaRepository extends JpaRepository<Meta, Integer> {

    List<Meta> findByIdUsuario(int idUsuario);
}
