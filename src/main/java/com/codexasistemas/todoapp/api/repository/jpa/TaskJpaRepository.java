package com.codexasistemas.todoapp.api.repository.jpa;

import com.codexasistemas.todoapp.api.model.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface TaskJpaRepository extends JpaRepository<Task, Long> {
    List<Task> findByUserIdAndCanceledAtIsNull(Long userId);
    List<Task> findByCanceledAtIsNull();
}