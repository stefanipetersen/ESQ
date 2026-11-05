-- Tabelas do Fintech na instancia Oracle da FIAP
-- Banco novo: rode este script inteiro (Run Script / F5 no SQL Developer). Ele ja inclui tudo.
-- As tabelas de categoria e transacao terminam com _esq porque t_pfe_categoria e t_pfe_transacao
-- ja existem no schema da FIAP com outro formato.

CREATE TABLE t_pfe_usuario (
    id_usuario    NUMBER GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    nm_usuario    VARCHAR2(100) NOT NULL,
    ds_email      VARCHAR2(150) NOT NULL,
    hash_senha    VARCHAR2(255) NOT NULL,
    dt_cadastro   DATE          NOT NULL,
    nr_telefone   VARCHAR2(20),
    dt_nascimento DATE,
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
    pc_rendimento           NUMBER(14,2) NOT NULL,
    dt_aplicacao            DATE         NOT NULL,
    id_usuario              NUMBER       NOT NULL,
    CONSTRAINT fk_pfe_invest_usuario FOREIGN KEY (id_usuario) REFERENCES t_pfe_usuario (id_usuario)
);

CREATE TABLE t_pfe_categoria_esq (
    id_categoria NUMBER GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    nm_categoria VARCHAR2(60) NOT NULL,
    tp_categoria VARCHAR2(10) NOT NULL,
    id_usuario   NUMBER       NOT NULL,
    vl_limite    NUMBER(14,2),
    cd_cor       VARCHAR2(7),
    CONSTRAINT ck_pfe_categoria_tipo CHECK (tp_categoria IN ('RECEITA', 'DESPESA')),
    CONSTRAINT fk_pfe_categoria_usuario FOREIGN KEY (id_usuario) REFERENCES t_pfe_usuario (id_usuario) ON DELETE CASCADE
);

CREATE TABLE t_pfe_transacao_esq (
    id_transacao  NUMBER GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    ds_transacao  VARCHAR2(150) NOT NULL,
    vl_transacao  NUMBER(14,2)  NOT NULL,
    tp_transacao  VARCHAR2(10)  NOT NULL,
    dt_transacao  DATE          NOT NULL,
    id_categoria  NUMBER        NOT NULL,
    id_conta      NUMBER        NOT NULL,
    id_usuario    NUMBER        NOT NULL,
    nr_parcela    NUMBER(3)     DEFAULT 1 NOT NULL,
    nr_parcelas   NUMBER(3)     DEFAULT 1 NOT NULL,
    fl_aplicada   NUMBER(1)     DEFAULT 1 NOT NULL,
    fl_fixo       NUMBER(1)     DEFAULT 0 NOT NULL,
    id_grupo      NUMBER,
    CONSTRAINT ck_pfe_transacao_tipo CHECK (tp_transacao IN ('RECEITA', 'DESPESA')),
    CONSTRAINT fk_pfe_transacao_categoria FOREIGN KEY (id_categoria) REFERENCES t_pfe_categoria_esq (id_categoria),
    CONSTRAINT fk_pfe_transacao_conta FOREIGN KEY (id_conta) REFERENCES t_pfe_conta (id_conta),
    CONSTRAINT fk_pfe_transacao_usuario FOREIGN KEY (id_usuario) REFERENCES t_pfe_usuario (id_usuario)
);

CREATE TABLE t_pfe_meta (
    id_meta     NUMBER GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    nm_meta     VARCHAR2(100) NOT NULL,
    ds_meta     VARCHAR2(200),
    vl_objetivo NUMBER(14,2)  NOT NULL,
    vl_atual    NUMBER(14,2)  DEFAULT 0 NOT NULL,
    dt_prazo    DATE          NOT NULL,
    id_usuario  NUMBER        NOT NULL,
    id_conta    NUMBER,
    CONSTRAINT fk_pfe_meta_usuario FOREIGN KEY (id_usuario) REFERENCES t_pfe_usuario (id_usuario) ON DELETE CASCADE,
    CONSTRAINT fk_pfe_meta_conta FOREIGN KEY (id_conta) REFERENCES t_pfe_conta (id_conta)
);

CREATE TABLE t_pfe_contribuicao (
    id_contribuicao NUMBER GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    vl_contribuicao NUMBER(14,2) NOT NULL,
    dt_contribuicao DATE         NOT NULL,
    id_meta         NUMBER       NOT NULL,
    CONSTRAINT fk_pfe_contribuicao_meta FOREIGN KEY (id_meta) REFERENCES t_pfe_meta (id_meta) ON DELETE CASCADE
);

CREATE TABLE t_pfe_configuracao (
    id_configuracao NUMBER GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    cd_moeda        VARCHAR2(3)  NOT NULL,
    cd_formato_data VARCHAR2(10) NOT NULL,
    fl_tema_claro   NUMBER(1)    DEFAULT 1 NOT NULL,
    fl_resumo_email NUMBER(1)    DEFAULT 1 NOT NULL,
    fl_dicas        NUMBER(1)    DEFAULT 1 NOT NULL,
    id_usuario      NUMBER       NOT NULL,
    CONSTRAINT uk_pfe_configuracao_usuario UNIQUE (id_usuario),
    CONSTRAINT fk_pfe_config_usuario FOREIGN KEY (id_usuario) REFERENCES t_pfe_usuario (id_usuario) ON DELETE CASCADE
);

CREATE TABLE t_pfe_transferencia (
    id_transferencia  NUMBER GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    id_conta_origem   NUMBER       NOT NULL,
    id_conta_destino  NUMBER       NOT NULL,
    vl_transferencia  NUMBER(14,2) NOT NULL,
    dt_transferencia  DATE         NOT NULL,
    ds_transferencia  VARCHAR2(150),
    id_usuario        NUMBER       NOT NULL,
    CONSTRAINT fk_pfe_transf_origem  FOREIGN KEY (id_conta_origem)  REFERENCES t_pfe_conta (id_conta),
    CONSTRAINT fk_pfe_transf_destino FOREIGN KEY (id_conta_destino) REFERENCES t_pfe_conta (id_conta),
    CONSTRAINT fk_pfe_transf_usuario FOREIGN KEY (id_usuario)       REFERENCES t_pfe_usuario (id_usuario)
);
