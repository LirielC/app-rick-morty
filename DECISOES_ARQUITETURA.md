# Decisões de Arquitetura do Projeto

Este documento registra as principais decisões técnicas tomadas durante o desenvolvimento do projeto **RickAndMortyApp**, explicando o motivo de cada escolha e como elas ajudam a atender ao desafio técnico.

O projeto foi desenvolvido como um aplicativo Android nativo em **Java**, usando **Views/XML**, arquitetura **MVVM**, comunicação HTTP com **OkHttp3**, backend local em **Grails/Groovy** e persistência em **MySQL local**.

---

## 1. Arquitetura geral do projeto

O projeto foi dividido em duas partes principais:

```text
App Android
  → Java
  → Views/XML
  → MVVM
  → OkHttp3
  → API Rick and Morty
  → Backend Grails

Backend
  → Grails/Groovy
  → Controllers
  → Services
  → Domain/GORM
  → MySQL
```

### Motivo da decisão

Essa separação atende diretamente ao desafio, que exige um app Android nativo consumindo tanto uma API pública quanto um backend próprio em Grails/Groovy com MySQL.

A divisão também facilita manutenção, testes e demonstração, pois cada parte tem responsabilidade clara.

## 2. MVVM como arquitetura principal no Android

A arquitetura principal do app Android é MVVM:

```text
Activity/XML
  → ViewModel
  → UseCase, quando necessário
  → Repository
  → Service/RemoteDataSource
  → OkHttp3
```

### Responsabilidades

#### Activity

As Activities são responsáveis por:

- inicializar componentes visuais;
- configurar listeners;
- observar estados expostos pela ViewModel;
- renderizar loading, erro, vazio e sucesso;
- executar navegação entre telas.

Elas não devem conter regra de negócio nem chamadas HTTP diretas.

#### ViewModel

As ViewModels são responsáveis por:

- controlar o estado da tela;
- chamar UseCases ou Repositories;
- expor LiveData para a Activity;
- preservar estado durante mudanças de configuração quando possível.

#### Repository

Os Repositories são responsáveis por:

- isolar a origem dos dados;
- chamar os services/remotes;
- adaptar erros técnicos para mensagens coerentes com o módulo;
- impedir que a UI conheça detalhes de rede.

#### Service / RemoteDataSource

Os services são responsáveis por:

- montar requests;
- usar OkHttp3;
- interpretar resposta HTTP básica;
- converter JSON em modelos usando Gson.

### Motivo da decisão

MVVM era obrigatório no desafio. Além disso, essa arquitetura reduz acoplamento entre interface, regra de tela e camada de dados.

## 3. UseCases leves

Foi adicionada uma camada leve de UseCases, mas apenas nos pontos em que havia regra clara.

UseCases criados:

- `LoadCharactersPageUseCase`
- `SaveEmployeeUseCase`
- `SendCapturedPhotoUseCase`

### LoadCharactersPageUseCase

Responsável por:

- controlar regra de paginação;
- limitar carregamento até 3 páginas;
- normalizar filtros;
- evitar regra excessiva dentro da ViewModel.

### SaveEmployeeUseCase

Responsável por:

- validar dados básicos do funcionário;
- decidir entre criação e edição;
- chamar o repository correto.

### SendCapturedPhotoUseCase

Responsável por:

- validar dados da foto capturada;
- montar payload do POST simulado;
- chamar o service/repository responsável pelo envio.

### Motivo da decisão

A ideia foi usar uma Clean Architecture leve, sem exagerar. O desafio pede MVVM, mas não proíbe UseCases. Eles foram usados apenas onde traziam clareza.

Não foram criados UseCases para tudo, para evitar overengineering.

## 4. Repository Pattern

O projeto usa repositories para separar a camada de UI da camada de dados.

Principais repositories:

- `AuthRepository`
- `CharacterRepository`
- `EmployeeRepository`

### Motivo da decisão

O Repository Pattern evita que ViewModels conheçam detalhes como:

- URLs;
- OkHttp;
- JSON;
- códigos HTTP;
- parsing;
- mensagens técnicas de rede.

Isso deixa a ViewModel focada em estado de tela e melhora a manutenção do projeto.

## 5. ApiResult

Foi criado um padrão de retorno para a camada de dados:

`ApiResult<T>`

Ele representa:

- sucesso;
- erro;
- código HTTP;
- mensagem técnica ou adaptada.

### Motivo da decisão

Antes, cada service/repository poderia tratar erro de uma forma diferente. Com ApiResult, a resposta fica mais padronizada.

A camada remote/service retorna informações técnicas, enquanto o repository adapta para mensagens mais adequadas ao módulo.

Exemplo:

Service:
erro de rede → `ERROR_CODE_NETWORK`

Repository:
`"Backend indisponível. Verifique se o servidor Grails está rodando."`

