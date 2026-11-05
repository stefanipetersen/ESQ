package br.com.fiap.esq.repository;

import br.com.fiap.esq.model.Transacao;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface TransacaoRepository extends JpaRepository<Transacao, Integer> {

    List<Transacao> findByIdUsuarioOrderByDtTransacaoDescIdTransacaoDesc(int idUsuario);

    List<Transacao> findByIdUsuarioAndDtTransacaoBetween(int idUsuario, LocalDate inicio, LocalDate fim);

    //Parcelas futuras que já chegaram na data e ainda não afetaram o saldo
    List<Transacao> findByFlAplicadaFalseAndDtTransacaoLessThanEqual(LocalDate data);

    //Esta ocorrência e as seguintes da mesma compra (parcelas ou gasto fixo)
    List<Transacao> findByIdGrupoAndDtTransacaoGreaterThanEqual(Integer idGrupo, LocalDate data);

    Optional<Transacao> findFirstByIdGrupoOrderByDtTransacaoDesc(Integer idGrupo);

    //Para cada gasto fixo, a data da última ocorrência já criada
    @Query("select t.idGrupo, max(t.dtTransacao) from Transacao t where t.flFixo = true group by t.idGrupo")
    List<Object[]> ultimasDosGastosFixos();

    boolean existsByIdCategoria(int idCategoria);

    boolean existsByIdConta(int idConta);
}
