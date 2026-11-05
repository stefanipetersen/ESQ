package br.com.fiap.esq.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;

import java.math.BigDecimal;

@Entity
@Table(name = "t_pfe_categoria_esq")
public class Categoria {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_categoria")
    private int idCategoria;

    @NotBlank(message = "Nome da categoria é obrigatório")
    @Column(name = "nm_categoria")
    private String nmCategoria;

    //RECEITA ou DESPESA
    @NotBlank(message = "Tipo da categoria é obrigatório")
    @Column(name = "tp_categoria")
    private String tpCategoria;

    @Column(name = "id_usuario")
    private int idUsuario;

    //Limite de gasto por mês (só despesa); gera alerta no Início quando chega perto
    @Column(name = "vl_limite")
    private BigDecimal vlLimite;

    //Cor da categoria nos gráficos, no formato #RRGGBB
    @Column(name = "cd_cor")
    private String cdCor;

    public Categoria() {}

    public Categoria(String nmCategoria, String tpCategoria, int idUsuario) {
        this.nmCategoria = nmCategoria;
        this.tpCategoria = tpCategoria;
        this.idUsuario = idUsuario;
    }

    public int getIdCategoria() {
        return idCategoria;
    }

    public void setIdCategoria(int idCategoria) {
        this.idCategoria = idCategoria;
    }

    public String getNmCategoria() {
        return nmCategoria;
    }

    public void setNmCategoria(String nmCategoria) {
        this.nmCategoria = nmCategoria;
    }

    public String getTpCategoria() {
        return tpCategoria;
    }

    public void setTpCategoria(String tpCategoria) {
        this.tpCategoria = tpCategoria;
    }

    public int getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(int idUsuario) {
        this.idUsuario = idUsuario;
    }

    public BigDecimal getVlLimite() {
        return vlLimite;
    }

    public void setVlLimite(BigDecimal vlLimite) {
        this.vlLimite = vlLimite;
    }

    public String getCdCor() {
        return cdCor;
    }

    public void setCdCor(String cdCor) {
        this.cdCor = cdCor;
    }
}