## 6. UiState

O app usa estados de UI para representar as telas de forma previsível.

Exemplos:

- `LOADING`
- `SUCCESS`
- `ERROR`
- `EMPTY`

Também foram criados estados específicos quando necessário:

- `LoginUiState`
- `EmployeeListUiState`
- `EmployeeFormUiState`
- `CharacterProfileUiState`

### Motivo da decisão

Separar estado de tela evita múltiplos LiveData soltos, como:

- `isLoading`
- `errorMessage`
- `data`
- `successMessage`

Com um estado mais coeso, a tela fica mais previsível e mais fácil de renderizar.

## 7. Event<T> para eventos transitórios

Foi criado o padrão `Event<T>` para eventos que devem acontecer apenas uma vez.

Exemplos:

- Snackbar;
- Toast;
- navegação;
- `finish()`;
- reload único após exclusão.

### Motivo da decisão

Em Android, quando a Activity é recriada por rotação ou reobservação do LiveData, eventos antigos podem disparar novamente.

Exemplo de problema evitado:

Usuário faz login
→ navega para menu
→ Activity recria
→ navegação dispara novamente

Com `Event<T>`, a Activity consome o evento apenas uma vez.

## 8. ListAdapter + DiffUtil

As listas principais usam `ListAdapter` com `DiffUtil.ItemCallback`.

Aplicado em:

- `CharacterAdapter`
- `EmployeeAdapter`

### Comparação em CharacterAdapter

`areItemsTheSame`: compara `id` do personagem

`areContentsTheSame`: compara:

- `name`
- `status`
- `species`
- `gender`
- `image`
- `location.name`

### Comparação em EmployeeAdapter

`areItemsTheSame`: compara `id` do funcionário

`areContentsTheSame`: compara:

- `nome`
- `email`
- `cargo`
- `salario`
- `ativo`
- `dataCriacao`

### Motivo da decisão

O desafio cita DiffUtil como diferencial.

Ele melhora:

- performance do RecyclerView;
- atualização visual;
- animações de inserção/remoção;
- atualização após CRUD;
- paginação de personagens.

Também evita o uso excessivo de `notifyDataSetChanged()`.

## 9. Tradução da interface sem alterar valores da API

A API Rick and Morty retorna valores em inglês, como:

- `Alive`
- `Dead`
- `Human`
- `Male`
- `Female`

Na interface, esses valores são exibidos em português:

- `Alive` → `Vivo`
- `Dead` → `Morto`
- `Human` → `Humano`
- `Male` → `Masculino`
- `Female` → `Feminino`

### Motivo da decisão

A interface fica mais natural para o usuário brasileiro.

Porém, os valores internos e os valores enviados para a API continuam em inglês.

Exemplo:

UI mostra: `Vivo`
API recebe: `status=alive`

Isso evita quebrar os filtros da API.

## 10. Filtros de personagens

A tela de personagens possui filtros por:

- status;
- gênero;
- espécie.

Os filtros são exibidos de forma amigável, mas convertidos para os valores esperados pela API.

### Motivo da decisão

O desafio exige filtros compatíveis com a API Rick and Morty. A solução mantém usabilidade para o usuário e compatibilidade técnica com a API.

## 11. Paginação limitada a 3 páginas

A listagem de personagens começa pela página 1 e carrega até no máximo 3 páginas.

### Motivo da decisão

Esse comportamento atende diretamente ao requisito do desafio.

A lógica evita:

- carregamentos simultâneos;
- duplicação de páginas;
- chamadas desnecessárias;
- paginação infinita fora do escopo.

## 12. Câmera nativa e FileProvider

A tela de perfil do personagem permite atualizar visualmente a foto usando a câmera nativa do Android.

A solução usa:

- Permissão `CAMERA`
- `Intent`/`Activity Result`
- `FileProvider`
- URI segura

Após capturar a imagem:

- a UI é atualizada;
- a foto original é substituída visualmente;
- um POST simulado é enviado para JsonPlaceholder.

### Motivo da decisão

O desafio exige uso da câmera nativa, não câmera customizada.

O `FileProvider` foi usado porque é a forma segura e moderna de compartilhar URI de arquivo com a câmera em Android.

## 13. POST simulado da captura

Após a captura da imagem, o app envia um POST simulado para:

`https://jsonplaceholder.typicode.com/posts`

O payload contém dados como:

- `characterId`
- `characterName`
- `capturedImageUri`
- `capturedAt`
- `source = camera`

O binário real da imagem não é enviado.

### Motivo da decisão

O desafio pede simular o envio dos dados da captura para uma API pública de teste, sem necessidade de enviar o arquivo real da imagem.

## 14. Backend em Grails/Groovy

O backend foi implementado com arquitetura em camadas:

