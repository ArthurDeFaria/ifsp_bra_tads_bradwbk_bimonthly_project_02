package com.codexasistemas.todoapp.api.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "task")
@Getter
@NoArgsConstructor
public class Task {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    private String description;

    private boolean done = false;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "due_date")
    private LocalDate dueDate;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "category_id", nullable = true)
    private Category category;

    @ManyToMany
    @JoinTable(name = "task_tag", joinColumns = @JoinColumn(name = "task_id"), inverseJoinColumns = @JoinColumn(name = "tag_id"))
    private List<Tag> tags;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "location_id")
    private Location location;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }

    public void markAsDone() {
        if (this.done)
            throw new IllegalStateException("A tarefa já está concluída.");
        this.done = true;
    }

    public void markAsUndone() {
        if (!this.done)
            throw new IllegalStateException("A tarefa já está pendente.");
        this.done = false;
    }

    public void toggleStatus() {
        this.done = !this.done;
    }

    public void updateTitle(String newTitle) {
        if (newTitle == null || newTitle.trim().isEmpty()) {
            throw new IllegalArgumentException("Título não pode estar vazio.");
        }
        this.title = newTitle.trim();
    }

    public void updateDescription(String newDescription) {
        this.description = (newDescription != null) ? newDescription.trim() : null;
    }

    public void setDueDate(LocalDate dueDate) {
        if (dueDate != null && dueDate.isBefore(LocalDate.now())) {
            throw new IllegalArgumentException("A data de vencimento não pode estar no passado.");
        }
        this.dueDate = dueDate;
    }

    public void assignUser(User user) {
        if (user == null) {
            throw new IllegalArgumentException("Usuário não pode ser nulo.");
        }
        this.user = user;
    }

    public void changeCategory(Category newCategory) {
        this.category = newCategory;
    }

    public void setTags(List<Tag> tags) {
        this.tags = tags;
    }

    public boolean isOwnedBy(User user) {
        return this.user != null && this.user.equals(user);
    }

    public void setLocation(Location location) {
        if (location != null) {
            location.validateCoordinates();
        }
        this.location = location;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (!(o instanceof Task))
            return false;
        Task task = (Task) o;
        return Objects.equals(id, task.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}