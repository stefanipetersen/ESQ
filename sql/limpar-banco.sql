-- CUIDADO: apaga as tabelas do ESQ e TODOS os dados delas. Use so para recomecar do zero:
-- rode este script e depois o sql/schema.sql.
-- Nao mexe nas tabelas t_pfe_categoria e t_pfe_transacao (sem _esq), que pertencem a outro modelo do schema.
-- Se aparecer ORA-00942 (tabela nao existe) em algum comando, pode ignorar e seguir.

DROP TABLE t_pfe_planejamento;   -- so existe em bancos criados por uma versao anterior
DROP TABLE t_pfe_transferencia;
DROP TABLE t_pfe_contribuicao;
DROP TABLE t_pfe_meta;
DROP TABLE t_pfe_configuracao;
DROP TABLE t_pfe_transacao_esq;
DROP TABLE t_pfe_categoria_esq;
DROP TABLE t_pfe_investimento;
DROP TABLE t_pfe_conta;
DROP TABLE t_pfe_usuario;
