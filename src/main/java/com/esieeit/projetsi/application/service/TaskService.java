package com.esieeit.projetsi.application.service;

import com.esieeit.projetsi.api.dto.TaskCreateRequest;
import com.esieeit.projetsi.api.dto.TaskUpdateRequest;
import com.esieeit.projetsi.domain.enums.TaskStatus;
import com.esieeit.projetsi.domain.enums.UserRole;
import com.esieeit.projetsi.domain.exception.BusinessRuleException;
import com.esieeit.projetsi.domain.exception.InvalidDataException;
import com.esieeit.projetsi.domain.exception.ResourceNotFoundException;
import com.esieeit.projetsi.domain.model.Project;
import com.esieeit.projetsi.domain.model.Task;
import com.esieeit.projetsi.domain.model.User;
import com.esieeit.projetsi.domain.validation.Validators;
import com.esieeit.projetsi.infrastructure.repository.ProjectRepository;
import com.esieeit.projetsi.infrastructure.repository.TaskRepository;
import com.esieeit.projetsi.infrastructure.repository.UserRepository;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class TaskService {

    private static final String DEFAULT_PROJECT_NAME = "Default API Project";
    private static final String SYSTEM_EMAIL = "system@esieeit.local";
    private final TaskRepository taskRepository;
    private final ProjectRepository projectRepository;
    private final UserRepository userRepository;

    public TaskService(
            TaskRepository taskRepository,
            ProjectRepository projectRepository,
            UserRepository userRepository) {
        this.taskRepository = taskRepository;
        this.projectRepository = projectRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public Task create(TaskCreateRequest request) {
        Validators.requireNonNull(request, "request");
        Project defaultProject = resolveDefaultProject();
        if (taskRepository.existsByProjectIdAndTitleIgnoreCase(defaultProject.getId(), request.getTitle())) {
            throw new BusinessRuleException("A task with the same title already exists in the default project");
        }
        Task task = new Task(request.getTitle(), request.getDescription(), defaultProject);
        defaultProject.addTask(task);
        return taskRepository.save(task);
    }

    @Transactional(readOnly = true)
    public List<Task> getAll() {
        return taskRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Task getById(Long id) {
        validateId(id);
        return taskRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Task not found: id=" + id));
    }

    @Transactional
    public Task update(Long id, TaskUpdateRequest request) {
        validateId(id);
        Validators.requireNonNull(request, "request");

        Task task = getById(id);

        if (request.getTitle() != null) {
            task.setTitle(request.getTitle());
        }

        if (request.getDescription() != null) {
            task.setDescription(request.getDescription());
        }

        if (request.getStatus() != null) {
            applyStatusTransition(task, request.getStatus());
        }

        return taskRepository.save(task);
    }

    @Transactional
    public void delete(Long id) {
        validateId(id);
        if (!taskRepository.existsById(id)) {
            throw new ResourceNotFoundException("Task not found: id=" + id);
        }
        taskRepository.deleteById(id);
    }

    private Project resolveDefaultProject() {
        return projectRepository.findFirstByNameIgnoreCase(DEFAULT_PROJECT_NAME)
                .orElseGet(this::createDefaultProject);
    }

    private Project createDefaultProject() {
        User systemOwner = userRepository.findByEmail(SYSTEM_EMAIL)
                .orElseGet(this::createSystemUser);
        Project project = new Project(DEFAULT_PROJECT_NAME, "Persistent default project for Task API", systemOwner);
        return projectRepository.save(project);
    }

    private User createSystemUser() {
        User systemOwner = new User(SYSTEM_EMAIL, "system", Set.of(UserRole.ADMIN));
        systemOwner.setPasswordHash("system-password");
        return userRepository.save(systemOwner);
    }

    private void applyStatusTransition(Task task, String statusRaw) {
        TaskStatus target;
        try {
            target = TaskStatus.valueOf(statusRaw.trim().toUpperCase(Locale.ROOT));
        } catch (IllegalArgumentException ex) {
            throw new InvalidDataException("Unknown status: " + statusRaw);
        }

        TaskStatus current = task.getStatus();
        if (current == target) {
            return;
        }

        if (current == TaskStatus.ARCHIVED) {
            throw new BusinessRuleException("No transition allowed from ARCHIVED");
        }

        switch (target) {
            case TODO -> {
                if (current == TaskStatus.IN_PROGRESS) {
                    task.moveBackToTodo();
                    return;
                }
                throw new BusinessRuleException("Transition to TODO is allowed only from IN_PROGRESS");
            }
            case IN_PROGRESS -> task.start();
            case DONE -> task.complete();
            case ARCHIVED -> task.archive();
            default -> throw new BusinessRuleException("Unsupported status transition to " + target);
        }
    }

    private void validateId(Long id) {
        if (id == null || id <= 0) {
            throw new InvalidDataException("id must be greater than 0");
        }
    }
}
