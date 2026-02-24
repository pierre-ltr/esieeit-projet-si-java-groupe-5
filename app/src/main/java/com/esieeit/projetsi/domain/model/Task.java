package com.esiee.project.domain.model;

import com.esiee.project.domain.enums.TaskStatus;
import com.esiee.project.domain.enums.TaskPriority;
import com.esiee.project.domain.exception.ValidationException;
import com.esiee.project.domain.validation.Validators;

public class Task {

    private Long id;
    private String title;
    private TaskStatus status;
    private TaskPriority priority;

    public Task(String title, TaskStatus status, TaskPriority priority) {

        if (title == null || title.isBlank()) {
            throw new ValidationException("Le titre ne peut pas être vide");
        }

        if (priority == null) {
            throw new ValidationException("La priorité ne peut pas être nulle");
        }

        this.title = title;
        this.status = status != null ? status : TaskStatus.TODO;
        this.priority = priority;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) { // seulement pour tests/démo
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public TaskStatus getStatus() {
        return status;
    }

    public TaskPriority getPriority() {
        return priority;
    }

    @Override
    public String toString() {
        return "Task{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", status=" + status +
                ", priority=" + priority +
                '}';
    }
}

public void archive() {
    if (this.status == TaskStatus.ARCHIVED) {
        throw new BusinessRuleException("La tâche est déjà archivée");
    }
    this.status = TaskStatus.ARCHIVED;
}


this.title = Validators.requireNonBlank(title, "Titre", 3, 100);
this.priority = Validators.requireNonNull(priority, "Priorité");