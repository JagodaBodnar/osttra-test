package org.osttra.integrationtests;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.osttra.exceptions.MessageNotFoundException;
import org.osttra.message.service.MessageFacade;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlGroup;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertThrows;

@SpringBootTest
@Testcontainers
public class MessageFacadeIT {
    @Autowired
    private MessageFacade messageFacade;
    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> postgres =
            new PostgreSQLContainer<>(DockerImageName.parse("postgres:15-alpine"));

    @Test
    void connectionEstablished() {
        assertThat(postgres.isCreated()).isTrue();
        assertThat(postgres.isRunning()).isTrue();
    }

    @Test
    @SqlGroup({
            @Sql(scripts = "/test-data-clear.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD),
            @Sql(scripts = "/test-data.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD),
    })

    @DisplayName("Should throw MessageNotFoundException when delete message with id that doesn't exist.")
    public void shouldReturn404Status_whenDeletedIdThatDoesNotExist() {
        /*ACT AND ASSERT*/
        assertThrows(MessageNotFoundException.class, () -> messageFacade.deleteMessage(5L));
    }
}
