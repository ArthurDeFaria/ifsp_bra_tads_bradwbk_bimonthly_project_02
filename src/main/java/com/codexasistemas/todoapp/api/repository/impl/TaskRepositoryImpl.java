package com.codexasistemas.todoapp.api.repository.impl;

import com.codexasistemas.todoapp.api.model.Task;
import com.codexasistemas.todoapp.api.repository.interfaces.TaskRepository;
import com.codexasistemas.todoapp.api.repository.jpa.TaskJpaRepository;

import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class TaskRepositoryImpl implements TaskRepository {

    private final TaskJpaRepository jpa;

    public TaskRepositoryImpl(TaskJpaRepository jpa) {
        this.jpa = jpa;
    }

    @Override
    public Task save(Task task) {
        return jpa.save(task);
    }

    @Override
    public Optional<Task> findById(Long id) {
        return jpa.findById(id);
    }

    @Override
    public List<Task> findAll() {
        return jpa.findByCanceledAtIsNull();
    } 

    @Override
    public void deleteById(Long id) {
        jpa.deleteById(id);
    }

    @Override
    public List<Task> findByUserId(Long userId) {
        return jpa.findByUserIdAndCanceledAtIsNull(userId);
    }
}