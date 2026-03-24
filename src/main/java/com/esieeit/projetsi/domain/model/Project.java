package com.esieeit.projetsi.domain.model;

import com.esieeit.projetsi.domain.enums.ProjectStatus;
import com.esieeit.projetsi.domain.exception.BusinessRuleException;
import com.esieeit.projetsi.domain.validation.Validators;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * Domain entity representing a project owned by a user.
 */
@Entity
@Table(name = "projects")
public class Project {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Size(min = 1, max = 80)
    @Column(name = "name", nullable = false, length = 80)
    private String name;

    @Size(max = 500)
    @Column(name = "description", length = 500)
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 30)
    private ProjectStatus status = ProjectStatus.DRAFT;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "owner_id", nullable = false, foreignKey = @ForeignKey(name = "fk_projects_owner"))
    private User owner;

    @OneToMany(mappedBy = "project", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private final List<Task> tasks = new ArrayList<>();
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;
    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    protected Project() {
    }

    public Project(String name, String description, User owner) {
        setName(name);
        setDescription(description);
        setOwner(owner);
        this.status = ProjectStatus.DRAFT;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public final void setName(String name) {
        this.name = Validators.requireNonBlank(name, "project.name", 1, 80);
        touch();
    }

    public String getDescription() {
        return description;
    }

    public final void setDescription(String description) {
        if (description == null) {
            this.description = null;
            touch();
            return;
        }
        String normalized = description.trim();
        if (normalized.isEmpty()) {
            this.description = null;
            touch();
            return;
        }
        this.description = Validators.requireSize(normalized, "project.description", 1, 500);
        touch();
    }

    public ProjectStatus getStatus() {
        return status;
    }

    public void setStatus(ProjectStatus status) {
        this.status = status == null ? ProjectStatus.DRAFT : status;
        touch();
    }

    public User getOwner() {
        return owner;
    }

    public final void setOwner(User owner) {
        Validators.requireNonNull(owner, "project.owner");
        this.owner = owner;
        touch();
    }

    public List<Task> getTasks() {
        return Collections.unmodifiableList(tasks);
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    /**
     * Business operation to rename a project.
     */
    public void rename(String newName) {
        setName(newName);
    }

    /**
     * Adds a task that must already belong to this project.
     */
    public void addTask(Task task) {
        Validators.requireNonNull(task, "project.task");
        if (task.getProject() != this) {
            throw new BusinessRuleException("task.project must reference this project");
        }
        this.tasks.add(task);
        touch();
    }

    private void touch() {
        this.updatedAt = Instant.now();
    }

    @PrePersist
    private void onCreate() {
        if (createdAt == null) {
            createdAt = Instant.now();
        }
        if (updatedAt == null) {
            updatedAt = createdAt;
        }
    }

    @PreUpdate
    private void onUpdate() {
        updatedAt = Instant.now();
    }

    @Override
    public String toString() {
        return "Project{id=" + id + ", name='" + name + "', owner=" + owner.getUsername() + "}";
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof Project other)) {
            return false;
        }
        return Objects.equals(id, other.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
