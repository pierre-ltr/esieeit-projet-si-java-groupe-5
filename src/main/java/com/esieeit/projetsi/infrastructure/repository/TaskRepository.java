package com.esieeit.projetsi.infrastructure.repository;

import com.esieeit.projetsi.domain.enums.TaskStatus;
import com.esieeit.projetsi.domain.model.Task;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TaskRepository extends JpaRepository<Task, Long> {

    List<Task> findByStatus(TaskStatus status);

    List<Task> findByStatusOrderByCreatedAtDesc(TaskStatus status);

    List<Task> findByProjectId(Long projectId);

    List<Task> findByAssigneeId(Long assigneeId);

    List<Task> findByTitleContainingIgnoreCase(String keyword);

    boolean existsByProjectIdAndTitleIgnoreCase(Long projectId, String title);

    long countByProjectId(Long projectId);
}