```text
Controller
  → Service
  → Domain/GORM
  → MySQL
```

Principais fluxos:

```text
AuthController
  → AuthService
  → Usuario/GORM
  → MySQL

FuncionarioController
  → FuncionarioService
  → Funcionario/GORM
  → MySQL
```

### Motivo da decisão

Essa é uma arquitetura simples, idiomática para Grails e suficiente para o escopo do desafio.

Não foi usada Clean Architecture pesada no backend porque o desafio pede um backend REST simples para autenticação e CRUD de funcionários.

## 15. Controllers finos no backend

Os controllers do backend são responsáveis por:

- receber requests;
- delegar para services;
- retornar JSON;
- definir status HTTP;
- tratar erros conhecidos.

### Motivo da decisão

Controllers finos evitam mistura de responsabilidades e deixam a regra de negócio dentro dos services.

## 16. Services no backend

Os services concentram:

- autenticação;
- validação de regra;
- CRUD de funcionário;
- tratamento de erros de negócio;
- montagem de respostas.

### Motivo da decisão

Isso deixa o backend mais organizado, testável e fácil de manter.

## 17. Domains pequenos com GORM

As classes de domínio são simples:

- `Usuario`
- `Funcionario`

Elas contêm apenas:

- campos;
- constraints;
- mapping essencial.

### Usuario

- `id`
- `nome`
- `email`
- `senha`
- `ativo`
- `dataCriacao`

### Funcionario

- `id`
- `nome`
- `email`
- `cargo`
- `salario`
- `ativo`
- `dataCriacao`

### Motivo da decisão

Em Grails, domains devem representar o modelo persistido e suas validações básicas. Regras de controller, JSON, HTTP ou CRUD não devem ficar dentro das entidades.

## 18. MySQL como fluxo principal

O backend usa MySQL local como fluxo principal.

Configuração principal:

- `DB_USERNAME`
- `DB_PASSWORD`
- `rick_morty_app`
- `com.mysql.cj.jdbc.Driver`

H2, se existir, é apenas fallback/debug/teste.

### Motivo da decisão

O desafio exige persistência em MySQL local. Por isso o README orienta criação do banco e execução do backend com o profile MySQL.

## 19. BootStrap para dados iniciais

O `BootStrap.groovy` cria dados iniciais:

- `admin@empresa.com / 123456`
- funcionários de exemplo

Ele verifica se o admin já existe antes de criar, evitando duplicação.

### Motivo da decisão

Isso facilita a execução do projeto pelo avaliador, garantindo credenciais de teste e dados iniciais sem necessidade de cadastro manual.

## 20. Autenticação simplificada

A autenticação usa:

- email
- senha
- token simples
- sessão local simples no app

### Motivo da decisão

O desafio afirma que autenticação complexa não é obrigatória. Por isso, não foram implementados:

- JWT completo;
- refresh token;
- RBAC;
- hash de senha;
- controle avançado de permissões.

### Observação

Em produção, seria recomendado usar:

- hash seguro com BCrypt;
- JWT ou sessão robusta;
- controle de permissões;
- regras de segurança mais completas.

## 21. Sem RBAC

Não foi implementado RBAC, como:

- `ADMIN`
- `USER`
- permissões por endpoint

### Motivo da decisão

O desafio não exige controle de papéis/permissões. Implementar RBAC aumentaria a complexidade sem necessidade para o escopo.

## 22. Tratamento de erro

O projeto trata erros em diferentes camadas.

### No Android

- erro de rede;
- backend indisponível;
- credenciais inválidas;
- lista vazia;
- erro ao salvar/excluir funcionário.

### No backend

- `400` para validação;
- `401` para login inválido;
- `404` para recurso inexistente;
- `500` com JSON consistente para erro inesperado.

### Motivo da decisão

O desafio avalia estados de loading, erro e vazio. O tratamento explícito melhora a experiência e a previsibilidade do app.

## 23. Suporte a modo escuro

O app possui suporte a modo escuro usando tema DayNight e recursos `values-night`.

### Motivo da decisão

O desafio exige suporte a modo escuro. A interface foi ajustada para manter contraste e consistência visual.

## 24. Material Components e identidade visual

A interface usa Material Components com:

- cards arredondados;
- chips;
- bottom navigation;
- `TextInputLayout`;
- botões com cantos arredondados;
- splash com imagem temática;
- cores inspiradas em portal/tema Rick and Morty.

### Motivo da decisão

A intenção foi criar uma interface mais natural e consistente, sem parecer apenas uma tela técnica de teste.

## 25. Comunicação com backend local

O backend roda localmente na máquina do desenvolvedor em:

`http://localhost:8080`

Para celular físico, foi usada a estratégia:

`adb reverse tcp:8080 tcp:8080`

Para emulador, a URL correta é:

`http://10.0.2.2:8080`

