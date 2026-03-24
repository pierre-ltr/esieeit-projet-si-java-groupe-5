package com.esieeit.projetsi.infrastructure.repository;

import com.esieeit.projetsi.domain.enums.ProjectStatus;
import com.esieeit.projetsi.domain.model.Project;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProjectRepository extends JpaRepository<Project, Long> {

    Optional<Project> findFirstByNameIgnoreCase(String name);

    List<Project> findByStatus(ProjectStatus status);

    List<Project> findByOwnerId(Long ownerId);
}
