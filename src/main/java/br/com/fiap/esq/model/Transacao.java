package br.com.fiap.esq.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "t_pfe_transacao_esq")
public class Transacao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_transacao")
    private int idTransacao;

    @NotBlank(message = "Descrição é obrigatória")
    @Column(name = "ds_transacao")
    private String dsTransacao;

    @NotNull(message = "Valor é obrigatório")
    @Column(name = "vl_transacao")
    private BigDecimal vlTransacao;

    //RECEITA ou DESPESA
    @NotBlank(message = "Tipo da transação é obrigatório")
    @Column(name = "tp_transacao")
    private String tpTransacao;

    @Column(name = "dt_transacao")
    private LocalDate dtTransacao;

    @Column(name = "id_categoria")
    private int idCategoria;

    @Column(name = "id_conta")
    private int idConta;

    @Column(name = "id_usuario")
    private int idUsuario;

    //Parcelamento: número desta parcela e total de parcelas (1 e 1 quando é à vista)
    @Column(name = "nr_parcela")
    private int nrParcela;

    @Column(name = "nr_parcelas")
    private int nrParcelas;

    //Gasto fixo: se repete todo mês com o mesmo valor (Netflix, aluguel...)
    @Column(name = "fl_fixo")
    private boolean flFixo;

    //Liga as ocorrências da mesma compra (parcelas ou gasto fixo); é o id da primeira delas
    @Column(name = "id_grupo")
    private Integer idGrupo;

    //false enquanto a parcela é futura: o saldo da conta só é afetado quando chega a data
    @Column(name = "fl_aplicada")
    private boolean flAplicada;

    public Transacao() {}

    public int getIdTransacao() {
        return idTransacao;
    }

    public void setIdTransacao(int idTransacao) {
        this.idTransacao = idTransacao;
    }

    public String getDsTransacao() {
        return dsTransacao;
    }

    public void setDsTransacao(String dsTransacao) {
        this.dsTransacao = dsTransacao;
    }

    public BigDecimal getVlTransacao() {
        return vlTransacao;
    }

    public void setVlTransacao(BigDecimal vlTransacao) {
        this.vlTransacao = vlTransacao;
    }

    public String getTpTransacao() {
        return tpTransacao;
    }

    public void setTpTransacao(String tpTransacao) {
        this.tpTransacao = tpTransacao;
    }

    public LocalDate getDtTransacao() {
        return dtTransacao;
    }

    public void setDtTransacao(LocalDate dtTransacao) {
        this.dtTransacao = dtTransacao;
    }

    public int getIdCategoria() {
        return idCategoria;
    }

    public void setIdCategoria(int idCategoria) {
        this.idCategoria = idCategoria;
    }

    public int getIdConta() {
        return idConta;
    }

    public void setIdConta(int idConta) {
        this.idConta = idConta;
    }

    public int getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(int idUsuario) {
        this.idUsuario = idUsuario;
    }

    public int getNrParcela() {
        return nrParcela;
    }

    public void setNrParcela(int nrParcela) {
        this.nrParcela = nrParcela;
    }

    public int getNrParcelas() {
        return nrParcelas;
    }

    public void setNrParcelas(int nrParcelas) {
        this.nrParcelas = nrParcelas;
    }

    public boolean isFlFixo() {
        return flFixo;
    }

    public void setFlFixo(boolean flFixo) {
        this.flFixo = flFixo;
    }

    public Integer getIdGrupo() {
        return idGrupo;
    }

    public void setIdGrupo(Integer idGrupo) {
        this.idGrupo = idGrupo;
    }

    public boolean isFlAplicada() {
        return flAplicada;
    }

    public void setFlAplicada(boolean flAplicada) {
        this.flAplicada = flAplicada;
    }
}
