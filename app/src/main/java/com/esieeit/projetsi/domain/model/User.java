package com.esiee.project.domain.model;

 import java.util.Collections;
 import java.util.HashSet;
 import java.util.Objects;
 import java.util.Set;

 import com.esiee.project.domain.enums.UserRole;
 import com.esiee.project.domain.validation.Validators;

 public class User {

     private Long id;
     private String email;
     private String username;
     private String passwordHash;
     private Set<UserRole> roles = new HashSet<>();

     public User(String email, String username, Set<UserRole> roles) {
         setEmail(email);
         setUsername(username);
         setRoles(roles);
     }

     public Long getId() { return id; }

      Optionnel dans ce TP : autoriser l’affectation (DB plus tard)
     public void setId(Long id) { this.id = id; }

     public String getEmail() { return email; }

     public final void setEmail(String email) {
         this.email = Validators.requireEmail(email, "email");
     }

     public String getUsername() { return username; }

     public final void setUsername(String username) {
         this.username = Validators.requireNonBlank(username, "username", 3, 30);
     }

     public String getPasswordHash() { return passwordHash; }

     public void setPasswordHash(String passwordHash) {
          Ici, on accepte null pour l’instant (auth en séance 5)
          Si non null, on impose une taille minimale
         if (passwordHash != null) {
             Validators.requireSize(passwordHash, "passwordHash", 10, 255);
         }
         this.passwordHash = passwordHash;
     }

     public Set<UserRole> getRoles() {
         return Collections.unmodifiableSet(roles);
     }

     public final void setRoles(Set<UserRole> roles) {
         Validators.requireNonNull(roles, "roles");
         if (roles.isEmpty()) {
             throw new com.esiee.project.domain.exception.ValidationException("roles doit contenir au moins un rôle");
         }
         this.roles = new HashSet<>(roles);
     }

     public boolean hasRole(UserRole role) {
         Validators.requireNonNull(role, "role");
         return roles.contains(role);
     }

     @Override
     public String toString() {
         return "User{id=" + id + ", email='" + email + "', username='" + username + "', roles=" + roles + "}";
     }

     @Override
     public boolean equals(Object o) {
         if (this == o) return true;
         if (o == null || getClass() != o.getClass()) return false;
         User user = (User) o;
         return Objects.equals(id, user.id);
     }

     @Override
     public int hashCode() {
         return Objects.hash(id);
     }
 }