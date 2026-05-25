# Diagramas Mermaid do projeto

Este documento contém os diagramas principais para orientar a IA/Codex durante a implementação.

## 1. Diagrama geral de arquitetura

```mermaid
flowchart TD
    subgraph Android["Android App Java + XML + MVVM"]
        A1[Activities / XML Views]
        A2[ViewModels]
        A3[Repositories]
        A4[Services / RemoteDataSources]
        A5[OkHttp3 ApiClient]
        A6[SessionManager SharedPreferences]
    end

    subgraph PublicAPI["API Pública"]
        B1[Rick and Morty API]
        B2[JsonPlaceholder POST simulado]
    end

    subgraph Backend["Backend Local Grails/Groovy"]
        C1[AuthController]
        C2[FuncionarioController]
        C3[AuthService]
        C4[FuncionarioService]
        C5[Domains Usuario e Funcionario]
    end

    subgraph Database["MySQL Local"]
        D1[(usuarios)]
        D2[(funcionarios)]
    end

    A1 --> A2
    A2 --> A3
    A3 --> A4
    A4 --> A5
    A2 --> A6

    A5 --> B1
    A5 --> B2
    A5 --> C1
    A5 --> C2

    C1 --> C3
    C2 --> C4
    C3 --> C5
    C4 --> C5
    C5 --> D1
    C5 --> D2
```

## 2. Diagrama de fluxo principal do app

```mermaid
flowchart TD
    Start([Abrir aplicativo]) --> Splash[SplashActivity]
    Splash --> Animation[Executar animação]
    Animation --> Home[HomeActivity]

    Home --> LoginButton[Usuário toca em Entrar]
    LoginButton --> Login[LoginActivity]

    Login --> ValidateLogin{Campos válidos?}
    ValidateLogin -->|Não| LocalError[Exibir erro de validação]
    LocalError --> Login

    ValidateLogin -->|Sim| LoginRequest[POST /api/auth/login]
    LoginRequest --> LoginResult{Login bem-sucedido?}

    LoginResult -->|Não| LoginError[Exibir erro de credenciais ou serviço]
    LoginError --> Login

    LoginResult -->|Sim| SaveSession[Salvar sessão simples]
    SaveSession --> Menu[MainMenuActivity]

    Menu --> CharactersModule[Módulo Personagens]
    Menu --> EmployeesModule[Módulo Funcionários]
    Menu --> Logout[Logout opcional]

    Logout --> ClearSession[Limpar sessão]
    ClearSession --> Home
```

## 3. Diagrama de fluxo do módulo personagens

```mermaid
flowchart TD
    Menu[MainMenuActivity] --> CharacterList[CharacterListActivity]

    CharacterList --> InitialLoad[Carregar página 1]
    InitialLoad --> RequestPage1[GET Rick and Morty /character?page=1]
    RequestPage1 --> RenderList[Exibir RecyclerView]

    RenderList --> UserScroll{Chegou perto do final?}
    UserScroll -->|Não| RenderList
    UserScroll -->|Sim| CanLoad{Página atual menor que 3 e não está carregando?}

    CanLoad -->|Não| RenderList
    CanLoad -->|Sim| NextPage[Incrementar página]
    NextPage --> RequestNext[GET /character?page=N com filtros]
    RequestNext --> AppendList[Adicionar resultados à lista]
    AppendList --> RenderList

    RenderList --> ApplyFilter[Usuário aplica filtro]
    ApplyFilter --> ResetList[Limpar lista e resetar página para 1]
    ResetList --> RequestFiltered[GET /character com status/gender/species]
    RequestFiltered --> RenderList

    RenderList --> ClickCard[Usuário toca no card]
    ClickCard --> Profile[CharacterProfileActivity]
```

## 4. Diagrama de fluxo do perfil e câmera

