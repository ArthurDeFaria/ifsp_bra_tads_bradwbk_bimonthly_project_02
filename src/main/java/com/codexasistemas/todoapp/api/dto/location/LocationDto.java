package com.codexasistemas.todoapp.api.dto.location;

import jakarta.validation.constraints.NotNull;

public record LocationDto(
    Double latitude,

    Double longitude,

    String locationName,

    String locationDescription
) {} 