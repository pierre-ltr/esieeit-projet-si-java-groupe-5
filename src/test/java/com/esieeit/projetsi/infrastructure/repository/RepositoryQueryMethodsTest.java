package com.esieeit.projetsi.infrastructure.repository;

import com.esieeit.projetsi.domain.enums.ProjectStatus;
import com.esieeit.projetsi.domain.enums.TaskPriority;
import com.esieeit.projetsi.domain.enums.TaskStatus;
import com.esieeit.projetsi.domain.enums.UserRole;
import com.esieeit.projetsi.domain.model.Project;
import com.esieeit.projetsi.domain.model.Task;
import com.esieeit.projetsi.domain.model.User;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.TestPropertySource;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@TestPropertySource(properties = {
        "spring.jpa.hibernate.ddl-auto=create-drop",
        "spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.H2Dialect"
})
class RepositoryQueryMethodsTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private TaskRepository taskRepository;

    private User owner;
    private User assignee;
    private Project activeProject;

    @BeforeEach
    void setUp() {
        owner = new User("owner-query@esieeit.local", "ownerquery", Set.of(UserRole.ADMIN));
        owner.setPasswordHash("owner-password");
        assignee = new User("assignee-query@esieeit.local", "assigneequery", Set.of(UserRole.USER));
        assignee.setPasswordHash("assignee-password");
        userRepository.saveAll(List.of(owner, assignee));

        activeProject = new Project("Repository Queries", "Verify JPA query methods", owner);
        activeProject.setStatus(ProjectStatus.ACTIVE);
        projectRepository.save(activeProject);

        Task todoTask = new Task("Alpha task", "Repository alpha", activeProject);
        todoTask.assignTo(assignee);

        Task progressTask = new Task("Beta task", "Repository beta", activeProject);
        progressTask.assignTo(assignee);
        progressTask.setPriority(TaskPriority.HIGH);
        progressTask.start();

        activeProject.addTask(todoTask);
        activeProject.addTask(progressTask);
        taskRepository.saveAll(List.of(todoTask, progressTask));
    }

    @Test
    void shouldExposeUsefulTaskProjectAndUserQueries() {
        assertThat(taskRepository.findByStatus(TaskStatus.TODO)).hasSize(1);
        assertThat(taskRepository.findByStatusOrderByCreatedAtDesc(TaskStatus.IN_PROGRESS)).hasSize(1);
        assertThat(taskRepository.findByProjectId(activeProject.getId())).hasSize(2);
        assertThat(taskRepository.findByAssigneeId(assignee.getId())).hasSize(2);
        assertThat(taskRepository.findByTitleContainingIgnoreCase("alpha")).extracting(Task::getTitle)
                .containsExactly("Alpha task");
        assertThat(taskRepository.existsByProjectIdAndTitleIgnoreCase(activeProject.getId(), "alpha task")).isTrue();
        assertThat(taskRepository.countByProjectId(activeProject.getId())).isEqualTo(2);

        assertThat(projectRepository.findFirstByNameIgnoreCase("repository queries")).isPresent();
        assertThat(projectRepository.findByStatus(ProjectStatus.ACTIVE)).hasSize(1);
        assertThat(projectRepository.findByOwnerId(owner.getId())).hasSize(1);

        assertThat(userRepository.findByEmail("owner-query@esieeit.local")).isPresent();
        assertThat(userRepository.findByEmailIgnoreCase("OWNER-QUERY@ESIEEIT.LOCAL")).isPresent();
        assertThat(userRepository.findByUsernameIgnoreCase("OWNERQUERY")).isPresent();
        assertThat(userRepository.findByEmailIgnoreCaseOrUsernameIgnoreCase("owner-query@esieeit.local", "ignored"))
                .isPresent();
        assertThat(userRepository.existsByEmailIgnoreCase("OWNER-QUERY@ESIEEIT.LOCAL")).isTrue();
        assertThat(userRepository.existsByUsernameIgnoreCase("OWNERQUERY")).isTrue();
    }
}
