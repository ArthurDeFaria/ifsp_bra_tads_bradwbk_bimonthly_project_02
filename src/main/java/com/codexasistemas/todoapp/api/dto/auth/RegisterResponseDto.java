package com.codexasistemas.todoapp.api.dto.auth;

public record RegisterResponseDto(Long id, String name, String email) {
    
    public RegisterResponseDto {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("O ID deve ser um número positivo.");
        }
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("O nome não pode ser nulo ou vazio.");
        }
        if (email == null || email.isBlank()) {
            throw new IllegalArgumentException("O email não pode ser nulo ou vazio.");
        }
    }
    
}
