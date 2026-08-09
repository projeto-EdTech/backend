# Vestibuline — Backend

API REST em Spring Boot que dá suporte à plataforma Vestibuline: provas, questões, simulados, histórico de desempenho, artigos de conteúdo, planner de estudos e sincronização de conta com Discord.

## Stack

- **Java 21** / **Spring Boot 3.5.3**
- **PostgreSQL** com migrações versionadas via **Flyway**
- **Spring Security** (autenticação stateless via JWT próprio, com `com.auth0:java-jwt`)
- **Login social**: Google OAuth (verificação de ID Token)
- **Spring Data JPA / Hibernate**
- **springdoc-openapi** (Swagger UI)
- **Testcontainers** + JUnit 5 para testes de integração

## Pré-requisitos

- JDK 21
- PostgreSQL (local ou remoto)
- Docker (para rodar os testes com Testcontainers)
- Uma conta de e-mail Google com [senha de app](https://myaccount.google.com/apppasswords) para envio de e-mails (opcional em dev)

## Configuração

O projeto lê as configurações de variáveis de ambiente (ver `src/main/resources/application.properties`). Crie um arquivo `.env` (ou exporte as variáveis no seu shell) com:

| Variável | Descrição |
|---|---|
| `DB_HOST` | Host do PostgreSQL (ex.: `localhost`) |
| `DB_NAMEP` | Nome do banco |
| `DB_USERNAMEP` | Usuário do banco |
| `DB_PASSWORD` | Senha do banco |
| `JWT_SECRET_VESTIBULINE` | Chave secreta usada para assinar os JWTs — **obrigatória** (mínimo 32 bytes/256 bits; a aplicação falha o boot se ausente ou fraca), não existe valor padrão |
| `AUTH_CLIENT` | Client ID do Google usado para validar o login (`AuthService`) |
| `APP_PASSWORD_GOOGLE` | Senha de app do Gmail usada pelo `spring-boot-starter-mail` |
| `MICROSOFT_CLIENT_ID`, `MICROSOFT_TENANT_ID` | Reservadas para login via Microsoft (ainda não implementado) |
| `FACEBOOK_CLIENT_ID`, `FACEBOOK_CLIENT_SECRET` | Reservadas para login via Facebook (ainda não implementado) |
| `CORS_ALLOWED_ORIGINS` | Lista de origens permitidas por CORS, separadas por vírgula (opcional — tem default de dev) |
| `INTERNAL_ACCESS_TOKEN` | Token que protege as rotas `/internal/**` (hoje: `GET/PATCH /internal/logs-erro`, consulta de erros de produção capturados automaticamente para os devs). Se não definida, `/internal/**` fica inacessível (fail-closed) |

A aplicação sobe por padrão na porta **8081**.

## Rodando localmente

```bash
# subir dependências (Maven Wrapper já incluso)
./mvnw clean install

# rodar a aplicação
./mvnw spring-boot:run
```

As migrações do Flyway em `src/main/resources/db/migration` rodam automaticamente no boot.

Documentação interativa da API (Swagger UI): `http://localhost:8081/swagger-ui.html`

## Testes

```bash
./mvnw test
```

Os testes de integração usam Testcontainers e exigem Docker em execução. O profile `test` (`application-test.properties`) valida o schema contra as entidades (`ddl-auto=validate`) em vez de gerá-lo automaticamente.

> **Cobertura atual**: `planner` (controller, service e repository), além de testes unitários para autenticação/JWT, filtro de segurança, rate limiting, IDOR em `UsuarioController`, sincronização com Discord, nota de corte, exceções HTTP e simulado personalizado. Ainda faltam testes de integração para os módulos de artigo (upload/conversão docx) e histórico (transação completa).

## Estrutura do projeto

```
src/main/java/br/com/Vestibuline/
├── config/          # CORS, segurança, recursos estáticos
├── controller/       # Endpoints REST
├── domain/           # Um pacote por agregado: entidade + repositório + DTOs (+ validadores)
├── docs/              # Configuração do OpenAPI/Swagger
├── exception/         # Exceções de negócio e handler global (@ControllerAdvice)
├── infra/security/    # Filtro de autenticação JWT
└── service/           # Regras de negócio
```

Cada domínio (`usuario`, `prova`, `questao`, `artigo`, `discord`, etc.) segue o padrão de "fatia vertical": entidade, repositório e DTOs vivem juntos no mesmo pacote.

## Principais módulos

| Domínio | Descrição |
|---|---|
| `usuario` | Cadastro, perfil e autenticação do estudante |
| `instituicao` / `prova` / `questao` / `alternativa` | Banco de provas e questões por instituição |
| `simulado` | Geração de simulados (mix e personalizado) |
| `historico` / `resposta` | Registro de tentativas e respostas do usuário |
| `planner` | Recomendação de matérias a revisar |
| `artigo` | Conteúdo editorial (artigos, imagens, estatísticas de engajamento) |
| `nota_corte` | Notas de corte por instituição/curso/ano |
| `discord` | Vínculo de conta via token OTP temporário |

## Segurança — pontos de atenção

Este projeto está em evolução ativa. Ver [RELATORIO_AUDITORIA.md](RELATORIO_AUDITORIA.md) para o levantamento completo e [TASKS.md](TASKS.md) para o backlog priorizado.

- `Usuario.getAuthorities()` já expõe `ROLE_<TipoUsuario>`, mas nenhum endpoint hoje exige um papel específico — aplicar `@PreAuthorize`/`hasRole` quando surgir a necessidade.
- `POST /auth/discord/sync` continua público por natureza (troca o OTP pela conta); há rate limiting em memória (`RateLimitFilter`), mas não distribuído — revisar se a aplicação passar a rodar em múltiplas instâncias.
- Não há mecanismo de revogação de JWT (logout/blacklist) — um token comprometido segue válido até expirar.
- Endpoints de listagem (`/api/artigos`, `/api/instituicao`, `/api/materias`) ainda não são paginados.

## Changelog

Alterações relevantes são documentadas em [CHANGES.md](CHANGES.md).