```mermaid
flowchart TD
    Profile[CharacterProfileActivity] --> ShowData[Exibir 10 informações do personagem]
    ShowData --> CameraButton[Usuário toca no botão de câmera]

    CameraButton --> HasPermission{Permissão CAMERA concedida?}
    HasPermission -->|Não| RequestPermission[Solicitar permissão]
    RequestPermission --> PermissionResult{Usuário concedeu?}

    PermissionResult -->|Não| PermissionError[Exibir mensagem de permissão negada]
    PermissionResult -->|Sim| CreateFile[Criar arquivo/URI com FileProvider]

    HasPermission -->|Sim| CreateFile
    CreateFile --> OpenCamera[Abrir câmera nativa por Intent]
    OpenCamera --> CaptureResult{Foto capturada?}

    CaptureResult -->|Não| CaptureError[Exibir erro ou cancelamento]
    CaptureResult -->|Sim| UpdateImage[Atualizar foto na UI]
    UpdateImage --> BuildPayload[Montar CapturedImagePostRequest]
    BuildPayload --> FakePost[POST JsonPlaceholder /posts]
    FakePost --> PostResult{POST concluído?}

    PostResult -->|Sim| SuccessFeedback[Exibir sucesso]
    PostResult -->|Não| PostError[Exibir erro sem desfazer imagem]
```

## 5. Diagrama de fluxo do CRUD de funcionários

```mermaid
flowchart TD
    Menu[MainMenuActivity] --> EmployeeList[EmployeeListActivity]
    EmployeeList --> LoadEmployees[GET /api/funcionarios]

    LoadEmployees --> HasEmployees{Existem funcionários?}
    HasEmployees -->|Não| EmptyState[Exibir lista vazia]
    HasEmployees -->|Sim| RenderEmployees[Exibir RecyclerView]

    RenderEmployees --> CreateAction[Usuário toca em cadastrar]
    CreateAction --> EmployeeFormCreate[EmployeeFormActivity modo criação]
    EmployeeFormCreate --> ValidateCreate{Campos válidos?}
    ValidateCreate -->|Não| ShowCreateValidation[Exibir erro de validação]
    ValidateCreate -->|Sim| PostEmployee[POST /api/funcionarios]
    PostEmployee --> RefreshAfterCreate[Atualizar lista]

    RenderEmployees --> EditAction[Usuário toca em editar]
    EditAction --> EmployeeFormEdit[EmployeeFormActivity modo edição]
    EmployeeFormEdit --> ValidateEdit{Campos válidos?}
    ValidateEdit -->|Não| ShowEditValidation[Exibir erro de validação]
    ValidateEdit -->|Sim| PutEmployee[PUT /api/funcionarios/id]
    PutEmployee --> RefreshAfterEdit[Atualizar lista]

    RenderEmployees --> DeleteAction[Usuário toca em excluir]
    DeleteAction --> ConfirmDelete{Confirmar exclusão?}
    ConfirmDelete -->|Não| RenderEmployees
    ConfirmDelete -->|Sim| DeleteEmployee[DELETE /api/funcionarios/id]
    DeleteEmployee --> RefreshAfterDelete[Atualizar lista]

    RefreshAfterCreate --> LoadEmployees
    RefreshAfterEdit --> LoadEmployees
    RefreshAfterDelete --> LoadEmployees
```

## 6. Diagrama de atividades do login

```mermaid
stateDiagram-v2
    [*] --> LoginIdle
    LoginIdle --> ValidatingFields: Usuário toca em Entrar

    ValidatingFields --> LoginIdle: Campos inválidos
    ValidatingFields --> Loading: Campos válidos

    Loading --> Success: Backend retorna sucesso
    Loading --> InvalidCredentials: Backend retorna 401
    Loading --> ServiceUnavailable: Erro de rede/servidor

    InvalidCredentials --> LoginIdle
    ServiceUnavailable --> LoginIdle

    Success --> SaveSession
    SaveSession --> NavigateToMenu
    NavigateToMenu --> [*]
```

## 7. Diagrama de atividades da listagem de personagens

```mermaid
stateDiagram-v2
    [*] --> InitialLoading
    InitialLoading --> ListVisible: Página 1 carregada
    InitialLoading --> Empty: Nenhum resultado
    InitialLoading --> Error: Falha de rede/API

    ListVisible --> LoadingNextPage: Usuário chega perto do fim
    LoadingNextPage --> ListVisible: Próxima página carregada
    LoadingNextPage --> PaginationLimit: Já carregou 3 páginas
    LoadingNextPage --> Error: Falha ao carregar próxima página

    ListVisible --> ApplyingFilters: Usuário altera filtros
    ApplyingFilters --> InitialLoading: Resetar página e lista

    Error --> InitialLoading: Usuário tenta novamente
    Empty --> ApplyingFilters: Usuário altera filtros
```

