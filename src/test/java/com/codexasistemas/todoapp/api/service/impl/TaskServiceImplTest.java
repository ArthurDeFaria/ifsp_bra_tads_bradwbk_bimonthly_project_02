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

@SpringBootTest
@Transactional
public class TaskServiceImplTest {

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

    private User testUser;
    private Category testCategory;
    private Tag testTag;

    @BeforeEach
    void setUp() {
        taskJpaRepository.deleteAll();
        tagJpaRepository.deleteAll();
        categoryJpaRepository.deleteAll();
        userJpaRepository.deleteAll();

        testUser = new User();
        testUser.setEmail("testuser@example.com");
        testUser.setPassword("password");
        testUser.setName("Test User");
        testUser = userJpaRepository.save(testUser);

        testCategory = new Category();
        testCategory.setName("Test Category");
        testCategory.setUser(testUser);
        testCategory = categoryJpaRepository.save(testCategory);

        testTag = new Tag();
        testTag.setName("Test Tag");
        testTag.setUser(testUser);
        testTag = tagJpaRepository.save(testTag);
    }

    @Test
    void testMainFlow_taskCreatedAndCompletedSuccessfully() {
        TaskRequestDto request = new TaskRequestDto(
                "Test Task - Main Flow",
                "A description for the main flow test task.",
                testUser.getId(),
                testCategory.getId(),
                Collections.emptyList(),
                null,
                null
        );

        TaskResponseDto createdTask = taskService.create(request);

        assertNotNull(createdTask);
        assertNotNull(createdTask.id());
        assertEquals("Test Task - Main Flow", createdTask.title());
        assertFalse(createdTask.done(), "A tarefa deve ser criada com status 'A FAZER' (done=false)");
        assertNull(createdTask.canceledAt(), "A tarefa não deve ser criada como cancelada");

        TaskResponseDto updatedTask = taskService.toggleStatus(createdTask.id());

        assertNotNull(updatedTask);
        assertTrue(updatedTask.done(), "A tarefa deve ser marcada como 'FEITA' (done=true)");
        assertNull(updatedTask.canceledAt(), "A tarefa feita não deve ser cancelada");
    }

    @Test
    void testAlternativeFlow_taskWithTagsCreatedAndCompletedSuccessfully() {
        TaskRequestDto request = new TaskRequestDto(
                "Test Task - With Tags",
                "A description for the task with tags.",
                testUser.getId(),
                testCategory.getId(),
                List.of(testTag.getId()),
                null,
                null
        );

        TaskResponseDto createdTask = taskService.create(request);

        assertNotNull(createdTask);
        assertNotNull(createdTask.id());
        assertEquals("Test Task - With Tags", createdTask.title());
        assertFalse(createdTask.tags().isEmpty(), "A tarefa deve ser criada com a tag informada");
        assertEquals(testTag.getName(), createdTask.tags().get(0));
        assertFalse(createdTask.done(), "A tarefa deve ser criada com status 'A FAZER' (done=false)");

        TaskResponseDto updatedTask = taskService.toggleStatus(createdTask.id());

        assertNotNull(updatedTask);
        assertTrue(updatedTask.done(), "A tarefa deve ser marcada como 'FEITA' (done=true)");
        assertFalse(updatedTask.tags().isEmpty(), "A tarefa deve manter suas tags após ser concluída");
    }

    @Test
    void testAlternativeFlow_taskCanceledSuccessfully() {
        TaskRequestDto request = new TaskRequestDto(
                "Test Task - To Be Canceled",
                "A description for the task to be canceled.",
                testUser.getId(),
                testCategory.getId(),
                null,
                null,
                null
        );

        TaskResponseDto createdTask = taskService.create(request);

        assertNotNull(createdTask);
        assertNotNull(createdTask.id());
        assertFalse(createdTask.done(), "A tarefa deve ser criada com status 'A FAZER' (done=false)");
        assertNull(createdTask.canceledAt(), "A tarefa não deve ser criada como cancelada");

        taskService.cancel(createdTask.id());

        TaskResponseDto canceledTask = taskService.findById(createdTask.id()).orElseThrow();

        assertNotNull(canceledTask.canceledAt(), "A tarefa deve ser marcada como 'CANCELADA' (canceledAt != null)");
        assertFalse(canceledTask.done(), "A tarefa cancelada não deve ser marcada como 'FEITA'");
    }

    @Test
    void testExceptionFlow_invalidUserThrowsException() {
        TaskRequestDto request = new TaskRequestDto(
                "Task with Invalid User",
                "Description for invalid user task.",
                999L,
                testCategory.getId(),
                Collections.emptyList(),
                null,
                null
        );

        assertThrows(IllegalArgumentException.class, () -> taskService.create(request),
                "Deve lançar IllegalArgumentException para usuário inválido.");
    }

    @Test
    void testExceptionFlow_invalidTaskDataThrowsException() {
        TaskRequestDto request = new TaskRequestDto(
                "",
                "Description for invalid data task.",
                testUser.getId(),
                testCategory.getId(),
                Collections.emptyList(),
                null,
                null
        );

        assertThrows(IllegalArgumentException.class, () -> taskService.create(request),
                "Deve lançar IllegalArgumentException para dados de tarefa inválidos (título em branco).");
    }
}
