package br.com.fiap.esq.repository;

import br.com.fiap.esq.model.Investimento;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InvestimentoRepository extends JpaRepository<Investimento, Integer> {

    boolean existsByIdUsuario(int idUsuario);
}
