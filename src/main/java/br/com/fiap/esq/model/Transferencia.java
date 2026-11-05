package br.com.fiap.esq.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDate;

//Dinheiro que sai de uma conta e entra em outra do mesmo usuário (não conta como receita nem despesa)
@Entity
@Table(name = "t_pfe_transferencia")
public class Transferencia {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_transferencia")
    private int idTransferencia;

    @Column(name = "id_conta_origem")
    private int idContaOrigem;

    @Column(name = "id_conta_destino")
    private int idContaDestino;

    @NotNull(message = "Valor é obrigatório")
    @Column(name = "vl_transferencia")
    private BigDecimal vlTransferencia;

    @Column(name = "dt_transferencia")
    private LocalDate dtTransferencia;

    @Column(name = "ds_transferencia")
    private String dsTransferencia;

    @Column(name = "id_usuario")
    private int idUsuario;

    public Transferencia() {}

    public int getIdTransferencia() {
        return idTransferencia;
    }

    public void setIdTransferencia(int idTransferencia) {
        this.idTransferencia = idTransferencia;
    }

    public int getIdContaOrigem() {
        return idContaOrigem;
    }

    public void setIdContaOrigem(int idContaOrigem) {
        this.idContaOrigem = idContaOrigem;
    }

    public int getIdContaDestino() {
        return idContaDestino;
    }

    public void setIdContaDestino(int idContaDestino) {
        this.idContaDestino = idContaDestino;
    }

    public BigDecimal getVlTransferencia() {
        return vlTransferencia;
    }

    public void setVlTransferencia(BigDecimal vlTransferencia) {
        this.vlTransferencia = vlTransferencia;
    }

    public LocalDate getDtTransferencia() {
        return dtTransferencia;
    }

    public void setDtTransferencia(LocalDate dtTransferencia) {
        this.dtTransferencia = dtTransferencia;
    }

    public String getDsTransferencia() {
        return dsTransferencia;
    }

    public void setDsTransferencia(String dsTransferencia) {
        this.dsTransferencia = dsTransferencia;
    }

    public int getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(int idUsuario) {
        this.idUsuario = idUsuario;
    }
}
