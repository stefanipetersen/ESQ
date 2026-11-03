# ESQ - API Fintech (Spring Boot + JPA + Oracle FIAP)

## Como rodar
1. Execute `sql/schema.sql` no Oracle da FIAP (cria `t_pfe_usuario`, `t_pfe_conta`, `t_pfe_investimento`).
2. Defina as variáveis de ambiente `DB_USER` (padrão `rm572254`) e `DB_PASSWORD`.
3. `mvn spring-boot:run` (a API sobe em `http://localhost:8080`).

## Endpoints
Para `/usuarios`, `/contas` e `/investimentos`:

| Verbo | Rota | Sucesso | Erros |
|---|---|---|---|
| GET | `/{recurso}` | 200 | |
| GET | `/{recurso}/{id}` | 200 | 404 |
| POST | `/{recurso}` | 201 + Location | 400 (validação), 422 (regra de negócio) |
| PUT | `/{recurso}/{id}` | 200 | 400, 404, 422 |
| DELETE | `/{recurso}/{id}` | 204 | 404, 422 |

## Regras de negócio
- Usuário: e-mail único, senha gravada como hash SHA-256, não pode ser removido se tiver contas ou investimentos.
- Conta: saldo não negativo e usuário precisa existir.
- Investimento: valor inicial maior que zero, data de aplicação não futura e usuário precisa existir.
