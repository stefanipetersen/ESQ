package br.com.fiap.esq.sql.repository;

import br.com.fiap.esq.sql.model.Investimento;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InvestimentoRepository extends JpaRepository<Investimento, Integer> {

    boolean existsByIdUsuario(int idUsuario);
}
