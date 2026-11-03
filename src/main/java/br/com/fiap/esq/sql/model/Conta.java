package br.com.fiap.esq.sql.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

@Entity
@Table(name = "t_pfe_conta")
public class Conta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_conta")
    private int idConta;

    @NotBlank(message = "Nome da conta é obrigatório")
    @Column(name = "nm_conta")
    private String nmConta;

    @NotBlank(message = "Tipo da conta é obrigatório")
    @Column(name = "tp_conta")
    private String tpConta;

    @NotNull(message = "Saldo é obrigatório")
    @Column(name = "vl_saldo_conta")
    private BigDecimal vlSaldoConta;

    @Column(name = "id_usuario")
    private int idUsuario;

    public Conta() {}

    public Conta(int idConta, String nmConta, String tpConta, BigDecimal vlSaldoConta, int idUsuario) {
        this.idConta = idConta;
        this.nmConta = nmConta;
        this.tpConta = tpConta;
        this.vlSaldoConta = vlSaldoConta;
        this.idUsuario = idUsuario;
    }

    //Quando cadastrar não precisa criar o código, ele cria sozinho
    public Conta(String nmConta, String tpConta, BigDecimal vlSaldoConta, int idUsuario) {
        this.nmConta = nmConta;
        this.tpConta = tpConta;
        this.vlSaldoConta = vlSaldoConta;
        this.idUsuario = idUsuario;
    }

    @Override
    public String toString() {
        return "Conta{" +
                "idConta=" + idConta +
                ", nmConta='" + nmConta + '\'' +
                ", tpConta='" + tpConta + '\'' +
                ", vlSaldoConta=" + vlSaldoConta +
                ", idUsuario=" + idUsuario +
                '}';
    }

    public int getIdConta() {
        return idConta;
    }

    public void setIdConta(int idConta) {
        this.idConta = idConta;
    }

    public String getNmConta() {
        return nmConta;
    }

    public void setNmConta(String nmConta) {
        this.nmConta = nmConta;
    }

    public String getTpConta() {
        return tpConta;
    }

    public void setTpConta(String tpConta) {
        this.tpConta = tpConta;
    }

    public BigDecimal getVlSaldoConta() {
        return vlSaldoConta;
    }

    public void setVlSaldoConta(BigDecimal vlSaldoConta) {
        this.vlSaldoConta = vlSaldoConta;
    }

    public int getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(int idUsuario) {
        this.idUsuario = idUsuario;
    }
}
