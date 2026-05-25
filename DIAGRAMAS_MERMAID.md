# Diagramas Mermaid do Projeto

Este arquivo reúne os diagramas visuais principais do app Android e do backend Grails.
O foco aqui é mostrar o fluxo central da aplicação, a arquitetura em camadas, o backend com MySQL e as integrações mais importantes, sem repetição desnecessária.

## 1. Fluxo geral do aplicativo

Este diagrama resume a navegação principal do app desde a abertura até os dois módulos centrais.
Ele também destaca o fluxo de perfil com câmera e POST simulado, além do CRUD de funcionários.

```mermaid
flowchart TD
    Splash["SplashActivity"] --> Home["HomeActivity"]
    Home --> Login["LoginActivity"]
    Login --> Menu["MainMenuActivity"]

    Menu --> CharacterList["CharacterListActivity"]
    CharacterList --> CharacterProfile["CharacterProfileActivity"]
    CharacterProfile --> NativeCamera["Câmera nativa"]
    NativeCamera --> FakePost["POST simulado"]

    Menu --> EmployeeList["EmployeeListActivity"]
    EmployeeList --> EmployeeForm["EmployeeFormActivity"]
    EmployeeForm --> CreateEmployee["Criar funcionário"]
    EmployeeForm --> EditEmployee["Editar funcionário"]
    EmployeeList --> DeleteEmployee["Excluir funcionário"]
```

## 2. Arquitetura Android MVVM

Este diagrama mostra a separação em camadas do app Android.
Os três fluxos principais usam a mesma base: Activity/XML, ViewModel, UseCase ou Repository, Service/OkHttp e API ou backend.

```mermaid
flowchart TD
    subgraph LoginFlow["Login"]
        LoginActivity["LoginActivity / XML"] --> LoginViewModel["LoginViewModel"]
        LoginViewModel --> AuthRepository["AuthRepository"]
        AuthRepository --> AuthService["AuthService"]
        AuthService --> OkHttpLogin["OkHttp3"]
        OkHttpLogin --> GrailsLogin["Backend Grails"]
    end

    subgraph CharacterFlow["Personagens"]
        CharacterListActivity["CharacterListActivity / XML"] --> CharacterListViewModel["CharacterListViewModel"]
        CharacterListViewModel --> LoadCharactersPageUseCase["LoadCharactersPageUseCase"]
        CharacterListViewModel --> CharacterRepository["CharacterRepository"]
        LoadCharactersPageUseCase --> CharacterRepository
        CharacterRepository --> RickMortyService["RickMortyService"]
        RickMortyService --> OkHttpCharacters["OkHttp3"]
        OkHttpCharacters --> RickAndMortyApi["Rick and Morty API"]
    end

    subgraph EmployeeFlow["Funcionários"]
        EmployeeScreens["EmployeeListActivity / EmployeeFormActivity"] --> EmployeeViewModel["EmployeeViewModel"]
        EmployeeViewModel --> SaveEmployeeUseCase["SaveEmployeeUseCase"]
        EmployeeViewModel --> EmployeeRepository["EmployeeRepository"]
        SaveEmployeeUseCase --> EmployeeRepository
        EmployeeRepository --> EmployeeService["EmployeeService"]
        EmployeeService --> OkHttpEmployees["OkHttp3"]
        OkHttpEmployees --> GrailsEmployees["Backend Grails"]
    end
```

## 3. Arquitetura Backend Grails

Este diagrama resume a organização do backend em controller, service, domain/GORM e banco local.
Também mostra o papel do `BootStrap` na criação do usuário administrador e dos dados iniciais.

```mermaid
flowchart TD
    AuthController["AuthController"] --> AuthService["AuthService"]
    AuthService --> Usuario["Usuario / GORM"]
    Usuario --> MySQL["MySQL local"]

    FuncionarioController["FuncionarioController"] --> FuncionarioService["FuncionarioService"]
    FuncionarioService --> Funcionario["Funcionario / GORM"]
    Funcionario --> MySQL

    BootStrap["BootStrap"] --> Usuario
    BootStrap --> Funcionario
```

## 4. Modelo de dados MySQL

Este diagrama mostra as duas entidades persistidas pelo backend.
Como não existe relacionamento direto implementado entre elas, os blocos ficam separados.

```mermaid
erDiagram
    USUARIO {
        BIGINT id
        STRING nome
        STRING email
        STRING senha
        BOOLEAN ativo
        DATETIME dataCriacao
    }

    FUNCIONARIO {
        BIGINT id
        STRING nome
        STRING email
        STRING cargo
        DECIMAL salario
        BOOLEAN ativo
        DATETIME dataCriacao
    }
```

## 5. Sequência de login

Este fluxo detalha o caminho do login do Android até o MySQL e o retorno para a UI.
Ele cobre tanto o caso de sucesso com sessão local quanto o caso de erro com mensagem ao usuário.

