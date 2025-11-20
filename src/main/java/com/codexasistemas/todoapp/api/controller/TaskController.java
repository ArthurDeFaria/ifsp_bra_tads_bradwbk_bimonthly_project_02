package com.codexasistemas.todoapp.api.controller;

import com.codexasistemas.todoapp.api.dto.task.TaskRequestDto;
import com.codexasistemas.todoapp.api.dto.task.TaskResponseDto;
import com.codexasistemas.todoapp.api.service.interfaces.TaskService;

import jakarta.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/tasks")
public class TaskController {

    @Autowired
    private TaskService taskService;

    @GetMapping
    public List<TaskResponseDto> findAll(@RequestParam(required = true) Long userId) {
        return taskService.findByUserId(userId);
    }

    @GetMapping("/{id}")
    public ResponseEntity<TaskResponseDto> findById(@PathVariable Long id) {
        return taskService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<TaskResponseDto> create(@Valid @RequestBody TaskRequestDto taskRequest) {
        TaskResponseDto dto = taskService.create(taskRequest);
        return ResponseEntity.created(URI.create("/tasks/" + dto.id())).body(dto);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<TaskResponseDto> delete(@PathVariable Long id) {
        TaskResponseDto deletedTask = taskService.delete(id);
        return ResponseEntity.ok(deletedTask);
    }

    @PutMapping("/{id}")
    public ResponseEntity<TaskResponseDto> update(
            @PathVariable Long id,
            @Valid @RequestBody TaskRequestDto taskRequest) {
        TaskResponseDto updatedTask = taskService.update(id, taskRequest);
        return ResponseEntity.ok(updatedTask);
    }

    @PatchMapping("/{id}/toggle")
    public ResponseEntity<TaskResponseDto> toggleStatus(@PathVariable Long id) {
        TaskResponseDto toggledTask = taskService.toggleStatus(id);
        return ResponseEntity.ok(toggledTask);
    }

    @PatchMapping("/{id}/cancel")
    public ResponseEntity<Void> cancel(@PathVariable Long id) {
        taskService.cancel(id);
        return ResponseEntity.noContent().build();
    }
}