-- Tabelas do Fintech na instancia Oracle da FIAP

CREATE TABLE t_pfe_usuario (
    id_usuario  NUMBER GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    nm_usuario  VARCHAR2(100) NOT NULL,
    ds_email    VARCHAR2(150) NOT NULL,
    hash_senha  VARCHAR2(255) NOT NULL,
    dt_cadastro DATE          NOT NULL,
    CONSTRAINT uk_pfe_usuario_email UNIQUE (ds_email)
);

CREATE TABLE t_pfe_conta (
    id_conta       NUMBER GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    nm_conta       VARCHAR2(100) NOT NULL,
    tp_conta       VARCHAR2(30)  NOT NULL,
    vl_saldo_conta NUMBER(14,2)  NOT NULL,
    id_usuario     NUMBER        NOT NULL,
    CONSTRAINT fk_pfe_conta_usuario FOREIGN KEY (id_usuario) REFERENCES t_pfe_usuario (id_usuario)
);

CREATE TABLE t_pfe_investimento (
    id_investimento         NUMBER GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    str_tipo                VARCHAR2(50) NOT NULL,
    vl_inicial_investimento NUMBER(14,2) NOT NULL,
    vl_rendimento           NUMBER(14,2) NOT NULL,
    dt_aplicacao            DATE         NOT NULL,
    id_usuario              NUMBER       NOT NULL,
    CONSTRAINT fk_pfe_invest_usuario FOREIGN KEY (id_usuario) REFERENCES t_pfe_usuario (id_usuario)
);
