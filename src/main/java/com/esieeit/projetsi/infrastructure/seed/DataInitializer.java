package com.esieeit.projetsi.infrastructure.seed;

import com.esieeit.projetsi.domain.enums.ProjectStatus;
import com.esieeit.projetsi.domain.enums.TaskPriority;
import com.esieeit.projetsi.domain.enums.UserRole;
import com.esieeit.projetsi.domain.model.Project;
import com.esieeit.projetsi.domain.model.Task;
import com.esieeit.projetsi.domain.model.User;
import com.esieeit.projetsi.infrastructure.repository.ProjectRepository;
import com.esieeit.projetsi.infrastructure.repository.TaskRepository;
import com.esieeit.projetsi.infrastructure.repository.UserRepository;
import java.time.LocalDate;
import java.util.Set;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@Profile("dev")
public class DataInitializer {

    private final UserRepository userRepository;
    private final ProjectRepository projectRepository;
    private final TaskRepository taskRepository;
    private final PasswordEncoder passwordEncoder;

    public DataInitializer(
            UserRepository userRepository,
            ProjectRepository projectRepository,
            TaskRepository taskRepository,
            PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.projectRepository = projectRepository;
        this.taskRepository = taskRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Bean
    CommandLineRunner initData() {
        return args -> {
            if (userRepository.count() > 0 || projectRepository.count() > 0 || taskRepository.count() > 0) {
                refreshSeedPasswordsIfNeeded();
                return;
            }

            User admin = new User("admin@esieeit.local", "admin", Set.of(UserRole.ADMIN));
            admin.setPasswordHash(passwordEncoder.encode("admin-password"));
            User devUser = new User("dev@esieeit.local", "devuser", Set.of(UserRole.USER));
            devUser.setPasswordHash(passwordEncoder.encode("dev-password"));
            userRepository.save(admin);
            userRepository.save(devUser);

            Project defaultProject = new Project("Default API Project", "Projet par defaut pour l'API Tasks", admin);
            defaultProject.setStatus(ProjectStatus.ACTIVE);
            Project sprintProject = new Project("Sprint 4 Delivery", "Preparation du TP 4.2", devUser);
            sprintProject.setStatus(ProjectStatus.ACTIVE);
            projectRepository.save(defaultProject);
            projectRepository.save(sprintProject);

            Task task1 = new Task("Brancher TaskService sur JPA", "Remplacer le repository en memoire", defaultProject);
            task1.assignTo(devUser);
            task1.setPriority(TaskPriority.HIGH);
            task1.start();

            Task task2 = new Task("Documenter le lancement Docker", "Ajouter les commandes de test au README", defaultProject);
            task2.setPriority(TaskPriority.MEDIUM);
            task2.setDueDate(LocalDate.now().plusDays(3));

            Task task3 = new Task("Verifier les query methods", "Tester les recherches par statut et projet", sprintProject);
            task3.assignTo(admin);
            task3.setPriority(TaskPriority.URGENT);
            task3.setDueDate(LocalDate.now().plusDays(1));

            defaultProject.addTask(task1);
            defaultProject.addTask(task2);
            sprintProject.addTask(task3);

            taskRepository.save(task1);
            taskRepository.save(task2);
            taskRepository.save(task3);
        };
    }

    private void refreshSeedPasswordsIfNeeded() {
        refreshSeedPassword("admin@esieeit.local", "admin-password");
        refreshSeedPassword("dev@esieeit.local", "dev-password");
    }

    private void refreshSeedPassword(String email, String rawPassword) {
        userRepository.findByEmailIgnoreCase(email)
                .filter(user -> user.getPasswordHash() != null && !user.getPasswordHash().startsWith("$2"))
                .ifPresent(user -> {
                    user.setPasswordHash(passwordEncoder.encode(rawPassword));
                    userRepository.save(user);
                });
    }
}
