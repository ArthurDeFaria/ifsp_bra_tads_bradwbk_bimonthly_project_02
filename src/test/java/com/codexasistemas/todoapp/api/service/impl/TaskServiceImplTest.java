package com.codexasistemas.todoapp.api.service.impl;

import com.codexasistemas.todoapp.api.dto.task.TaskRequestDto;
import com.codexasistemas.todoapp.api.dto.task.TaskResponseDto;
import com.codexasistemas.todoapp.api.model.Category;
import com.codexasistemas.todoapp.api.model.Tag;
import com.codexasistemas.todoapp.api.model.User;
import com.codexasistemas.todoapp.api.repository.jpa.CategoryJpaRepository;
import com.codexasistemas.todoapp.api.repository.jpa.TagJpaRepository;
import com.codexasistemas.todoapp.api.repository.jpa.TaskJpaRepository;
import com.codexasistemas.todoapp.api.repository.jpa.UserJpaRepository;
import com.codexasistemas.todoapp.api.service.interfaces.TaskService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Classe de teste de integração para TaskServiceImpl.
 *
 * @SpringBootTest: Esta anotação carrega o contexto completo da aplicação Spring Boot,
 * permitindo testar a interação entre diferentes camadas (serviços, repositórios, etc.).
 * É ideal para garantir que os componentes funcionem juntos como esperado.
 *
 * @Transactional: Garante que cada método de teste seja executado dentro de uma transação
 * que é revertida (rollback) ao final. Isso isola os testes uns dos outros,
 * assegurando que o estado do banco de dados modificado por um teste não afete os subsequentes.
 */
@SpringBootTest
@Transactional
public class TaskServiceImplTest {

    // Injeção de dependências necessárias para os testes.
    // O Spring injeta as implementações concretas (beans) gerenciadas em seu contexto.
    @Autowired
    private TaskService taskService;

    @Autowired
    private UserJpaRepository userJpaRepository;

    @Autowired
    private CategoryJpaRepository categoryJpaRepository;

    @Autowired
    private TagJpaRepository tagJpaRepository;

    @Autowired
    private TaskJpaRepository taskJpaRepository;

    // Entidades de pré-condição que serão usadas como base para os testes.
    private User testUser;
    private Category testCategory;
    private Tag testTag;

    /**
     * Método de configuração executado antes de cada teste (@BeforeEach).
     * Prepara o ambiente de teste, garantindo um estado limpo e consistente.
     */
    @BeforeEach
    void setUp() {
        // Limpa todos os dados dos repositórios para evitar interferência entre os testes.
        // A ordem de exclusão é importante para respeitar as restrições de chave estrangeira.
        taskJpaRepository.deleteAll();
        tagJpaRepository.deleteAll();
        categoryJpaRepository.deleteAll();
        userJpaRepository.deleteAll();

        // Cria e persiste as entidades necessárias como pré-condição para os testes de tarefa.
        // Um usuário e uma categoria são obrigatórios para criar uma tarefa.
        testUser = new User();
        testUser.setEmail("testuser@example.com");
        testUser.setPassword("password");
        testUser.setName("Test User");
        testUser = userJpaRepository.save(testUser);

        testCategory = new Category();
        testCategory.setName("Test Category");
        testCategory.setUser(testUser);
        testCategory = categoryJpaRepository.save(testCategory);

        // Uma tag é criada para ser usada no teste de fluxo alternativo com tags.
        testTag = new Tag();
        testTag.setName("Test Tag");
        testTag.setUser(testUser);
        testTag = tagJpaRepository.save(testTag);
    }

    /**
     * CT01: Testa o fluxo principal de criação e conclusão de uma tarefa.
     * Cenário: Uma tarefa válida é criada sem tags e, em seguida, marcada como "FEITA".
     */
    @Test
    void testMainFlow_taskCreatedAndCompletedSuccessfully() {
        // Arrange: Prepara o DTO (Data Transfer Object) com os dados da nova tarefa.
        TaskRequestDto request = new TaskRequestDto(
                "Test Task - Main Flow",
                "A description for the main flow test task.",
                testUser.getId(),
                testCategory.getId(),
                Collections.emptyList(), // Nenhuma tag é informada neste fluxo.
                null,
                null
        );

        // Act: Executa a ação de criar a tarefa.
        TaskResponseDto createdTask = taskService.create(request);

        // Assert: Verifica se a tarefa foi criada corretamente e com o estado inicial esperado.
        assertNotNull(createdTask);
        assertNotNull(createdTask.id(), "A tarefa criada deve ter um ID.");
        assertEquals("Test Task - Main Flow", createdTask.title());
        assertFalse(createdTask.done(), "A tarefa deve ser criada com status 'A FAZER' (done=false).");
        assertNull(createdTask.canceledAt(), "A tarefa não deve ser criada como cancelada.");

        // Act: Executa a ação de marcar a tarefa como "FEITA".
        TaskResponseDto updatedTask = taskService.toggleStatus(createdTask.id());

        // Assert: Verifica se o status da tarefa foi atualizado para "FEITA".
        assertNotNull(updatedTask);
        assertTrue(updatedTask.done(), "A tarefa deve ser marcada como 'FEITA' (done=true).");
        assertNull(updatedTask.canceledAt(), "A tarefa feita não deve ser cancelada.");
    }

