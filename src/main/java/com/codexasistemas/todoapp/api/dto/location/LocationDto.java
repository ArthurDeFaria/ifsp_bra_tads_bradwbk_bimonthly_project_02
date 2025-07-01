package com.codexasistemas.todoapp.api.dto.location;

public record LocationDto(
        Double latitude,

        Double longitude,

        String locationName,

        String locationDescription) {
}