# ifsp_bra_tads_bradwbk_bimonthly_project_02

## Descrição do Projeto
API RESTful desenvolvida para a disciplina de Desenvolvimento Web Back-end (BRADWBK), IFSP, semestre 2025/1. O sistema permite o gerenciamento de tarefas, categorias e tags, com autenticação de usuários, integração a banco de dados relacional e suporte a testes de performance e balanceamento de carga.

### Tema da aplicação
Gerenciamento de Tarefas (ToDo App)

### Integrantes do grupo
- ARTHUR DE FARIA BP3038289
- INÁCIO FERNANDES SANTANA BP3039307
- JOÃO PAULO PEREIRA COSTA BP3039331
- LYBIO CROTON DE MORAES JUNIOR BP303934X

### Entidades principais
- **User**: Usuário do sistema, pode possuir várias tarefas, categorias e tags.
- **Task**: Tarefa criada pelo usuário, pode pertencer a uma categoria, conter várias tags e estar associada a uma localização.
- **Category**: Categoria criada pelo usuário para organizar tarefas (um-para-muitos com Task).
- **Tag**: Tag criada pelo usuário para classificar tarefas (muitos-para-muitos com Task).
- **Location**: Localização associada a uma tarefa (um-para-um).

### Relacionamentos
- **User 1:N Task**: Um usuário pode ter várias tarefas.
- **User 1:N Category**: Um usuário pode ter várias categorias.
- **User 1:N Tag**: Um usuário pode ter várias tags.
- **Category 1:N Task**: Uma categoria pode ter várias tarefas.
- **Task N:M Tag**: Uma tarefa pode ter várias tags e uma tag pode estar em várias tarefas.
- **Task 1:1 Location**: Uma tarefa pode ter uma localização associada.

### Tecnologias Utilizadas
- Java 17+
- Spring Boot
- Spring Web
- JPA/Hibernate
- Banco de Dados Relacional (ex: PostgreSQL, MySQL, H2)
- NGINX (balanceamento de carga)
- Apache JMeter (testes de performance)
- Postman/JMeter/Thunder Client/Insomnia (testes funcionais)
- Git

## Endpoints da API

### Autenticação

| Verbo HTTP | Path                        | Body de Requisição | Body de Retorno         | Status Sucesso | Status Erro |
|------------|----------------------------|--------------------|-------------------------|---------------|-------------|
| POST       | /api/auth/register         | RegisterRequestDto | RegisterResponseDto     | 200           | 400/500     |
| POST       | /api/auth/login            | LoginRequestDto    | LoginResponseDto        | 200           | 401/400     |

**Exemplo de resposta de autenticação:**
```json
{
  "id": 1,
  "name": "Test User",
  "email": "test@test.com"
}
```

### Usuários

| Verbo HTTP | Path                        | Body de Requisição | Body de Retorno         | Status Sucesso | Status Erro |
|------------|----------------------------|--------------------|-------------------------|---------------|-------------|
| GET        | /api/users                 | -                  | List<UserResponseDto>   | 200           | 404/500     |
| GET        | /api/users/{id}            | -                  | UserResponseDto         | 200           | 404/500     |
| POST       | /api/users                 | UserRequestDto     | UserResponseDto         | 200           | 400/500     |
| PUT        | /api/users/{id}            | UserRequestDto     | UserResponseDto         | 200           | 400/404     |
| DELETE     | /api/users/{id}            | -                  | UserResponseDto         | 200           | 404/500     |
| GET        | /api/users/{id}/categories | -                  | List<CategoryWithTaskCountDto> | 200   | 404/500     |
| GET        | /api/users/{id}/tags       | -                  | List<TagWithTaskCountDto>      | 200   | 404/500     |
| GET        | /api/users/{id}/tasks      | -                  | List<TaskResponseDto>         | 200   | 404/500     |

### Tarefas

