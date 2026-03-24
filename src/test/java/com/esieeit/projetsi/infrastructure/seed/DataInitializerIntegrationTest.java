package com.esieeit.projetsi.infrastructure.seed;

import com.esieeit.projetsi.infrastructure.repository.ProjectRepository;
import com.esieeit.projetsi.infrastructure.repository.TaskRepository;
import com.esieeit.projetsi.infrastructure.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("dev")
@TestPropertySource(properties = {
        "spring.datasource.url=jdbc:h2:mem:seeddb;MODE=MySQL;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE",
        "spring.datasource.driver-class-name=org.h2.Driver",
        "spring.datasource.username=sa",
        "spring.datasource.password=",
        "spring.jpa.hibernate.ddl-auto=create-drop",
        "spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.H2Dialect"
})
class DataInitializerIntegrationTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private TaskRepository taskRepository;

    @Test
    void shouldSeedDevelopmentDataWhenDatabaseIsEmpty() {
        assertThat(userRepository.count()).isGreaterThanOrEqualTo(2);
        assertThat(projectRepository.count()).isGreaterThanOrEqualTo(2);
        assertThat(taskRepository.count()).isGreaterThanOrEqualTo(3);
        assertThat(projectRepository.findFirstByNameIgnoreCase("Default API Project")).isPresent();
    }
}