Para celular na mesma rede:

`http://IP_DA_MAQUINA:8080`

### Motivo da decisão

O desafio permite backend local, sem necessidade de deploy externo. Essas estratégias permitem testar no celular físico ou emulador sem publicar o backend.

## 26. URL do backend configurada manualmente

A base URL atual fica em:

`app/src/main/java/com/example/rickandmortyapp/util/Constants.java`

No snapshot atual, está configurada para:

`http://localhost:8080`

### Motivo da decisão

Essa configuração favorece o teste em celular físico com `adb reverse`.

Para emulador, pode ser necessário alterar para:

`http://10.0.2.2:8080`

Essa limitação foi documentada no README.

## 27. Testes automatizados

Foram adicionados testes automatizados no backend e no Android.

### Backend

Cobertura:

- login válido;
- login inválido;
- body vazio;
- campos obrigatórios;
- CRUD de funcionários;
- casos de erro;
- e-mail duplicado.

### Android

Cobertura:

- `Event<T>`;
- `CharacterQueryBuilder`;
- `LoadCharactersPageUseCase`.

### Motivo da decisão

Testes automatizados são citados como diferencial no desafio. A estratégia foi testar partes relevantes e estáveis sem criar dependências pesadas ou testes frágeis.

## 28. Testes não adicionados

Alguns testes Android não foram adicionados de propósito.

### SaveEmployeeUseCase

Não foi testado porque depende de classes Android como:

- `Application`
- `getString`
- `TextUtils`
- `Patterns`
- `resources`

Testar isso exigiria Robolectric ou refatoração adicional.

### SendCapturedPhotoUseCase

Não foi testado porque depende de `android.net.Uri`, que não funciona bem em teste JVM local sem Robolectric.

### Motivo da decisão

A decisão foi manter a suíte de testes simples, rápida e estável, evitando adicionar complexidade perto da entrega.

## 29. Deploy externo não utilizado

Não foi feito deploy externo do backend.

### Motivo da decisão

O desafio afirma que deploy externo é opcional. O fluxo principal exigido é backend local com MySQL, acessado pelo app via emulador, `adb reverse` ou rede local.

## 30. Diagramas Mermaid

Foram criados diagramas Mermaid para documentar:

- fluxo geral do app;
- arquitetura Android;
- arquitetura backend;
- modelo de dados;
- sequências principais;
- câmera;
- integração externa.

### Motivo da decisão

Os diagramas ajudam o avaliador a entender rapidamente a estrutura e os fluxos principais do projeto.

O README contém os diagramas principais e o arquivo `DIAGRAMAS_MERMAID.md` contém os diagramas mais detalhados.

## 31. Por que a solução não foi mais complexa

Algumas tecnologias e padrões foram evitados propositalmente:

- Clean Architecture completa;
- RBAC;
- JWT completo;
- refresh token;
- DI complexo;
- EventBus;
- Retrofit;
- Jetpack Compose;
- deploy externo obrigatório.

### Motivo da decisão

O desafio pede uma solução funcional, clara e bem organizada, mas não exige sofisticação excessiva.

A prioridade foi:

- funcionamento
- organização
- clareza
- aderência ao escopo
- documentação
- testabilidade

## 32. Resumo das decisões

| Decisão | Motivo |
|---|---|
| MVVM no Android | Requisito obrigatório e boa separação de responsabilidades |
| Views/XML | Requisito do desafio |
| OkHttp3 | Requisito obrigatório para requests |
| Repository Pattern | Isolar origem dos dados e adaptar erros |
| UseCases leves | Separar regras específicas sem overengineering |
| ApiResult | Padronizar sucesso e erro na camada de dados |
| UiState | Organizar estados de tela |
| Event<T> | Evitar repetição de eventos após rotação |
| ListAdapter + DiffUtil | Melhorar atualização das listas |
| FileProvider | Compartilhar URI segura com câmera nativa |
| Grails/Groovy | Requisito do backend |
| MySQL local | Requisito de persistência |
| Backend em camadas | Simplicidade e clareza |
| Autenticação simples | Suficiente para o escopo |
| Sem RBAC | Não exigido pelo desafio |
| Sem deploy externo | Opcional no desafio |
| Testes pontuais | Ganho de qualidade sem fragilidade |

## 33. Conclusão

As decisões arquiteturais foram guiadas pelo objetivo de atender ao desafio com clareza, organização e estabilidade.

O projeto evita complexidade desnecessária, mas aplica boas práticas suficientes para demonstrar domínio de:

- Android nativo;
- MVVM;
- consumo de API pública;
- backend próprio;
- persistência com MySQL;
- câmera nativa;
- tratamento de estados;
- testes automatizados;
- documentação técnica.

A solução final busca equilibrar simplicidade, qualidade de código e aderência ao escopo solicitado.