## 8. Diagrama de atividades da câmera

```mermaid
stateDiagram-v2
    [*] --> ProfileVisible
    ProfileVisible --> CheckingPermission: Tocar em câmera

    CheckingPermission --> RequestingPermission: Permissão ausente
    CheckingPermission --> PreparingFile: Permissão já concedida

    RequestingPermission --> PermissionDenied: Usuário nega
    RequestingPermission --> PreparingFile: Usuário concede

    PermissionDenied --> ProfileVisible

    PreparingFile --> OpeningNativeCamera
    OpeningNativeCamera --> CaptureCanceled: Usuário cancela
    OpeningNativeCamera --> ImageCaptured: Foto capturada

    CaptureCanceled --> ProfileVisible
    ImageCaptured --> UpdatingUI
    UpdatingUI --> SendingFakePost

    SendingFakePost --> PostSuccess
    SendingFakePost --> PostError

    PostSuccess --> ProfileVisible
    PostError --> ProfileVisible
```

## 9. Diagrama de classes Android

```mermaid
classDiagram
    class SplashActivity {
        +onCreate()
        -startAnimation()
        -navigateToHome()
    }

    class HomeActivity {
        +onCreate()
        -goToLogin()
    }

    class LoginActivity {
        -LoginViewModel viewModel
        +onCreate()
        -setupObservers()
        -submitLogin()
        -renderState(UiState)
    }

    class MainMenuActivity {
        +onCreate()
        -goToCharacters()
        -goToEmployees()
        -logout()
    }

    class CharacterListActivity {
        -CharacterListViewModel viewModel
        -CharacterAdapter adapter
        +onCreate()
        -setupRecyclerView()
        -setupFilters()
        -setupPagination()
        -renderState(UiState)
    }

    class CharacterProfileActivity {
        -CharacterProfileViewModel viewModel
        +onCreate()
        -renderCharacter()
        -requestCamera()
        -openNativeCamera()
        -renderCapturedImage()
    }

    class EmployeeListActivity {
        -EmployeeViewModel viewModel
        -EmployeeAdapter adapter
        +onCreate()
        -setupRecyclerView()
        -loadEmployees()
        -openCreateForm()
        -openEditForm()
        -confirmDelete()
    }

    class EmployeeFormActivity {
        -EmployeeViewModel viewModel
        +onCreate()
        -fillFormIfEditing()
        -validateFields()
        -saveEmployee()
    }

    class LoginViewModel {
        -AuthRepository repository
        -MutableLiveData~UiState~ loginState
        +login(email, senha)
        +getLoginState()
    }

    class CharacterListViewModel {
        -CharacterRepository repository
        -MutableLiveData~UiState~ charactersState
        -int currentPage
        -boolean isLoading
        -List~Character~ loadedCharacters
        +loadFirstPage()
        +loadNextPage()
        +applyFilters(status, gender, species)
        +getCharactersState()
    }

    class CharacterProfileViewModel {
        -CharacterRepository repository
        -MutableLiveData~Uri~ capturedImageUri
        +setCharacter(character)
        +onImageCaptured(uri)
        +sendCapturedImagePost()
    }

    class EmployeeViewModel {
        -EmployeeRepository repository
        -MutableLiveData~UiState~ employeesState
        +loadEmployees()
        +createEmployee(request)
        +updateEmployee(id, request)
        +deleteEmployee(id)
    }

    class AuthRepository {
        -AuthService service
        +login(LoginRequest)
    }

    class CharacterRepository {
        -RickMortyService service
        -FakePostService fakePostService
        +getCharacters(page, status, gender, species)
        +sendCapturedImagePost(request)
    }

    class EmployeeRepository {
        -EmployeeService service
        +list()
        +getById(id)
        +create(request)
        +update(id, request)
        +delete(id)
    }

    class ApiClient {
        -OkHttpClient client
        +getInstance()
        +getClient()
    }

    class RequestFactory {
        +get(url)
        +postJson(url, json)
        +putJson(url, json)
        +delete(url)
    }

    class SessionManager {
        +saveSession(token, user)
        +isLoggedIn()
        +getToken()
        +clear()
    }

    class UiState~T~ {
        +Status status
        +T data
        +String message
        +loading()
        +success(data)
        +error(message)
        +empty()
    }

    LoginActivity --> LoginViewModel
    CharacterListActivity --> CharacterListViewModel
    CharacterProfileActivity --> CharacterProfileViewModel
    EmployeeListActivity --> EmployeeViewModel
    EmployeeFormActivity --> EmployeeViewModel

    LoginViewModel --> AuthRepository
    CharacterListViewModel --> CharacterRepository
    CharacterProfileViewModel --> CharacterRepository
    EmployeeViewModel --> EmployeeRepository

    AuthRepository --> AuthService
    CharacterRepository --> RickMortyService
    CharacterRepository --> FakePostService
    EmployeeRepository --> EmployeeService

    AuthService --> ApiClient
    RickMortyService --> ApiClient
    FakePostService --> ApiClient
    EmployeeService --> ApiClient

    AuthService --> RequestFactory
    RickMortyService --> RequestFactory
    FakePostService --> RequestFactory
    EmployeeService --> RequestFactory

    LoginViewModel --> SessionManager
```

