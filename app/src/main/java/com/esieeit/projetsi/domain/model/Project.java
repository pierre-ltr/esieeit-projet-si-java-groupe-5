 package com.esiee.project.domain.model;

 import java.util.Objects;
 import com.esiee.project.domain.validation.Validators;

 public class Project {

     private Long id;
     private String name;
     private String description;
     private User owner;

     public Project(String name, String description, User owner) {
         setName(name);
         setDescription(description);
         setOwner(owner);
     }

     public Long getId() { return id; }
     public void setId(Long id) { this.id = id; }

     public String getName() { return name; }
     public final void setName(String name) {
         this.name = Validators.requireNonBlank(name, "project.name", 1, 80);
     }

     public String getDescription() { return description; }
     public final void setDescription(String description) {
         if (description == null) {
             this.description = null;
             return;
         }
         String d = description.trim();
         if (d.isEmpty()) {
             this.description = null;
             return;
         }
         this.description = Validators.requireSize(d, "project.description", 0, 500);
     }

     public User getOwner() { return owner; }
     public final void setOwner(User owner) {
         Validators.requireNonNull(owner, "project.owner");
         this.owner = owner;
     }

     public void rename(String newName) {
         setName(newName);
     }

     @Override
     public String toString() {
         return "Project{id=" + id + ", name='" + name + "', owner=" + owner.getUsername() + "}";
     }

     @Override
     public boolean equals(Object o) {
         if (this == o) return true;
         if (o == null || getClass() != o.getClass()) return false;
         Project project = (Project) o;
         return Objects.equals(id, project.id);
     }

     @Override
     public int hashCode() {
         return Objects.hash(id);
     }
 }