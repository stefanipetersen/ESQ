package br.com.fiap.esq.repository;

import br.com.fiap.esq.model.Transferencia;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TransferenciaRepository extends JpaRepository<Transferencia, Integer> {

    List<Transferencia> findByIdUsuarioOrderByDtTransferenciaDescIdTransferenciaDesc(int idUsuario);

    boolean existsByIdContaOrigemOrIdContaDestino(int idContaOrigem, int idContaDestino);
}
