package br.com.fiap.esq.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "t_pfe_meta")
public class Meta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_meta")
    private int idMeta;

    @NotBlank(message = "Nome da meta é obrigatório")
    @Column(name = "nm_meta")
    private String nmMeta;

    @Column(name = "ds_meta")
    private String dsMeta;

    @NotNull(message = "Valor do objetivo é obrigatório")
    @Column(name = "vl_objetivo")
    private BigDecimal vlObjetivo;

    //Só muda por meio das contribuições
    @Column(name = "vl_atual")
    private BigDecimal vlAtual;

    @NotNull(message = "Prazo é obrigatório")
    @Column(name = "dt_prazo")
    private LocalDate dtPrazo;

    @Column(name = "id_usuario")
    private int idUsuario;

    //Conta de onde saem os aportes (opcional); sem ela a meta é só um controle
    @Column(name = "id_conta")
    private Integer idConta;

    public Meta() {}

    public int getIdMeta() {
        return idMeta;
    }

    public void setIdMeta(int idMeta) {
        this.idMeta = idMeta;
    }

    public String getNmMeta() {
        return nmMeta;
    }

    public void setNmMeta(String nmMeta) {
        this.nmMeta = nmMeta;
    }

    public String getDsMeta() {
        return dsMeta;
    }

    public void setDsMeta(String dsMeta) {
        this.dsMeta = dsMeta;
    }

    public BigDecimal getVlObjetivo() {
        return vlObjetivo;
    }

    public void setVlObjetivo(BigDecimal vlObjetivo) {
        this.vlObjetivo = vlObjetivo;
    }

    public BigDecimal getVlAtual() {
        return vlAtual;
    }

    public void setVlAtual(BigDecimal vlAtual) {
        this.vlAtual = vlAtual;
    }

    public LocalDate getDtPrazo() {
        return dtPrazo;
    }

    public void setDtPrazo(LocalDate dtPrazo) {
        this.dtPrazo = dtPrazo;
    }

    public int getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(int idUsuario) {
        this.idUsuario = idUsuario;
    }

    public Integer getIdConta() {
        return idConta;
    }

    public void setIdConta(Integer idConta) {
        this.idConta = idConta;
    }
}
