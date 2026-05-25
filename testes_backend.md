# Testes Automatizados

## Objetivo

Registrar os testes automatizados criados para o backend Grails/Groovy e para o app Android, com foco nos fluxos principais exigidos pelo desafio e em regras de baixo risco para teste local.

## Backend

Os testes do backend usam Spock com MockMvc.

Rodam com:

```powershell
cd backend-grails
.\gradlew.bat test
```

Cobrem:

- autenticacao via `POST /api/auth/login`
- listagem de funcionarios via `GET /api/funcionarios`
- criacao de funcionario via `POST /api/funcionarios`
- busca por id via `GET /api/funcionarios/{id}`
- atualizacao via `PUT /api/funcionarios/{id}`
- exclusao via `DELETE /api/funcionarios/{id}`
- validacao de payload invalido
- validacao de e-mail duplicado
- casos de erro de autenticacao
- casos de erro de funcionario inexistente
- validacoes negativas de campos obrigatorios e salario

## Arquivos de teste criados

- `backend-grails/src/test/groovy/backend/auth/AuthIntegrationSpec.groovy`
- `backend-grails/src/test/groovy/backend/funcionario/FuncionarioIntegrationSpec.groovy`

## O que cada teste valida

### AuthIntegrationSpec

Casos cobertos:

- login com `admin@empresa.com` e senha `123456` retorna `200`
- resposta contem `success = true`
- resposta contem `token`
- resposta contem `user.email = admin@empresa.com`
- login com senha invalida retorna `401`
- resposta de erro contem `success = false`
- login com body vazio retorna `401`
- login com e-mail vazio retorna `401`
- login com senha vazia retorna `401`
- login com usuario inexistente retorna `401`

### FuncionarioIntegrationSpec

Casos cobertos:

- `GET /api/funcionarios` retorna `200`
- `POST /api/funcionarios` cria um funcionario e retorna `201`
- `GET /api/funcionarios/{id}` retorna o funcionario criado
- `PUT /api/funcionarios/{id}` atualiza o funcionario e retorna `200`
- `DELETE /api/funcionarios/{id}` remove o funcionario e retorna `204`
- `GET` no funcionario removido retorna `404`
- `POST` com dados invalidos retorna `400`
- `POST` com e-mail duplicado retorna `400`
- `GET /api/funcionarios/{id}` inexistente retorna `404`
- `PUT /api/funcionarios/{id}` inexistente retorna `404`
- `DELETE /api/funcionarios/{id}` inexistente retorna `404`
- `POST` sem nome retorna `400`
- `POST` sem e-mail retorna `400`
- `POST` com salario negativo retorna `400`
- `POST` com salario invalido retorna `400`
- `PUT` com e-mail duplicado retorna `400`

## Android

Os testes unitarios locais ficam em `app/src/test/java`.

Rodam com:

```powershell
.\gradlew.bat :app:testDebugUnitTest
```

Cobrem:

- `Event<T>`
- `CharacterQueryBuilder`
- `LoadCharactersPageUseCase`

Arquivos atuais:

- `app/src/test/java/com/example/rickandmortyapp/util/EventTest.java`
- `app/src/test/java/com/example/rickandmortyapp/util/CharacterQueryBuilderTest.java`
- `app/src/test/java/com/example/rickandmortyapp/domain/usecase/LoadCharactersPageUseCaseTest.java`

Cobertura adicionada:

- `EventTest`
  - garante que `getContentIfNotHandled()` consome o evento apenas uma vez
  - garante que `peek()` nao consome o evento
- `CharacterQueryBuilderTest`
  - valida montagem da URL base com pagina
  - valida inclusao e trim de filtros
- `LoadCharactersPageUseCaseTest`
  - valida regra de limite de paginas
  - valida regra de bloqueio por loading
  - valida normalizacao de filtros
  - valida delegacao para o repository

## Testes nao adicionados agora

- `SaveEmployeeUseCase` nao foi testado agora porque depende de `Application`, `getString`, `TextUtils`, `Patterns` e resources Android, o que exigiria Robolectric ou refatoracao adicional.
- `SendCapturedPhotoUseCase` nao foi testado agora porque o fluxo usa `android.net.Uri`, que nao e mockado em teste JVM local sem Robolectric.
- Essa decisao foi tomada para manter a suite de testes estavel, rapida e de baixo risco.

## Estrutura de execucao

O projeto nao possui task `integrationTest` configurada. A execucao foi feita com a task padrao de testes:

```powershell
cd backend-grails
.\gradlew.bat test
```

Para os testes unitarios Android, a execucao foi:

```powershell
.\gradlew.bat :app:testDebugUnitTest
```

## Resultado da execucao

Resultado obtido:

```text
BUILD SUCCESSFUL in 4s
> Task :test
```

Os testes compilaram e executaram com sucesso.

Resultado dos testes Android:

```text
BUILD SUCCESSFUL in 7s
> Task :app:testDebugUnitTest
```

## Observacoes tecnicas

- Os testes foram implementados com Spock no ambiente de teste do Grails.
- Os testes Android foram implementados com JUnit local, sem testes instrumentados.
- Nao foi necessario alterar endpoints.
- Nao foi necessario alterar o JSON esperado pelo app Android.
- Nao foi necessario alterar a arquitetura `Controller -> Service -> Domain/GORM`.
- O fluxo principal documentado da aplicacao continua sendo MySQL local.
- O ambiente de teste do Grails pode usar a configuracao padrao de teste sem mudar o fluxo principal da entrega.
- `SaveEmployeeUseCase` nao recebeu teste unitario nesta etapa porque, no setup atual, ele depende de componentes Android e resources que exigiriam ampliar o ambiente de teste alem do escopo de baixo risco.

## Como reproduzir

1. Abrir um terminal na raiz do projeto.
2. Entrar em `backend-grails`.
3. Executar:

```powershell
.\gradlew.bat test
```

Para rodar os testes unitarios Android:

```powershell
.\gradlew.bat :app:testDebugUnitTest
```

## Conclusao

O backend agora possui cobertura automatizada para os fluxos REST principais exigidos no desafio:

- autenticacao
- CRUD de funcionarios
- validacoes basicas de erro

E o app Android agora possui cobertura automatizada local para regras pequenas e estaveis de utilitarios e use case de paginacao.

O fluxo principal da aplicacao continua sendo MySQL local, enquanto os testes automatizados usam o ambiente de teste adequado para execucao rapida.
