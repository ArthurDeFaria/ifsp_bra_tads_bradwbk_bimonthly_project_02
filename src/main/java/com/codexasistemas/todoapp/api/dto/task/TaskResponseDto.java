package com.codexasistemas.todoapp.api.dto.task;

import com.codexasistemas.todoapp.api.dto.location.LocationDto;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public record TaskResponseDto(
    Long id,
    String title,
    String description,
    boolean done,
    String category,
    List<String> tags,
    LocalDateTime createdAt,
    LocalDate dueDate,
    LocalDateTime canceledAt,
    LocationDto location
) {}