## 10. Diagrama de classes do backend Grails

```mermaid
classDiagram
    class AuthController {
        +login()
    }

    class FuncionarioController {
        +index()
        +show(Long id)
        +save()
        +update(Long id)
        +delete(Long id)
    }

    class AuthService {
        +autenticar(String email, String senha)
    }

    class FuncionarioService {
        +listar()
        +buscar(Long id)
        +criar(Map data)
        +atualizar(Long id, Map data)
        +excluir(Long id)
    }

    class Usuario {
        Long id
        String nome
        String email
        String senha
        Boolean ativo
        Date dataCriacao
        +constraints
    }

    class Funcionario {
        Long id
        String nome
        String email
        String cargo
        BigDecimal salario
        Boolean ativo
        Date dataCriacao
        +constraints
    }

    AuthController --> AuthService
    FuncionarioController --> FuncionarioService
    AuthService --> Usuario
    FuncionarioService --> Funcionario
```

## 11. Diagrama de dados SQL

```mermaid
erDiagram
    USUARIOS {
        BIGINT id PK
        VARCHAR nome
        VARCHAR email UK
        VARCHAR senha
        BOOLEAN ativo
        DATETIME data_criacao
    }

    FUNCIONARIOS {
        BIGINT id PK
        VARCHAR nome
        VARCHAR email UK
        VARCHAR cargo
        DECIMAL salario
        BOOLEAN ativo
        DATETIME data_criacao
        DATETIME data_atualizacao
    }
```

## 12. Diagrama de sequência do login

```mermaid
sequenceDiagram
    actor User as Usuário
    participant LoginActivity
    participant LoginViewModel
    participant AuthRepository
    participant AuthService
    participant Backend as Grails Backend
    participant SessionManager

    User->>LoginActivity: Preenche e-mail e senha
    User->>LoginActivity: Toca em Entrar
    LoginActivity->>LoginViewModel: login(email, senha)
    LoginViewModel->>LoginViewModel: Valida estado inicial/loading
    LoginViewModel->>AuthRepository: login(LoginRequest)
    AuthRepository->>AuthService: login(request)
    AuthService->>Backend: POST /api/auth/login

    alt Sucesso
        Backend-->>AuthService: 200 + token + user
        AuthService-->>AuthRepository: LoginResponse
        AuthRepository-->>LoginViewModel: sucesso
        LoginViewModel->>SessionManager: saveSession(token, user)
        LoginViewModel-->>LoginActivity: UiState SUCCESS
        LoginActivity->>LoginActivity: Navega para MainMenuActivity
    else Erro
        Backend-->>AuthService: 401 ou erro
        AuthService-->>AuthRepository: erro
        AuthRepository-->>LoginViewModel: erro
        LoginViewModel-->>LoginActivity: UiState ERROR
        LoginActivity->>LoginActivity: Exibe mensagem
    end
```

## 13. Diagrama de sequência da listagem de personagens

