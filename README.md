# ESQ — API do Fintech (Spring Boot + JPA + Oracle FIAP)

API REST do **ESQ**, um controle financeiro pessoal: contas, transferências entre contas, investimentos, transações (receitas e gastos, inclusive parcelados e fixos), categorias com limite mensal, metas, relatórios e preferências do usuário.

Este repositório é o **backend**. O frontend (Next.js) fica em outro repositório: <https://github.com/stefanipetersen/projeto_fintech_ESQ>.

## Início rápido

1. Tenha o **JDK 21** e o **IntelliJ** instalados.
2. No Oracle da FIAP, rode o arquivo [`sql/schema.sql`](sql/schema.sql) (seção 3.3).
3. No IntelliJ, defina as variáveis `DB_USER` e `DB_PASSWORD` (seção 3.2).
4. Rode a classe `EsqApplication` e espere o `Started EsqApplication`.
5. Confira em <http://localhost:8080/usuarios>. Depois, suba o frontend.

## Sumário
1. [Visão geral](#1-visão-geral)
2. [Pré-requisitos](#2-pré-requisitos)
3. [Banco de dados Oracle (FIAP)](#3-banco-de-dados-oracle-fiap)
4. [Rodando a API](#4-rodando-a-api)
5. [Estrutura do projeto](#5-estrutura-do-projeto)
6. [Endpoints](#6-endpoints)
7. [Regras de negócio](#7-regras-de-negócio)
8. [Exemplos de requisição](#8-exemplos-de-requisição)
9. [Tarefas automáticas](#9-tarefas-automáticas)
10. [Problemas comuns](#10-problemas-comuns)
11. [Requisitos da disciplina](#11-requisitos-da-disciplina)
12. [Limitações conhecidas](#12-limitações-conhecidas)

---

## 1. Visão geral

| Item | Versão / detalhe |
|---|---|
| Linguagem | Java 21 |
| Framework | Spring Boot 3.4.5 (Web, Data JPA, Validation) |
| Banco | Oracle da FIAP, via driver `ojdbc17` |
| Build | Maven |
| Porta | `8080` |

O código segue as camadas **Model → Repository → Service → Controller**, repetidas para cada entidade:

- **Model**: entidade JPA que representa uma tabela.
- **Repository**: `JpaRepository`, acesso ao banco.
- **Service**: regras de negócio (lança erros de regra, de "não encontrado" etc.).
- **Controller**: `@RestController` com GET, POST, PUT e DELETE e o código HTTP correto para cada situação.

Os erros são traduzidos em um só lugar (`exception/TratadorErro`), sempre no formato `{"erro": "mensagem"}` (ou `{"campo": "mensagem"}` nos erros de validação).

---

## 2. Pré-requisitos

- **JDK 21** (o IntelliJ pode baixar sozinho em *File > Project Structure > SDKs*).
- **IntelliJ IDEA** (recomendado) ou Maven instalado.
- Acesso ao **Oracle da FIAP** com o seu RM e senha (usuário `rm...`).
- Um cliente SQL para criar as tabelas, como o **Oracle SQL Developer**.

---

## 3. Banco de dados Oracle (FIAP)

### 3.1 Dados de conexão

| Campo | Valor |
|---|---|
| Host | `oracle.fiap.com.br` |
| Porta | `1521` |
| SID | `orcl` |
| URL JDBC | `jdbc:oracle:thin:@oracle.fiap.com.br:1521:orcl` |
| Usuário | o seu RM (ex.: `rm572254`) |
| Senha | a sua senha do Oracle da FIAP |

A URL fica em `src/main/resources/application.properties`. **A senha nunca deve ir para o Git**: ela é lida de variáveis de ambiente.

### 3.2 Variáveis de ambiente

| Variável | Para que serve | Padrão |
|---|---|---|
| `DB_USER` | usuário do Oracle | `rm572254` |
| `DB_PASSWORD` | senha do Oracle | *(sem padrão: obrigatória)* |

**No IntelliJ** (jeito mais simples): *Run > Edit Configurations > EsqApplication > Environment variables* e coloque
`DB_USER=seu_rm;DB_PASSWORD=sua_senha`.

**Pelo terminal (PowerShell)**, antes de rodar com Maven:

```powershell
$env:DB_USER = "seu_rm"
$env:DB_PASSWORD = "sua_senha"
```

### 3.3 Criando as tabelas (primeira vez)

1. Abra o **SQL Developer** e crie uma conexão com os dados acima (nome da conexão livre, tipo *SID* = `orcl`).
2. Abra o arquivo [`sql/schema.sql`](sql/schema.sql).
3. Execute o arquivo inteiro com **Run Script (F5)** (não use o botão de executar uma instrução só).
4. Confira em *Tabelas* se apareceram as 9 tabelas da seção 3.4.

O `schema.sql` cria tudo de uma vez. O Hibernate **não** cria nem altera tabelas (`ddl-auto=none`): o banco é sempre criado pelos scripts da pasta `sql/`.

### 3.4 Tabelas

Todas as tabelas começam com `t_pfe_` e usam chave primária automática (`GENERATED ALWAYS AS IDENTITY`).

| Tabela | O que guarda | Colunas principais |
|---|---|---|
| `t_pfe_usuario` | quem usa o sistema | `nm_usuario`, `ds_email` (único), `hash_senha`, `dt_cadastro`, `nr_telefone`, `dt_nascimento` |
| `t_pfe_conta` | contas do usuário | `nm_conta`, `tp_conta`, `vl_saldo_conta` |
| `t_pfe_investimento` | aplicações (o rendimento é um **percentual**, ex.: `10.5` = 10,5%) | `str_tipo`, `vl_inicial_investimento`, `pc_rendimento` (percentual), `dt_aplicacao` |
| `t_pfe_categoria_esq` | categorias de receita e de gasto | `nm_categoria`, `tp_categoria` (`RECEITA` ou `DESPESA`), `vl_limite` (limite mensal, só despesa), `cd_cor` (`#RRGGBB`) |
| `t_pfe_transacao_esq` | receitas e gastos | `ds_transacao`, `vl_transacao`, `tp_transacao`, `dt_transacao`, `nr_parcela`, `nr_parcelas`, `fl_aplicada`, `fl_fixo`, `id_grupo` |
| `t_pfe_meta` | metas de economia | `nm_meta`, `ds_meta`, `vl_objetivo`, `vl_atual`, `dt_prazo`, `id_conta` (conta de onde saem os aportes, opcional) |
| `t_pfe_contribuicao` | valores guardados em cada meta | `vl_contribuicao`, `dt_contribuicao` |
| `t_pfe_transferencia` | dinheiro movido entre contas do mesmo usuário | `id_conta_origem`, `id_conta_destino`, `vl_transferencia`, `dt_transferencia`, `ds_transferencia` |
| `t_pfe_configuracao` | preferências do usuário (uma por usuário) | `cd_moeda`, `cd_formato_data`, `fl_tema_claro`, `fl_resumo_email`, `fl_dicas` |

> **Por que `_esq` em categoria e transação?** O schema da FIAP já tinha tabelas `t_pfe_categoria` e `t_pfe_transacao` de outro modelo (sem usuário). Para não mexer nelas, as do ESQ ganharam o sufixo `_esq`.

### 3.5 Relacionamentos

```
t_pfe_usuario 1 ──< t_pfe_conta 1 ──< t_pfe_transacao_esq >── 1 t_pfe_categoria_esq
      │                  │ └──< t_pfe_transferencia (conta de origem e de destino)
      │                  └──< t_pfe_meta 1 ──< t_pfe_contribuicao   (a conta da meta é opcional)
      ├──< t_pfe_investimento
      └──1 t_pfe_configuracao

Todas as tabelas acima também guardam o id do usuário dono do registro.
```

- Excluir um usuário apaga em cascata as categorias, metas (e suas contribuições) e a configuração dele. Contas, investimentos e transações **não** são apagados em cascata: a API recusa remover um usuário que ainda tenha contas ou investimentos.
- Uma conta com transações ou transferências não pode ser removida, e uma categoria com transações também não.

### 3.6 Colunas de controle da transação

| Coluna | Significado |
|---|---|
| `nr_parcela` / `nr_parcelas` | parcela atual e total (`1` e `1` quando é à vista) |
| `fl_aplicada` | `1` se a transação já mexeu no saldo da conta; `0` enquanto é futura (parcela ou gasto fixo de meses seguintes) |
| `fl_fixo` | `1` quando se repete todo mês (gasto como Netflix e aluguel, ou receita como salário) |
| `id_grupo` | liga as parcelas ou os meses do mesmo gasto (é o id da primeira ocorrência) |

### 3.7 Scripts do banco

| Arquivo | Quando usar |
|---|---|
| [`sql/schema.sql`](sql/schema.sql) | **Banco novo.** Cria tudo. |
| [`sql/limpar-banco.sql`](sql/limpar-banco.sql) | Recomeçar do zero: apaga as tabelas do ESQ **e os dados**. Depois rode o `schema.sql`. |

---

## 4. Rodando a API

### 4.1 Pelo IntelliJ (recomendado)

1. Abra a pasta do projeto no IntelliJ e aguarde o Maven baixar as dependências (se precisar: aba **Maven > Reload All Maven Projects**).
2. Configure `DB_USER` e `DB_PASSWORD` (seção 3.2).
3. Abra `src/main/java/br/com/fiap/esq/EsqApplication.java` e clique no triângulo verde ao lado do `main` (**Run 'EsqApplication'**).
4. Quando aparecer `Started EsqApplication` no console, a API está no ar em **<http://localhost:8080>**.

Depois de mudar o código, pare (botão quadrado vermelho) e rode de novo: o IntelliJ não recarrega sozinho.

### 4.2 Pelo Maven

```powershell
mvn spring-boot:run
```

(o Maven precisa estar instalado e no `PATH`; o IntelliJ traz um Maven próprio, mas o jeito acima é mais simples).

### 4.3 Conferindo que funcionou

Abra no navegador <http://localhost:8080/usuarios>: deve aparecer uma lista JSON (vazia `[]` ou com usuários).

Para usar a interface, suba também o frontend (veja o README do repositório do frontend). O CORS já está liberado para `http://localhost:3000`.

---

## 5. Estrutura do projeto

```
sql/                              scripts do Oracle (criação das tabelas e limpeza)
src/main/resources/
  application.properties          conexão com o Oracle e configurações do JPA
src/main/java/br/com/fiap/esq/
  EsqApplication.java             ponto de entrada (liga também as tarefas agendadas)
  config/CorsConfig.java          libera o frontend (localhost:3000)
  model/                          entidades JPA (uma por tabela)
  repository/                     interfaces JpaRepository
  service/                        regras de negócio
  controller/                     endpoints REST
  dto/                            corpos especiais de requisição/resposta (login, perfil, senha, relatório)
  exception/                      erros de regra de negócio e o tratador que vira JSON com o código HTTP certo
```

---

## 6. Endpoints

Todas as rotas respondem em `http://localhost:8080`. Troque `{id}` pelo número do registro.

### 6.1 CRUD de cada recurso

| Recurso | Rota base | Observações |
|---|---|---|
| Usuários | `/usuarios` | |
| Contas | `/contas` | |
| Investimentos | `/investimentos` | |
| Categorias | `/categorias` | `GET ?idUsuario=` filtra pelas do usuário |
| Transações | `/transacoes` | `GET ?idUsuario=&tipo=RECEITA\|DESPESA`; `DELETE /{id}?proximas=true` apaga também as parcelas / meses seguintes; `PUT /{id}?proximas=true` muda também o valor, a categoria e a conta dos meses seguintes |
| Metas | `/metas` | `GET ?idUsuario=`; com `idConta`, cada contribuição sai do saldo da conta |
| Transferências | `/transferencias` | `GET ?idUsuario=` |
| Contribuições | `/contribuicoes` | `GET ?idMeta=` ou `?idUsuario=` |
| Configurações | `/configuracoes` | `GET ?idUsuario=` |

| Verbo | Rota | O que faz | Sucesso | Erros |
|---|---|---|---|---|
| GET | rota base | Lista | `200` | |
| GET | rota/`{id}` | Busca um | `200` | `404` |
| POST | rota base | Cria (JSON no corpo) | `201` + header `Location` | `400` validação, `422` regra de negócio |
| PUT | rota/`{id}` | Atualiza (JSON no corpo) | `200` | `400`, `404`, `422` |
| DELETE | rota/`{id}` | Remove e devolve `{"mensagem": "... deletado com sucesso"}` | `200` | `404`, `422` |

### 6.2 Rotas extras

| Verbo | Rota | O que faz |
|---|---|---|
| POST | `/auth/login` | Login. Corpo: `{"dsEmail": "...", "senha": "..."}`. Devolve o usuário (`200`), `401` se errar e `400` se faltar campo. |
| PUT | `/usuarios/{id}/perfil` | Edita nome, e-mail, telefone e data de nascimento sem mexer na senha. |
| PUT | `/usuarios/{id}/senha` | Troca a senha. Corpo: `{"senhaAtual": "...", "novaSenha": "..."}`. `422` se a atual estiver errada. |
| GET | `/relatorios/resumo?idUsuario=1&mes=11&ano=2026` | Dados prontos para o dashboard (veja abaixo). `mes` e `ano` são opcionais (padrão: mês atual). |

**Resposta de `/relatorios/resumo`**: saldo disponível e variação no mês, total de receitas, total de despesas, economia e percentual, gastos por categoria, fluxo de caixa dia a dia, total guardado nas metas e evolução das contribuições nos últimos 6 meses.

### 6.3 Formato dos erros

| Código | Quando | Corpo |
|---|---|---|
| `400` | campo obrigatório vazio ou inválido | `{"nmCategoria": "Nome da categoria é obrigatório"}` |
| `401` | login com e-mail ou senha errados | `{"erro": "E-mail ou senha inválidos"}` |
| `404` | registro não existe | `{"erro": "Conta não encontrada"}` |
| `422` | regra de negócio violada | `{"erro": "Saldo insuficiente na conta Conta Corrente"}` |

---

## 7. Regras de negócio

**Usuário**
- E-mail único; a senha é gravada como foi informada e **nunca aparece nas respostas** da API.
- Não pode ser removido se tiver contas ou investimentos.

**Conta**
- Saldo não negativo; o usuário precisa existir.
- Não pode ser removida se tiver transações.

**Investimento**
- Valor inicial maior que zero; rendimento em **porcentagem** (`pcRendimento`, ex.: `10.5` = 10,5%); data de aplicação não futura (se omitida, vale hoje); usuário precisa existir.

**Categoria**
- Tipo `RECEITA` ou `DESPESA`; nome único por usuário e tipo.
- Limite mensal opcional (só despesa, maior que zero) e cor opcional no formato `#RRGGBB`.
- Não pode ser removida se tiver transações, nem mudar de tipo depois de usada.

**Transação**
- Valor maior que zero; tipo `RECEITA` ou `DESPESA`; data não futura (se omitida, vale hoje).
- A conta e a categoria precisam ser do mesmo usuário, e a categoria deve ter o **mesmo tipo** da transação.
- **Mexe no saldo da conta**: receita soma, despesa subtrai. Editar ou excluir desfaz o efeito. Despesa que deixaria o saldo negativo é recusada (`422`).
- **Gasto parcelado** (`nrParcelas` de 2 a 60, só despesas): o valor informado é o **total**; é dividido em uma parcela por mês a partir da data informada (os centavos que sobram ficam na primeira). As descrições ganham "(1/10)", "(2/10)"...
- **Gasto ou receita fixa** (`flFixo = true`, ex.: Netflix ou salário): o **mesmo valor** se repete todo mês. Ficam lançados os próximos 12 meses e o sistema renova sozinho (seção 9).
- Um gasto não pode ser parcelado e fixo ao mesmo tempo.
- Parcelas e meses futuros só descontam do saldo **quando a data chega** (`fl_aplicada`).
- Editar ou excluir mexe **só naquela ocorrência**. Para encerrar um gasto fixo (ou as parcelas restantes), use `DELETE /transacoes/{id}?proximas=true`.

**Meta**
- Objetivo maior que zero; prazo não pode estar no passado (ao criar).
- O valor guardado (`vlAtual`) **só muda pelas contribuições**; o objetivo não pode ficar menor que o já guardado.

**Transferência**
- Valor maior que zero; contas de origem e destino diferentes e do mesmo usuário; data não futura; saldo da origem precisa cobrir. Move o saldo entre as contas e **não conta como receita nem despesa**.
- Uma conta com transferências não pode ser removida.

**Contribuição**
- Valor maior que zero; data não futura (padrão hoje); a meta precisa existir.
- Soma na meta e **não pode ultrapassar o objetivo**. Se a meta tem conta, o valor é descontado do saldo dela (e devolvido ao excluir a contribuição ou a meta); a conta da meta não troca depois que há valor guardado. Não dá para trocar a meta de uma contribuição.

**Configuração**
- Uma por usuário; moeda `BRL`, `USD` ou `EUR`; formato de data `DD/MM/AAAA`, `MM/DD/AAAA` ou `AAAA-MM-DD`.

---

## 8. Exemplos de requisição

Ordem sugerida para testar (no Postman ou no `curl`): usuário, conta, categoria, transação.

```bash
# 1) criar usuário
curl -X POST http://localhost:8080/usuarios -H "Content-Type: application/json" \
  -d '{"nmUsuario":"Maria Silva","dsEmail":"maria@email.com","hashSenha":"minhasenha123"}'

# 2) criar conta (use o idUsuario devolvido)
curl -X POST http://localhost:8080/contas -H "Content-Type: application/json" \
  -d '{"nmConta":"Conta Corrente","tpConta":"CORRENTE","vlSaldoConta":1500.00,"idUsuario":1}'

# 3) criar categoria de despesa
curl -X POST http://localhost:8080/categorias -H "Content-Type: application/json" \
  -d '{"nmCategoria":"Lazer","tpCategoria":"DESPESA","idUsuario":1}'

# 4) gasto à vista
curl -X POST http://localhost:8080/transacoes -H "Content-Type: application/json" \
  -d '{"dsTransacao":"Cinema","vlTransacao":60,"tpTransacao":"DESPESA","dtTransacao":"2026-11-04","idCategoria":1,"idConta":1,"idUsuario":1}'

# 5) gasto parcelado em 10x (o valor é o total)
curl -X POST http://localhost:8080/transacoes -H "Content-Type: application/json" \
  -d '{"dsTransacao":"Notebook","vlTransacao":3000,"tpTransacao":"DESPESA","dtTransacao":"2026-11-04","idCategoria":1,"idConta":1,"idUsuario":1,"nrParcelas":10}'

# 6) gasto fixo (Netflix todo mês)
curl -X POST http://localhost:8080/transacoes -H "Content-Type: application/json" \
  -d '{"dsTransacao":"Netflix","vlTransacao":39.90,"tpTransacao":"DESPESA","dtTransacao":"2026-11-04","idCategoria":1,"idConta":1,"idUsuario":1,"flFixo":true}'

# 7) encerrar o gasto fixo a partir de um mês
curl -X DELETE "http://localhost:8080/transacoes/12?proximas=true"

# 8) resumo do mês
curl "http://localhost:8080/relatorios/resumo?idUsuario=1&mes=11&ano=2026"
```

---

## 9. Tarefas automáticas

O `TransacaoService.aplicarParcelasVencidas` roda **quando a API sobe e todo dia às 00:05**:

1. **Renova os gastos fixos**: garante que cada um tenha sempre os próximos 12 meses lançados.
2. **Aplica no saldo** as transações futuras cuja data já chegou. Se o saldo da conta não cobrir, a transação continua pendente e é tentada de novo no dia seguinte.

Uma falha nessa tarefa (por exemplo, banco fora do ar) só gera um aviso no log; ela **nunca derruba a API**.

---

## 10. Problemas comuns

| Sintoma | Causa provável | O que fazer |
|---|---|---|
| `Port 8080 was already in use` | outra execução da API ainda está aberta | pare a anterior no IntelliJ (quadrado vermelho) e rode de novo |
| Rotas novas (`/transacoes`, `/categorias`...) dão **500** | a API que está rodando é uma versão antiga | pare e rode de novo a `EsqApplication` |
| `ORA-00904: identificador inválido` | falta alguma coluna no banco | rode o `sql/schema.sql` (ou, se as tabelas já existem de uma versão antiga, `limpar-banco.sql` e depois `schema.sql`) |
| `ORA-00942: tabela ou view não existe` | tabelas ainda não criadas | rode o `sql/schema.sql` |
| `ORA-00955: nome já está sendo usado` ao rodar o script | a tabela já existe | normal em reexecução; para recomeçar use o `limpar-banco.sql` |
| `ORA-01017: usuário/senha inválidos` | `DB_USER` / `DB_PASSWORD` errados ou não configurados | revise a seção 3.2 |
| `ORA-12541` / `Connection refused` / timeout | sem acesso ao servidor da FIAP | confira internet/VPN e o host `oracle.fiap.com.br:1521` |
| `ConflictingBeanDefinitionException` ao subir | sobra de compilação antiga | em *Build > Rebuild Project* ou apague a pasta `target/` e rode de novo |
| O frontend mostra "Não foi possível conectar ao servidor" | API fora do ar ou em outra porta | confirme <http://localhost:8080/usuarios> |

---

## 11. Requisitos da disciplina

| # | Requisito | Onde está |
|---|---|---|
| 1 | Classes de modelo (entidades) | `model/` — 9 entidades |
| 2 | Repository com JPA para cada entidade | `repository/` |
| 3 | Service com as regras de negócio | `service/` e seção 7 |
| 4 | RestController com GET, POST, PUT e DELETE | `controller/` e seção 6 |
| 5 | Código HTTP certo para cada verbo | `200`, `201` + `Location`, `400`, `401`, `404`, `422` (`exception/TratadorErro`) |
| 6 | Tabelas criadas no Oracle da FIAP | `sql/schema.sql` |
| 7 | Conexão obrigatória com o Oracle da FIAP | `application.properties` |
| 8 | Várias entidades com as 4 camadas | usuário, conta, investimento, categoria, transação, transferência, meta, contribuição e configuração |

---

## 12. Limitações conhecidas

Este é um projeto acadêmico:

- A **senha é gravada em texto simples** (decisão do projeto) e o login não gera token: o frontend só guarda o usuário no navegador. Não use senhas reais.
- Não há controle de acesso por usuário na API: quem conhece a URL consegue consultar os dados de qualquer usuário.
- O frontend não usa os campos `flResumoEmail` e `flDicas` da configuração (continuam no banco, sem efeito).
