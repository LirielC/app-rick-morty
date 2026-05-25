# Testes Manuais

## Dispositivo utilizado

- Smartphone: Samsung Galaxy M31
- Tipo de teste: validação manual em dispositivo físico

## Objetivo

Registrar os testes manuais realizados no aplicativo para validar os principais fluxos implementados, com foco em atualização de foto de personagem e operações do módulo de funcionários.

## Cenários testados

### 1. Alteração de foto do personagem

Fluxo validado:

1. Abrir a listagem de personagens.
2. Selecionar um personagem para abrir a tela de perfil.
3. Acionar a função de câmera para alterar a foto.
4. Capturar uma nova imagem usando a câmera nativa do aparelho.
5. Retornar ao aplicativo e verificar a atualização visual da foto.

Resultado observado:

- A câmera do dispositivo foi aberta corretamente.
- A captura da imagem ocorreu normalmente.
- A foto do personagem foi atualizada no perfil após o retorno ao app.

### 2. Adição de funcionário

Fluxo validado:

1. Abrir o módulo de funcionários.
2. Acessar a tela de cadastro.
3. Preencher os campos obrigatórios.
4. Salvar o novo funcionário.
5. Confirmar o aparecimento do registro na lista.

Resultado observado:

- O formulário permitiu o cadastro normalmente.
- O novo funcionário foi adicionado com sucesso.
- O item passou a aparecer na listagem após a operação.

### 3. Edição de funcionário

Fluxo validado:

1. Selecionar um funcionário existente.
2. Abrir a tela de edição.
3. Alterar os dados desejados.
4. Salvar as alterações.
5. Conferir a atualização na lista.

Resultado observado:

- A edição do funcionário funcionou corretamente.
- Os dados alterados foram refletidos na interface após salvar.

### 4. Remoção de funcionário

Fluxo validado:

1. Abrir a lista de funcionários.
2. Selecionar a opção de excluir um registro.
3. Confirmar a remoção.
4. Verificar se o item foi removido da lista.

Resultado observado:

- A exclusão foi executada corretamente.
- O funcionário removido deixou de aparecer na listagem.

## Conclusão

Os testes manuais realizados no Samsung Galaxy M31 indicaram funcionamento adequado dos fluxos principais validados nesta etapa, especialmente:

- mudança de foto do personagem por câmera nativa;
- cadastro de funcionário;
- edição de funcionário;
- remoção de funcionário.

Este arquivo serve como registro dos testes manuais executados em dispositivo físico durante a validação do projeto.
