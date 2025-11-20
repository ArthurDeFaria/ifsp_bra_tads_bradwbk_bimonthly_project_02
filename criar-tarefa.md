# Caso de Uso: Criar Nova Tarefa

**Ator Principal:** Usuário

## Pré-condições:
- O usuário deve estar autenticado no sistema (implícito pela necessidade de um `userId`).
- A categoria e as tags, se informadas, devem existir previamente no sistema.

## Fluxo Principal

**Usuário:** Inicia o processo de criação de uma nova tarefa.
**Sistema:** Apresenta a interface (tela/formulário) para a inserção dos dados da tarefa.
**Usuário:** Preenche as informações da tarefa e solicita a sua criação.
- `title` (Título - Obrigatório)
- `description` (Descrição - Opcional)
- `userId` (ID do Usuário - Obrigatório)
- `categoryId` (ID da Categoria - Opcional)
- `tagIds` (Lista de IDs de Tags - Opcional)
- `dueDate` (Data de Vencimento - Opcional)
- `location` (Localização - Opcional)
**Sistema:** Valida os dados. Cria e armazena a nova tarefa. Em seguida, retorna uma confirmação de sucesso com os dados da tarefa recém-criada.

## Fluxos Alternativos:

### A. Dados Inválidos

**Usuário:** Inicia o processo de criação de uma nova tarefa.
**Sistema:** Apresenta a interface (tela/formulário) para a inserção dos dados da tarefa.
**Usuário:** Preenche as informações da tarefa, mas não informa um dado obrigatório (ex: `title`).
**Sistema:** Valida os dados, identifica a inconsistência e retorna uma mensagem de erro informando o motivo da falha (ex: "O título é obrigatório.").

## Pós-condições:
- Uma nova tarefa é persistida no banco de dados, associada ao usuário correspondente.

## Regras de Negócio (RN):

**RN1: Título da Tarefa Único por Usuário:** O título de uma tarefa deve ser único para um determinado usuário. Não é permitido que um usuário tenha duas tarefas com o mesmo título.
**RN2: Data de Vencimento no Futuro:** A data de vencimento (`dueDate`) de uma tarefa, se informada, deve ser igual ou posterior à data atual. Não é permitido criar tarefas com data de vencimento no passado.
**RN3: Associação de Categoria e Tags Existentes:** A `categoryId` e os `tagIds` informados para uma tarefa devem corresponder a categorias e tags já existentes no sistema. Não é permitido associar tarefas a categorias ou tags inexistentes.

## Fluxos de Exceção:

**FE1: Título Duplicado:** Se o usuário tentar criar uma tarefa com um título que já existe para ele, o sistema rejeita a criação e retorna uma mensagem de erro informando que "Já existe uma tarefa com este título para o usuário".
**FE2: Data de Vencimento Inválida:** Se o usuário informar uma `dueDate` anterior à data atual, o sistema rejeita a criação e retorna uma mensagem de erro informando que "A data de vencimento não pode ser no passado".
**FE3: Categoria/Tag Inexistente:** Se o usuário tentar associar uma tarefa a uma `categoryId` ou `tagId` que não existe no sistema, o sistema rejeita a criação e retorna uma mensagem de erro informando que "Categoria ou Tag(s) informada(s) não encontrada(s)".

## Cancelamento de Tarefa

**Usuário:** Solicita o cancelamento de uma tarefa existente.
**Sistema:** Recebe a requisição de cancelamento para uma tarefa específica (via `PATCH /api/tasks/{id}/cancel`).
**Sistema:** Marca a tarefa como cancelada, preenchendo o campo `canceledAt` com a data e hora atuais. Tarefas canceladas não são mais retornadas nas listagens padrão.
**Sistema:** Retorna uma confirmação de sucesso (HTTP 204 No Content).

### Regras de Negócio para Cancelamento:

**RN4: Tarefa Concluída Não Pode Ser Cancelada:** Uma tarefa que já foi marcada como concluída (`done = true`) não pode ser cancelada.
**RN5: Tarefas Canceladas Não São Listadas:** Tarefas que possuem o campo `canceledAt` preenchido não devem ser exibidas nas listagens padrão de tarefas (ex: `GET /api/tasks`).