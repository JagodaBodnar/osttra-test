package org.osttra.integrationtests;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.osttra.exceptions.EmailAlreadyExistsException;
import org.osttra.user.dto.UserRequestDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
public class UserControllerIT {
    @Autowired
    TestRestTemplate restTemplate;
    @Autowired
    private JdbcTemplate jdbcTemplate;
    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> postgres =
            new PostgreSQLContainer<>(DockerImageName.parse("postgres:15-alpine"));

    @BeforeEach
    public void setUp() {
        jdbcTemplate.execute("CREATE TABLE IF NOT EXISTS osttra_user (" +
                "id UUID PRIMARY KEY," +
                "email VARCHAR(255) UNIQUE NOT NULL," +
                "first_name VARCHAR(255)," +
                "last_name VARCHAR(255)" +
                ");");
    }

    @AfterEach
    public void cleanUp() {
        jdbcTemplate.execute("DELETE FROM osttra_user where email='henrik.nordin@gmail.com'");
    }

    @Test
    void connectionEstablished() {
        assertThat(postgres.isCreated()).isTrue();
        assertThat(postgres.isRunning()).isTrue();
    }

    @Test
    public void shouldSaveUser() {
        ResponseEntity<UUID> result = restTemplate.postForEntity("/api/users",
                new UserRequestDto("Henrik", "Nordin", "henrik.nordin@gmail.com"),
                UUID.class);
        assertEquals(201, result.getStatusCode().value());
    }

    @Test
    public void shouldThrowExceptionWhenEmailIsRepeated() {
        restTemplate.postForEntity("/api/users",
                new UserRequestDto("Henrik", "Nordin", "henrik.nordin@gmail.com"),
                UUID.class);
        ResponseEntity<EmailAlreadyExistsException> result2 = restTemplate.postForEntity("/api/users",
                new UserRequestDto("Henrik", "Nordin", "henrik.nordin@gmail.com"),
                EmailAlreadyExistsException.class);
        assertEquals(400, result2.getStatusCode().value());
    }
}