| Verbo HTTP | Path                        | Body de Requisição | Body de Retorno         | Status Sucesso | Status Erro |
|------------|----------------------------|--------------------|-------------------------|---------------|-------------|
| GET        | /api/tasks?userId=         | -                  | List<TaskResponseDto>         | 200   | 404/500     |
| GET        | /api/tasks/{id}            | -                  | TaskResponseDto               | 200   | 404/500     |
| POST       | /api/tasks                 | TaskRequestDto     | TaskResponseDto               | 201   | 400/500     |
| PUT        | /api/tasks/{id}            | TaskRequestDto     | TaskResponseDto               | 200   | 400/404     |
| DELETE     | /api/tasks/{id}            | -                  | TaskResponseDto               | 200   | 404/500     |
| PATCH      | /api/tasks/{id}/toggle     | -                  | TaskResponseDto               | 200   | 404/500     |
| PATCH      | /api/tasks/{id}/cancel     | -                  | -                             | 204   | 404/500     |

> #### Corpo da Requisição de Tarefa (`TaskRequestDto`)
>
> Ao criar (`POST`) ou atualizar (`PUT`) uma tarefa, o corpo da requisição deve conter os seguintes campos:
>
> -   `title` (String, **Obrigatório**): Título da tarefa.
> -   `userId` (Long, **Obrigatório**): ID do usuário ao qual a tarefa pertence.
> -   `categoryId` (Long, **Obrigatório**): ID da categoria à qual a tarefa pertence.
> -   `description` (String, Opcional): Descrição da tarefa.
> -   `tagIds` (List<Long>, Opcional): Lista de IDs de tags para associar à tarefa.
> -   `dueDate` (LocalDate, Opcional): Data de vencimento da tarefa (formato: `YYYY-MM-DD`).
> -   `location` (LocationDto, Opcional): Objeto com dados de localização.
>
> **Exemplo de corpo para criar uma nova tarefa:**
> ```json
> {
>   "title": "Finalizar o relatório bimestral",
>   "userId": 1,
>   "categoryId": 2,
>   "description": "O relatório precisa incluir os gráficos de performance.",
>   "tagIds": [1, 3],
>   "dueDate": "2025-12-31"
> }
> ```

### Categorias

| Verbo HTTP | Path                        | Body de Requisição | Body de Retorno         | Status Sucesso | Status Erro |
|------------|----------------------------|--------------------|-------------------------|---------------|-------------|
| GET        | /api/categories?userId=    | -                  | List<CategoryResponseDto>      | 200   | 404/500     |
| GET        | /api/categories/{id}       | -                  | CategoryResponseDto           | 200   | 404/500     |
| POST       | /api/categories            | CategoryRequestDto | CategoryResponseDto           | 201   | 400/500     |
| PUT        | /api/categories/{id}       | CategoryRequestDto | CategoryResponseDto           | 200   | 400/404     |
| DELETE     | /api/categories/{id}       | -                  | CategoryResponseDto           | 200   | 404/500     |
| GET        | /api/categories/{id}/tasks | -                  | List<TaskResponseDto>         | 200   | 404/500     |

### Tags

| Verbo HTTP | Path                        | Body de Requisição | Body de Retorno         | Status Sucesso | Status Erro |
|------------|----------------------------|--------------------|-------------------------|---------------|-------------|
| GET        | /api/tags?userId=          | -                  | List<TagResponseDto>          | 200   | 404/500     |
| GET        | /api/tags/{id}             | -                  | TagResponseDto                | 200   | 404/500     |
| POST       | /api/tags                  | TagRequestDto      | TagResponseDto                | 201   | 400/500     |
| PUT        | /api/tags/{id}             | TagRequestDto      | TagResponseDto                | 200   | 400/404     |
| DELETE     | /api/tags/{id}             | -                  | TagResponseDto                | 200   | 404/500     |

> Para detalhes completos dos contratos, consulte os DTOs em `src/main/java/com/codexasistemas/todoapp/api/dto/`.

## Como rodar o projeto

1. Clone o repositório e instale as dependências com Maven:
   ```bash
   mvn clean install
   ```
2. Crie um arquivo `.env` na raiz do projeto com as seguintes variáveis:
   ```env
   DB_URL=jdbc:postgresql://localhost:5432/seubanco
   DB_USER=seuusuario
   DB_PASS=suasenha
   ```
3. Configure o banco de dados em `src/main/resources/application.properties` (já utiliza as variáveis do `.env`).
4. Rode a aplicação:
   ```bash
   mvn spring-boot:run
   ```
5. Acesse a API em `http://localhost:8080`.

## Licença
Este projeto está licenciado sob os termos da licença MIT. Veja o arquivo [LICENSE](LICENSE) para mais detalhes.
