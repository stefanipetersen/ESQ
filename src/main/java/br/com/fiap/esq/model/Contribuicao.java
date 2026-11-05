package br.com.fiap.esq.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "t_pfe_contribuicao")
public class Contribuicao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_contribuicao")
    private int idContribuicao;

    @NotNull(message = "Valor é obrigatório")
    @Column(name = "vl_contribuicao")
    private BigDecimal vlContribuicao;

    @Column(name = "dt_contribuicao")
    private LocalDate dtContribuicao;

    @Column(name = "id_meta")
    private int idMeta;

    public Contribuicao() {}

    public int getIdContribuicao() {
        return idContribuicao;
    }

    public void setIdContribuicao(int idContribuicao) {
        this.idContribuicao = idContribuicao;
    }

    public BigDecimal getVlContribuicao() {
        return vlContribuicao;
    }

    public void setVlContribuicao(BigDecimal vlContribuicao) {
        this.vlContribuicao = vlContribuicao;
    }

    public LocalDate getDtContribuicao() {
        return dtContribuicao;
    }

    public void setDtContribuicao(LocalDate dtContribuicao) {
        this.dtContribuicao = dtContribuicao;
    }

    public int getIdMeta() {
        return idMeta;
    }

    public void setIdMeta(int idMeta) {
        this.idMeta = idMeta;
    }
}
