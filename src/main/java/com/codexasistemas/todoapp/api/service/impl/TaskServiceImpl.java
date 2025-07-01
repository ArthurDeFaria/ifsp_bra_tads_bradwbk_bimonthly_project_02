package com.codexasistemas.todoapp.api.service.impl;

import com.codexasistemas.todoapp.api.dto.task.TaskRequestDto;
import com.codexasistemas.todoapp.api.dto.task.TaskResponseDto;
import com.codexasistemas.todoapp.api.mapper.TaskMapper;
import com.codexasistemas.todoapp.api.model.Category;
import com.codexasistemas.todoapp.api.model.Location;
import com.codexasistemas.todoapp.api.model.Tag;
import com.codexasistemas.todoapp.api.model.Task;
import com.codexasistemas.todoapp.api.model.User;
import com.codexasistemas.todoapp.api.repository.interfaces.TaskRepository;
import com.codexasistemas.todoapp.api.service.interfaces.TaskService;
import com.codexasistemas.todoapp.api.service.interfaces.CategoryService;
import com.codexasistemas.todoapp.api.service.interfaces.TagService;
import com.codexasistemas.todoapp.api.service.interfaces.UserService;

import jakarta.persistence.EntityNotFoundException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class TaskServiceImpl implements TaskService {

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private TagService tagService;

    @Override
    public List<TaskResponseDto> findAll() {
        return taskRepository.findAll().stream()
                .map(TaskMapper::toResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<TaskResponseDto> findById(Long id) {
        return taskRepository.findById(id)
                .map(TaskMapper::toResponseDto);
    }

    @Override
    public TaskResponseDto create(TaskRequestDto taskRequest) {
        User user = userService.findByIdEntity(taskRequest.userId());
        Category category = taskRequest.categoryId() != null ? categoryService.findByIdEntity(taskRequest.categoryId()) : null;
        List<Tag> tags = taskRequest.tagIds() != null ? taskRequest.tagIds().stream()
                .map(tagService::findByIdEntity)
                .collect(Collectors.toList()) : List.of();

        Task task = TaskMapper.toEntity(taskRequest, user, category, tags);
        Task savedTask = taskRepository.save(task);
        return TaskMapper.toResponseDto(savedTask);
    }

    @Override
    public TaskResponseDto delete(Long id) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Tarefa não encontrada: " + id));
        TaskResponseDto responseDto = TaskMapper.toResponseDto(task);
        taskRepository.deleteById(task.getId());
        return responseDto;
    }

    @Override
    public TaskResponseDto update(Long id, TaskRequestDto taskRequest) {
        Task existingTask = taskRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Tarefa não encontrada: " + id));

        User user = userService.findByIdEntity(taskRequest.userId());
        Category category = taskRequest.categoryId() != null ? categoryService.findByIdEntity(taskRequest.categoryId()) : null;
        List<Tag> tags = taskRequest.tagIds() != null ? taskRequest.tagIds().stream()
                .map(tagService::findByIdEntity)
                .collect(Collectors.toList()) : List.of();

        existingTask.updateTitle(taskRequest.title());
        existingTask.updateDescription(taskRequest.description());
        existingTask.assignUser(user);
        existingTask.changeCategory(category);
        existingTask.setTags(tags);
        existingTask.setDueDate(taskRequest.dueDate());

        if (taskRequest.location() != null) {
            Location location = new Location(
                    taskRequest.location().latitude(),
                    taskRequest.location().longitude(),
                    taskRequest.location().locationName(),
                    taskRequest.location().locationDescription());
            existingTask.setLocation(location);
        } else {
            existingTask.setLocation(null);
        }

        Task updatedTask = taskRepository.save(existingTask);
        return TaskMapper.toResponseDto(updatedTask);
    }

    @Override
    public List<TaskResponseDto> findByUserId(Long userId) {
        return taskRepository.findByUserId(userId).stream()
                .map(TaskMapper::toResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    public TaskResponseDto toggleStatus(Long id) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Tarefa não encontrada: " + id));
        
        task.toggleStatus();
        Task updatedTask = taskRepository.save(task);
        return TaskMapper.toResponseDto(updatedTask);
    }
}