```mermaid
sequenceDiagram
    actor User as Usuário
    participant Activity as CharacterListActivity
    participant VM as CharacterListViewModel
    participant Repo as CharacterRepository
    participant Service as RickMortyService
    participant API as Rick and Morty API
    participant Adapter as CharacterAdapter

    Activity->>VM: loadFirstPage()
    VM->>Repo: getCharacters(1, filters)
    Repo->>Service: fetchCharacters(page, filters)
    Service->>API: GET /api/character?page=1
    API-->>Service: JSON
    Service-->>Repo: CharacterResponse
    Repo-->>VM: Lista de personagens
    VM-->>Activity: UiState SUCCESS
    Activity->>Adapter: submit/update list

    User->>Activity: Rola até perto do fim
    Activity->>VM: loadNextPage()
    VM->>VM: Verifica isLoading e page <= 3
    VM->>Repo: getCharacters(nextPage, filters)
    Repo->>Service: fetchCharacters(nextPage, filters)
    Service->>API: GET /api/character?page=N
    API-->>Service: JSON
    Service-->>Repo: CharacterResponse
    Repo-->>VM: Novos personagens
    VM-->>Activity: UiState SUCCESS com lista acumulada
    Activity->>Adapter: atualiza lista
```

## 14. Diagrama de sequência do CRUD de funcionários

```mermaid
sequenceDiagram
    actor User as Usuário
    participant Activity as EmployeeList/FormActivity
    participant VM as EmployeeViewModel
    participant Repo as EmployeeRepository
    participant Service as EmployeeService
    participant Backend as Grails Backend
    participant DB as MySQL

    User->>Activity: Solicita listar funcionários
    Activity->>VM: loadEmployees()
    VM->>Repo: list()
    Repo->>Service: list()
    Service->>Backend: GET /api/funcionarios
    Backend->>DB: SELECT funcionarios
    DB-->>Backend: registros
    Backend-->>Service: JSON
    Service-->>Repo: List<Employee>
    Repo-->>VM: sucesso
    VM-->>Activity: UiState SUCCESS

    User->>Activity: Salva funcionário
    Activity->>VM: createEmployee ou updateEmployee
    VM->>Repo: create/update
    Repo->>Service: POST ou PUT
    Service->>Backend: Request JSON
    Backend->>DB: INSERT ou UPDATE
    DB-->>Backend: confirmação
    Backend-->>Service: JSON sucesso
    Service-->>Repo: sucesso
    Repo-->>VM: sucesso
    VM-->>Activity: Recarregar lista

    User->>Activity: Exclui funcionário
    Activity->>VM: deleteEmployee(id)
    VM->>Repo: delete(id)
    Repo->>Service: delete(id)
    Service->>Backend: DELETE /api/funcionarios/id
    Backend->>DB: DELETE FROM funcionarios
    DB-->>Backend: confirmação
    Backend-->>Service: sucesso
    Service-->>Repo: sucesso
    Repo-->>VM: sucesso
    VM-->>Activity: Recarregar lista
```

## 15. Diagrama de componentes

```mermaid
flowchart LR
    subgraph UI["Camada UI"]
        SplashActivity
        HomeActivity
        LoginActivity
        MainMenuActivity
        CharacterListActivity
        CharacterProfileActivity
        EmployeeListActivity
        EmployeeFormActivity
    end

    subgraph VM["Camada ViewModel"]
        LoginViewModel
        CharacterListViewModel
        CharacterProfileViewModel
        EmployeeViewModel
    end

    subgraph DATA["Camada Data"]
        AuthRepository
        CharacterRepository
        EmployeeRepository
        AuthService
        RickMortyService
        EmployeeService
        FakePostService
        ApiClient
    end

    subgraph UTIL["Utilitários"]
        UiState
        SessionManager
        RequestFactory
        CharacterQueryBuilder
        ValidationUtils
    end

    subgraph EXT["Sistemas externos"]
        RickMortyAPI["Rick and Morty API"]
        JsonPlaceholder
        GrailsBackend["Grails Backend"]
        MySQL
    end

    UI --> VM
    VM --> DATA
    VM --> UTIL
    DATA --> UTIL
    DATA --> EXT
    GrailsBackend --> MySQL
```
