package com.esieeit.projetsi.domain.model;

import com.esieeit.projetsi.domain.enums.UserRole;
import com.esieeit.projetsi.domain.exception.ValidationException;
import com.esieeit.projetsi.domain.validation.Validators;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.time.Instant;
import java.util.Collections;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/**
 * Domain entity representing an authenticated user.
 */
@Entity
@Table(
        name = "users",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_users_email", columnNames = "email"),
                @UniqueConstraint(name = "uk_users_username", columnNames = "username")
        }
)
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Email
    @NotBlank
    @Size(max = 254)
    @Column(name = "email", nullable = false, length = 254)
    private String email;

    @NotBlank
    @Size(min = 3, max = 30)
    @Column(name = "username", nullable = false, length = 30)
    private String username;

    @Size(min = 10, max = 255)
    @Column(name = "password_hash", length = 255)
    private String passwordHash;

    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(name = "user_roles", joinColumns = @JoinColumn(name = "user_id"))
    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false, length = 30)
    private Set<UserRole> roles = new HashSet<>();

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @OneToMany(mappedBy = "owner", fetch = FetchType.LAZY)
    private Set<Project> ownedProjects = new HashSet<>();

    @OneToMany(mappedBy = "assignee", fetch = FetchType.LAZY)
    private Set<Task> assignedTasks = new HashSet<>();

    @OneToMany(mappedBy = "author", fetch = FetchType.LAZY)
    private Set<Comment> comments = new HashSet<>();

    protected User() {
    }

    public User(String email, String username, Set<UserRole> roles) {
        setEmail(email);
        setUsername(username);
        setRoles(roles);
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public final void setEmail(String email) {
        this.email = Validators.requireEmail(email, "user.email");
    }

    public String getUsername() {
        return username;
    }

    public final void setUsername(String username) {
        this.username = Validators.requireNonBlank(username, "user.username", 3, 30);
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(String passwordHash) {
        if (passwordHash != null) {
            Validators.requireSize(passwordHash, "user.passwordHash", 10, 255);
        }
        this.passwordHash = passwordHash;
    }

    public Set<UserRole> getRoles() {
        return Collections.unmodifiableSet(roles);
    }

    public final void setRoles(Set<UserRole> roles) {
        Validators.requireNonNull(roles, "user.roles");
        if (roles.isEmpty()) {
            throw new ValidationException("user.roles", "must contain at least one role");
        }
        this.roles = new HashSet<>(roles);
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Set<Project> getOwnedProjects() {
        return Collections.unmodifiableSet(ownedProjects);
    }

    public Set<Task> getAssignedTasks() {
        return Collections.unmodifiableSet(assignedTasks);
    }

    public Set<Comment> getComments() {
        return Collections.unmodifiableSet(comments);
    }

    /**
     * Checks if the user owns a given role.
     */
    public boolean hasRole(UserRole role) {
        Validators.requireNonNull(role, "user.role");
        return roles.contains(role);
    }

    @PrePersist
    private void onCreate() {
        if (createdAt == null) {
            createdAt = Instant.now();
        }
    }

    @Override
    public String toString() {
        return "User{id=" + id + ", email='" + email + "', username='" + username + "', roles=" + roles + "}";
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof User other)) {
            return false;
        }
        return Objects.equals(id, other.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
