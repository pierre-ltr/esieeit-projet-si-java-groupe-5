package com.esieeit.projetsi.domain.model;

import com.esieeit.projetsi.domain.enums.TaskPriority;
import com.esieeit.projetsi.domain.enums.UserRole;
import jakarta.persistence.EntityManager;
import java.util.Set;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.TestPropertySource;

@DataJpaTest
@TestPropertySource(properties = {
        "spring.jpa.hibernate.ddl-auto=create-drop",
        "spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.H2Dialect"
})
class JpaMappingSmokeTest {

    @Autowired
    private EntityManager entityManager;

    @Test
    void shouldPersistCoreDomainEntitiesWithJpaMappings() {
        User owner = new User("owner@esieeit.local", "owneruser", Set.of(UserRole.ADMIN));
        owner.setPasswordHash("hashed-password");
        entityManager.persist(owner);

        User assignee = new User("assignee@esieeit.local", "assignee", Set.of(UserRole.USER));
        assignee.setPasswordHash("hashed-password");
        entityManager.persist(assignee);

        Project project = new Project("Projet JPA", "Verification mapping TP 4.1", owner);
        entityManager.persist(project);

        Task task = new Task("Configurer JPA", "Persist task with relations", project);
        task.assignTo(assignee);
        task.setPriority(TaskPriority.HIGH);
        project.addTask(task);
        entityManager.persist(task);

        Comment comment = new Comment("Mapping JPA OK", task, owner);
        entityManager.persist(comment);

        entityManager.flush();
        entityManager.clear();
    }
}