```mermaid
sequenceDiagram
    participant LoginActivity
    participant LoginViewModel
    participant AuthRepository
    participant AuthServiceAndroid as AuthService Android
    participant AuthController
    participant AuthServiceGrails as AuthService Grails
    participant Usuario
    participant MySQL
    participant SessionManager
    participant MainMenuActivity

    LoginActivity->>LoginViewModel: login(email, senha)
    LoginViewModel->>AuthRepository: login(request)
    AuthRepository->>AuthServiceAndroid: login(request)
    AuthServiceAndroid->>AuthController: POST /api/auth/login
    AuthController->>AuthServiceGrails: autenticar(email, senha)
    AuthServiceGrails->>Usuario: findByEmailAndSenhaAndAtivo(...)
    Usuario->>MySQL: SELECT
    MySQL-->>Usuario: resultado
    Usuario-->>AuthServiceGrails: usuario ou null

    alt Sucesso
        AuthServiceGrails-->>AuthController: 200 + token + user
        AuthController-->>AuthServiceAndroid: JSON sucesso
        AuthServiceAndroid-->>AuthRepository: LoginResponse
        AuthRepository-->>LoginViewModel: sucesso
        LoginViewModel->>SessionManager: saveSession(token, user)
        LoginViewModel-->>LoginActivity: evento de navegação
        LoginActivity->>MainMenuActivity: startActivity()
    else Erro
        AuthServiceGrails-->>AuthController: 401 ou erro
        AuthController-->>AuthServiceAndroid: mensagem de erro
        AuthServiceAndroid-->>AuthRepository: erro
        AuthRepository-->>LoginViewModel: erro
        LoginViewModel-->>LoginActivity: mensagem na UI
    end
```

## 6. Sequência dos módulos principais

Os três diagramas abaixo mostram o caminho principal de personagens, funcionários e câmera.
Eles foram mantidos separados para continuar legíveis e objetivos.

### 6.1 Personagens

```mermaid
sequenceDiagram
    participant CharacterListActivity
    participant CharacterListViewModel
    participant LoadCharactersPageUseCase
    participant CharacterRepository
    participant RickMortyService
    participant RickAndMortyApi as Rick and Morty API

    CharacterListActivity->>CharacterListViewModel: loadFirstPage() ou applyFilters()
    CharacterListViewModel->>LoadCharactersPageUseCase: execute(page, filters)
    LoadCharactersPageUseCase->>CharacterRepository: fetchCharacters(...)
    CharacterRepository->>RickMortyService: fetchCharacters(...)
    RickMortyService->>RickAndMortyApi: GET /api/character
    RickAndMortyApi-->>RickMortyService: JSON
    RickMortyService-->>CharacterRepository: CharacterResponse
    CharacterRepository-->>CharacterListViewModel: dados
    CharacterListViewModel-->>CharacterListActivity: UiState SUCCESS
```

### 6.2 Funcionários

```mermaid
sequenceDiagram
    participant EmployeeScreen as EmployeeListActivity / EmployeeFormActivity
    participant EmployeeViewModel
    participant SaveEmployeeUseCase
    participant EmployeeRepository
    participant EmployeeServiceAndroid as EmployeeService Android
    participant FuncionarioController
    participant FuncionarioService
    participant MySQL

    EmployeeScreen->>EmployeeViewModel: loadEmployees() ou saveEmployee()
    EmployeeViewModel->>EmployeeRepository: list() ou delete()
    EmployeeViewModel->>SaveEmployeeUseCase: execute(...)
    SaveEmployeeUseCase->>EmployeeRepository: create/update(...)
    EmployeeRepository->>EmployeeServiceAndroid: request HTTP
    EmployeeServiceAndroid->>FuncionarioController: GET / POST / PUT / DELETE
    FuncionarioController->>FuncionarioService: regra de negócio
    FuncionarioService->>MySQL: SELECT / INSERT / UPDATE / DELETE
    MySQL-->>FuncionarioService: resultado
    FuncionarioService-->>FuncionarioController: resposta
    FuncionarioController-->>EmployeeServiceAndroid: JSON/status
    EmployeeServiceAndroid-->>EmployeeRepository: dados
    EmployeeRepository-->>EmployeeViewModel: sucesso ou erro
    EmployeeViewModel-->>EmployeeScreen: estado e feedback
```

### 6.3 Câmera e POST simulado

```mermaid
sequenceDiagram
    participant CharacterProfileActivity
    participant FileProvider
    participant NativeCamera as Câmera nativa
    participant CharacterProfileViewModel
    participant SendCapturedPhotoUseCase
    participant CharacterRepository
    participant FakePostService
    participant JsonPlaceholder

    CharacterProfileActivity->>FileProvider: cria URI segura
    CharacterProfileActivity->>NativeCamera: abre câmera
    NativeCamera-->>CharacterProfileActivity: imagem capturada
    CharacterProfileActivity->>CharacterProfileViewModel: onImageCaptured(uri)
    CharacterProfileViewModel-->>CharacterProfileActivity: atualiza UI
    CharacterProfileViewModel->>SendCapturedPhotoUseCase: execute(character, uri)
    SendCapturedPhotoUseCase->>CharacterRepository: sendCapturedPhoto(payload)
    CharacterRepository->>FakePostService: POST simulado
    FakePostService->>JsonPlaceholder: POST /posts
    JsonPlaceholder-->>FakePostService: resposta
    FakePostService-->>CharacterRepository: sucesso ou erro
    CharacterRepository-->>CharacterProfileViewModel: resultado
    CharacterProfileViewModel-->>CharacterProfileActivity: feedback
```
