package com.codexasistemas.todoapp.api.mapper;

import com.codexasistemas.todoapp.api.dto.task.*;
import com.codexasistemas.todoapp.api.dto.location.LocationDto;
import com.codexasistemas.todoapp.api.model.*;

import java.util.List;

public class TaskMapper {

    public static TaskResponseDto toResponseDto(Task task) {
        LocationDto locationDto = null;
        if (task.getLocation() != null) {
            locationDto = new LocationDto(
                task.getLocation().getLatitude(),
                task.getLocation().getLongitude(),
                task.getLocation().getLocationName(),
                task.getLocation().getLocationDescription()
            );
        }

        return new TaskResponseDto(
            task.getId(),
            task.getTitle(),
            task.getDescription(),
            task.isDone(),
            task.getCategory() != null ? task.getCategory().getName() : null,
            task.getTags() != null ? task.getTags().stream().map(Tag::getName).toList() : List.of(),
            task.getCreatedAt(),
            task.getDueDate(),
            task.getCanceledAt(),
            locationDto
        );
    }

    public static Task toEntity(TaskRequestDto dto, User user, Category category, List<Tag> tags) {
        Task task = new Task();
        task.updateTitle(dto.title());
        task.updateDescription(dto.description());
        task.assignUser(user);
        task.changeCategory(category);
        task.setTags(tags);
        task.setDueDate(dto.dueDate());

        if (dto.location() != null) {
            Location location = new Location(
                dto.location().latitude(),
                dto.location().longitude(),
                dto.location().locationName(),
                dto.location().locationDescription()
            );
            task.setLocation(location);
        }

        return task;
    }
}