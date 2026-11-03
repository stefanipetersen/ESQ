package br.com.fiap.esq.repository;

import br.com.fiap.esq.model.Conta;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ContaRepository extends JpaRepository<Conta, Integer> {

    boolean existsByIdUsuario(int idUsuario);
}
