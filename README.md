# ESQ - API Fintech (Spring Boot + JPA + Oracle FIAP)

## Como rodar
1. Abra o projeto no IntelliJ e aguarde o Maven baixar as dependências (se precisar, use **Reload All Maven Projects** na aba Maven).
2. Execute o script `sql/schema.sql` no Oracle da FIAP para criar as tabelas `t_pfe_usuario`, `t_pfe_conta` e `t_pfe_investimento`.
3. Configure o acesso ao banco: o usuário vem da variável de ambiente `DB_USER` (padrão `rm572254`) e a senha da variável `DB_PASSWORD`.
4. Abra `src/main/java/br/com/fiap/esq/EsqApplication.java` e clique no triângulo verde ao lado do `main` (**Run 'EsqApplication'**).
5. Quando aparecer `Started EsqApplication` no console, a API está no ar em `http://localhost:8080`.

Para testar, abra `http://localhost:8080/usuarios` no navegador ou use o Postman.

## Endpoints

| Entidade | Rota base | Rota por id |
|---|---|---|
| Usuário | `/usuarios` | `/usuarios/{id}` |
| Conta | `/contas` | `/contas/{id}` |
| Investimento | `/investimentos` | `/investimentos/{id}` |

Troque `{id}` pelo número do registro (exemplo: `/usuarios/1`).

| Verbo | Rota | O que faz | Sucesso | Erros |
|---|---|---|---|---|
| GET | rota base | Lista todos | 200 | |
| GET | rota por id | Busca um | 200 | 404 |
| POST | rota base | Cria (envia JSON no corpo) | 201 + header Location | 400 (validação), 422 (regra de negócio) |
| PUT | rota por id | Atualiza (envia JSON no corpo) | 200 | 400, 404, 422 |
| DELETE | rota por id | Remove | 204 | 404, 422 |

## Exemplos de corpo (POST e PUT)

Usuário:
```json
{
  "nmUsuario": "Maria Silva",
  "dsEmail": "maria@email.com",
  "hashSenha": "minhasenha123"
}
```

Conta (o `idUsuario` precisa ser de um usuário existente):
```json
{
  "nmConta": "Conta Corrente",
  "tpConta": "CORRENTE",
  "vlSaldoConta": 1500.00,
  "idUsuario": 1
}
```

Investimento:
```json
{
  "strTipo": "CDB",
  "vlInicialInvestimento": 1000.00,
  "vlRendimento": 0,
  "dtAplicacao": "2026-11-01",
  "idUsuario": 1
}
```

Ordem sugerida para testar: crie primeiro um usuário e use o `idUsuario` retornado para criar contas e investimentos.

## Regras de negócio
- Usuário: e-mail único, senha gravada como hash SHA-256, não pode ser removido se tiver contas ou investimentos.
- Conta: saldo não negativo e usuário precisa existir.
- Investimento: valor inicial maior que zero, data de aplicação não futura (se omitida, usa a data de hoje) e usuário precisa existir.
