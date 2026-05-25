# Como Rodar no Android

Este guia explica como rodar o app Android em:

- emulador Android
- celular Android via USB com `adb reverse`
- celular Android via rede Wi-Fi

## Pre-requisito

Antes de abrir o app, suba o backend Grails:

```powershell
cd C:\Users\Ryzen\AndroidStudioProjects\RickAndMortyApp\backend-grails
$env:DB_USERNAME="root"
$env:DB_PASSWORD="sua_senha_do_mysql"
.\gradlew.bat bootRun
```

Credenciais de login:

- e-mail: `admin@empresa.com`
- senha: `123456`

## 1. Emulador Android

Use este valor em `app/src/main/java/com/example/rickandmortyapp/util/Constants.java`:

```java
public static final String BACKEND_BASE_URL = "http://10.0.2.2:8080";
```

Passos:

1. Abra o projeto no Android Studio.
2. Sincronize o Gradle.
3. Inicie um emulador Android.
4. Confirme que o backend está rodando em `localhost:8080` no PC.
5. Execute o app no emulador.
6. Siga o fluxo `SplashActivity -> HomeActivity -> LoginActivity`.
7. Faça login com:
   - `admin@empresa.com`
   - `123456`

Observação:

- `10.0.2.2` funciona apenas no emulador Android.

## 2. Celular Android via USB com adb reverse

Este é o caminho mais simples para testar em aparelho físico.

Use este valor em `app/src/main/java/com/example/rickandmortyapp/util/Constants.java`:

```java
public static final String BACKEND_BASE_URL = "http://localhost:8080";
```

Passos:

1. Ative a depuração USB no celular.
2. Conecte o aparelho ao PC por USB.
3. Confirme que o dispositivo aparece no ADB:

```powershell
adb devices
```

4. Crie o redirecionamento:

```powershell
adb reverse tcp:8080 tcp:8080
```

5. Instale e abra o app no celular.
6. Faça login com:
   - `admin@empresa.com`
   - `123456`

Observações:

- com `adb reverse`, o `localhost:8080` do celular aponta para o `localhost:8080` do PC;
- se desconectar o cabo, reiniciar o ADB ou trocar de aparelho, rode o comando novamente.

## 3. Celular Android via rede Wi-Fi

Se não quiser usar USB, use o IP local da sua máquina.

Use este valor em `app/src/main/java/com/example/rickandmortyapp/util/Constants.java`:

```java
public static final String BACKEND_BASE_URL = "http://SEU_IP_LOCAL:8080";
```

Exemplo:

```java
public static final String BACKEND_BASE_URL = "http://192.168.0.15:8080";
```

Passos:

1. Coloque o PC e o celular na mesma rede Wi-Fi.
2. Descubra o IP local do PC:

```powershell
ipconfig
```

3. Troque `SEU_IP_LOCAL` pelo IPv4 da sua máquina.
4. Garanta que o Windows Firewall permita conexões na porta `8080` em rede privada.
5. Instale e abra o app no celular.
6. Teste no navegador do celular:

```text
http://SEU_IP_LOCAL:8080/api/funcionarios
```

7. Se responder, faça login no app com:
   - `admin@empresa.com`
   - `123456`

## Resumo rápido das URLs

- Emulador Android: `http://10.0.2.2:8080`
- Celular via USB com `adb reverse`: `http://localhost:8080`
- Celular via rede local: `http://SEU_IP_LOCAL:8080`

## O que validar no app

Depois do login, valide:

1. Abertura do menu principal.
2. Lista de personagens carregando da API pública.
3. Filtros de personagens.
4. Perfil do personagem.
5. Lista de funcionários.
6. Criação, edição e exclusão de funcionários.
7. Captura de foto no perfil do personagem.
