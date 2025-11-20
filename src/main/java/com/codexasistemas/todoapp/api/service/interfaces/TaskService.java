package com.codexasistemas.todoapp.api.service.interfaces;

import java.util.List;
import java.util.Optional;

import com.codexasistemas.todoapp.api.dto.task.TaskRequestDto;
import com.codexasistemas.todoapp.api.dto.task.TaskResponseDto;

public interface TaskService {
    List<TaskResponseDto> findAll();

    Optional<TaskResponseDto> findById(Long id);

    TaskResponseDto create(TaskRequestDto taskRequest);

    TaskResponseDto update(Long id, TaskRequestDto taskRequest);

    TaskResponseDto delete(Long id);

    List<TaskResponseDto> findByUserId(Long userId);
    
    TaskResponseDto toggleStatus(Long id);

    void cancel(Long id);
}
