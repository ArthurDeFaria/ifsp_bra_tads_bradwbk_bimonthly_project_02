package com.codexasistemas.todoapp.api.dto.task;

import com.codexasistemas.todoapp.api.dto.location.LocationDto;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.util.List;

public record TaskRequestDto(

        @NotBlank(message = "O título é obrigatório.") String title,

        String description,

        @NotNull(message = "O ID do usuário é obrigatório.") Long userId,

        @NotNull(message = "O ID da categoria é obrigatório.") Long categoryId,

        List<Long> tagIds,

        LocalDate dueDate,

        LocationDto location

) {
}