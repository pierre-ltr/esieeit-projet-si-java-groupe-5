package com.esieeit.projetsi.domain.model;

import com.esieeit.projetsi.domain.validation.Validators;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.time.Instant;
import java.util.Objects;

/**
 * Domain entity representing a comment posted on a task.
 */
@Entity
@Table(name = "comments")
public class Comment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Size(min = 1, max = 500)
    @Column(name = "content", nullable = false, length = 500)
    private String content;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "task_id", nullable = false, foreignKey = @ForeignKey(name = "fk_comments_task"))
    private Task task;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "author_id", nullable = false, foreignKey = @ForeignKey(name = "fk_comments_author"))
    private User author;
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    protected Comment() {
    }

    public Comment(String content, Task task, User author) {
        setContent(content);
        setTask(task);
        setAuthor(author);
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getContent() {
        return content;
    }

    public final void setContent(String content) {
        this.content = Validators.requireNonBlank(content, "comment.content", 1, 500);
    }

    public Task getTask() {
        return task;
    }

    public final void setTask(Task task) {
        Validators.requireNonNull(task, "comment.task");
        this.task = task;
    }

    public User getAuthor() {
        return author;
    }

    public final void setAuthor(User author) {
        Validators.requireNonNull(author, "comment.author");
        this.author = author;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    @PrePersist
    private void onCreate() {
        if (createdAt == null) {
            createdAt = Instant.now();
        }
    }

    @Override
    public String toString() {
        return "Comment{id=" + id + ", author=" + author.getUsername() + ", content='" + content + "'}";
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof Comment other)) {
            return false;
        }
        return Objects.equals(id, other.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
