package br.com.fiap.esq.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;

//Preferências de cada usuário (uma por usuário)
@Entity
@Table(name = "t_pfe_configuracao")
public class Configuracao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_configuracao")
    private int idConfiguracao;

    @NotBlank(message = "Moeda é obrigatória")
    @Column(name = "cd_moeda")
    private String cdMoeda;

    @NotBlank(message = "Formato de data é obrigatório")
    @Column(name = "cd_formato_data")
    private String cdFormatoData;

    @Column(name = "fl_tema_claro")
    private boolean flTemaClaro;

    @Column(name = "fl_resumo_email")
    private boolean flResumoEmail;

    @Column(name = "fl_dicas")
    private boolean flDicas;

    @Column(name = "id_usuario")
    private int idUsuario;

    public Configuracao() {}

    public int getIdConfiguracao() {
        return idConfiguracao;
    }

    public void setIdConfiguracao(int idConfiguracao) {
        this.idConfiguracao = idConfiguracao;
    }

    public String getCdMoeda() {
        return cdMoeda;
    }

    public void setCdMoeda(String cdMoeda) {
        this.cdMoeda = cdMoeda;
    }

    public String getCdFormatoData() {
        return cdFormatoData;
    }

    public void setCdFormatoData(String cdFormatoData) {
        this.cdFormatoData = cdFormatoData;
    }

    public boolean isFlTemaClaro() {
        return flTemaClaro;
    }

    public void setFlTemaClaro(boolean flTemaClaro) {
        this.flTemaClaro = flTemaClaro;
    }

    public boolean isFlResumoEmail() {
        return flResumoEmail;
    }

    public void setFlResumoEmail(boolean flResumoEmail) {
        this.flResumoEmail = flResumoEmail;
    }

    public boolean isFlDicas() {
        return flDicas;
    }

    public void setFlDicas(boolean flDicas) {
        this.flDicas = flDicas;
    }

    public int getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(int idUsuario) {
        this.idUsuario = idUsuario;
    }
}
