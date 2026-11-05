package br.com.fiap.esq.repository;

import br.com.fiap.esq.model.Contribuicao;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ContribuicaoRepository extends JpaRepository<Contribuicao, Integer> {

    List<Contribuicao> findByIdMetaOrderByDtContribuicaoDescIdContribuicaoDesc(int idMeta);

    //Contribuições de todas as metas de um usuário
    @Query("select c from Contribuicao c, Meta m where c.idMeta = m.idMeta and m.idUsuario = :idUsuario "
            + "order by c.dtContribuicao desc, c.idContribuicao desc")
    List<Contribuicao> listarDoUsuario(@Param("idUsuario") int idUsuario);
}