    /**
     * CT02: Testa o fluxo alternativo de criação de uma tarefa com tags.
     * Cenário: Uma tarefa válida é criada com tags e, em seguida, marcada como "FEITA".
     */
    @Test
    void testAlternativeFlow_taskWithTagsCreatedAndCompletedSuccessfully() {
        // Arrange: Prepara o DTO com os dados da nova tarefa, incluindo uma lista de IDs de tags.
        TaskRequestDto request = new TaskRequestDto(
                "Test Task - With Tags",
                "A description for the task with tags.",
                testUser.getId(),
                testCategory.getId(),
                List.of(testTag.getId()), // Informa a tag criada no setup.
                null,
                null
        );

        // Act: Executa a ação de criar a tarefa.
        TaskResponseDto createdTask = taskService.create(request);

        // Assert: Verifica se a tarefa foi criada com as tags associadas.
        assertNotNull(createdTask);
        assertNotNull(createdTask.id());
        assertEquals("Test Task - With Tags", createdTask.title());
        assertFalse(createdTask.tags().isEmpty(), "A tarefa deve ser criada com a tag informada.");
        assertEquals(testTag.getName(), createdTask.tags().get(0));
        assertFalse(createdTask.done(), "A tarefa deve ser criada com status 'A FAZER' (done=false).");

        // Act: Executa a ação de marcar a tarefa como "FEITA".
        TaskResponseDto updatedTask = taskService.toggleStatus(createdTask.id());

        // Assert: Verifica se o status foi atualizado e se as tags foram mantidas.
        assertNotNull(updatedTask);
        assertTrue(updatedTask.done(), "A tarefa deve ser marcada como 'FEITA' (done=true).");
        assertFalse(updatedTask.tags().isEmpty(), "A tarefa deve manter suas tags após ser concluída.");
    }

    /**
     * CT03: Testa o fluxo alternativo de cancelamento de uma tarefa.
     * Cenário: Uma tarefa válida é criada e, em seguida, marcada como "CANCELADA".
     */
    @Test
    void testAlternativeFlow_taskCanceledSuccessfully() {
        // Arrange: Prepara o DTO com os dados da nova tarefa.
        TaskRequestDto request = new TaskRequestDto(
                "Test Task - To Be Canceled",
                "A description for the task to be canceled.",
                testUser.getId(),
                testCategory.getId(),
                null,
                null,
                null
        );

        // Act: Cria a tarefa.
        TaskResponseDto createdTask = taskService.create(request);

        // Assert: Verifica o estado inicial da tarefa.
        assertNotNull(createdTask);
        assertNotNull(createdTask.id());
        assertFalse(createdTask.done(), "A tarefa deve ser criada com status 'A FAZER' (done=false).");
        assertNull(createdTask.canceledAt(), "A tarefa não deve ser criada como cancelada.");

        // Act: Executa a ação de cancelar a tarefa.
        taskService.cancel(createdTask.id());

        // Act: Busca a tarefa novamente para verificar seu estado final.
        TaskResponseDto canceledTask = taskService.findById(createdTask.id()).orElseThrow();

        // Assert: Verifica se a tarefa foi corretamente marcada como "CANCELADA".
        assertNotNull(canceledTask.canceledAt(), "A tarefa deve ser marcada como 'CANCELADA' (canceledAt != null).");
        assertFalse(canceledTask.done(), "A tarefa cancelada não deve ser marcada como 'FEITA'.");
    }

    /**
     * CT04: Testa o fluxo de exceção para um usuário inválido.
     * Cenário: Tenta-se criar uma tarefa com um ID de usuário que não existe no banco de dados.
     */
    @Test
    void testExceptionFlow_invalidUserThrowsException() {
        // Arrange: Prepara um DTO com um ID de usuário sabidamente inexistente.
        TaskRequestDto request = new TaskRequestDto(
                "Task with Invalid User",
                "Description for invalid user task.",
                999L, // ID de usuário fictício que não existe.
                testCategory.getId(),
                Collections.emptyList(),
                null,
                null
        );

        // Act & Assert: Verifica se a chamada ao método create lança a exceção esperada.
        // A implementação do `UserService` lança `IllegalArgumentException` quando o usuário não é encontrado.
        // `assertThrows` captura e valida o tipo da exceção.
        assertThrows(IllegalArgumentException.class, () -> taskService.create(request),
                "Deve lançar IllegalArgumentException para usuário inválido.");
    }

    /**
     * CT05: Testa o fluxo de exceção para dados de tarefa inválidos.
     * Cenário: Tenta-se criar uma tarefa com um título em branco, que é um campo obrigatório.
     */
    @Test
    void testExceptionFlow_invalidTaskDataThrowsException() {
        // Arrange: Prepara um DTO com um título em branco.
        TaskRequestDto request = new TaskRequestDto(
                "", // Título em branco, violando a regra de negócio.
                "Description for invalid data task.",
                testUser.getId(),
                testCategory.getId(),
                Collections.emptyList(),
                null,
                null
        );

        // Act & Assert: Verifica se a chamada ao método create lança a exceção esperada.
        // A lógica de validação no `TaskMapper` ou na entidade `Task` deve impedir a criação
        // e lançar `IllegalArgumentException` para um título inválido.
        assertThrows(IllegalArgumentException.class, () -> taskService.create(request),
                "Deve lançar IllegalArgumentException para dados de tarefa inválidos (título em branco).");
    }
}

