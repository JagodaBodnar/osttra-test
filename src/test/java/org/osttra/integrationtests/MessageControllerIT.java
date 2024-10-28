package org.osttra.integrationtests;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.*;
import org.osttra.message.dtos.IdsRequestDto;
import org.osttra.message.dtos.MessageRequestDto;
import org.osttra.message.repository.MessageRepository;
import org.osttra.message.repository.model.Message;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlGroup;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@Testcontainers
@AutoConfigureMockMvc
public class MessageControllerIT {
    @Autowired
    MockMvc mockMvc;
    @Autowired
    private MessageRepository messageRepository;

    @Autowired
    private ObjectMapper objectMapper;

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
            @Sql(scripts = "/test-data.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    })
    @DisplayName("Should post message for Henrik.")
    public void shouldPostMessageForHenrik() throws Exception {
        /*ARRANGE*/
        UUID recipientId = UUID.fromString("49368723-db5a-4232-a44e-71b3c54b8b83");
        MessageRequestDto messageRequestDto = new MessageRequestDto(recipientId,
                "Remember about meeting tomorrow 15:30");
        String jsonRequest = objectMapper.writeValueAsString(messageRequestDto);

        mockMvc.perform(post("/api/messages")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                .andExpect(status().isCreated());
        mockMvc.perform(get("/api/messages/new/49368723-db5a-4232-a44e-71b3c54b8b83"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(2)))
                .andExpect(jsonPath("$.content[1].content").value("Remember about meeting tomorrow 15:30"));
    }

    @Test
    @SqlGroup({
            @Sql(scripts = "/test-data-clear.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD),
            @Sql(scripts = "/test-data.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    })
    @DisplayName("Should return one new message for Henrik when fetching new messages.")
    public void shouldGetOneMessageForHenrik_whenFetchingNewMessages() throws Exception {
        /*ARRANGE*/
        mockMvc.perform(get("/api/messages/new/49368723-db5a-4232-a44e-71b3c54b8b83"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].content")
                        .value("Remember about meeting tomorrow at 13:30."));
    }

    @Test
    @SqlGroup({
            @Sql(scripts = "/test-data-clear.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD),
            @Sql(scripts = "/test-data.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    })
    @DisplayName("Should not return any messages when fetching second time new messages.")
    public void shouldGetNoMessageForHenrik_whenFetchingNewMessagesSecondTime() throws Exception {
        /*ACT AND ASSERT*/
        mockMvc.perform(get("/api/messages/new/49368723-db5a-4232-a44e-71b3c54b8b83"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].content")
                        .value("Remember about meeting tomorrow at 13:30."));

        mockMvc.perform(get("/api/messages/new/49368723-db5a-4232-a44e-71b3c54b8b83"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[*]").value(hasSize(0)));
    }

    @Test
    @SqlGroup({
            @Sql(scripts = "/test-data-clear.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD),
            @Sql(scripts = "/test-data.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD),
            @Sql(scripts = "/test-data-all-msg.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD),
    })
    @DisplayName("Should return 3 messages when fetching all messages.")
    public void shouldGet3MessagesForHenrik_whenFetchingAllMessages() throws Exception {
        /*ACT AND ASSERT*/
        mockMvc.perform(get("/api/messages/all/49368723-db5a-4232-a44e-71b3c54b8b83"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[*]").value(hasSize(3)))
                .andExpect(jsonPath("$.content[2].content")
                        .value("Remember about meeting tomorrow at 15:30."));
    }

    @Test
    @SqlGroup({
            @Sql(scripts = "/test-data-clear.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD),
            @Sql(scripts = "/test-data.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD),
            @Sql(scripts = "/test-data-all-msg.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD),
    })
    @DisplayName("Should return status 204 and 2 messages left after deleting one.")
    public void shouldRemain2MessagesForHenrik_whenDeleteOneMessage() throws Exception {
        /*ARRANGE*/
        List<Message> all = messageRepository.findAll();
        Long id = all.getFirst().getId();
        /*ACT AND ASSERT*/
        mockMvc.perform(delete("/api/messages/{id}", id))
                .andExpect(status().isNoContent());
        assertEquals(messageRepository.findAll().size(), 2);
    }

    @Test
    @SqlGroup({
            @Sql(scripts = "/test-data-clear.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD),
            @Sql(scripts = "/test-data.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD),
            @Sql(scripts = "/test-data-all-msg.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD),
    })
    @DisplayName("Should return status 204 and one message should left after deleting two messages.")
    public void shouldRemain1MessageForHenrik_whenDeletedTwoMessages() throws Exception {
        /*ARRANGE*/
        List<Message> all = messageRepository.findAll();
        Long id = all.getFirst().getId();
        Long id2 = all.getLast().getId();
        IdsRequestDto ids = new IdsRequestDto(List.of(id, id2));
        String jsonRequest = objectMapper.writeValueAsString(ids);
        /*ACT AND ASSERT*/
        mockMvc.perform(delete("/api/messages")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                .andExpect(status().isNoContent());
        assertEquals(messageRepository.findAll().size(), 1);
    }

    @Test
    @SqlGroup({
            @Sql(scripts = "/test-data-clear.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD),
            @Sql(scripts = "/test-data.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD),
            @Sql(scripts = "/test-data-all-msg.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD),
    })
    @DisplayName("Should return status 204 and 2 messages should left after deleting two messages with same id.")
    public void shouldRemain2MessagesForHenrik_whenDeletedTwoMessagesButWithSameId() throws Exception {
        /*ARRANGE*/
        List<Message> all = messageRepository.findAll();
        Long id = all.getFirst().getId();
        Long id2 = all.getFirst().getId();
        IdsRequestDto ids = new IdsRequestDto(List.of(id, id2));
        String jsonRequest = objectMapper.writeValueAsString(ids);
        /*ACT AND ASSERT*/
        mockMvc.perform(delete("/api/messages")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                .andExpect(status().isNoContent());
        assertEquals(messageRepository.findAll().size(), 2);
    }

    @Test
    @SqlGroup({
            @Sql(scripts = "/test-data-clear.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD),
            @Sql(scripts = "/test-data.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD),
            @Sql(scripts = "/test-data-all-msg.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD),
    })

    @DisplayName("Should return status 404 when delete message with id that doesn't exist.")
    public void shouldReturn404Status_whenDeletedIdThatDoesNotExist() throws Exception {
        /*ACT AND ASSERT*/
        mockMvc.perform(delete("/api/messages/11"))
                .andExpect(status().isNotFound());
    }

    @Test
    @SqlGroup({
            @Sql(scripts = "/test-data-clear.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD),
            @Sql(scripts = "/test-data.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD),
            @Sql(scripts = "/test-data-all-msg.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD),
    })

    @DisplayName("Should change status read to true when new message fetched.")
    public void shouldChangeReadStatusToTrue_whenDeletedIdThatDoesNotExist() throws Exception {
        Pageable pageable = PageRequest.of(0, 20);
        Page<Message> allByRecipientIdAndReadFalse = messageRepository.findAllByRecipientIdAndReadFalse(pageable,
                UUID.fromString("49368723-db5a-4232-a44e-71b3c54b8b83"));
        Long id = allByRecipientIdAndReadFalse.stream().toList().getFirst().getId();
        boolean isRead = messageRepository.findById(id).get().isRead();
        /*ACT AND ASSERT*/
        assertFalse(isRead);
        mockMvc.perform(get("/api/messages/new/49368723-db5a-4232-a44e-71b3c54b8b83"))
                .andExpect(status().isOk());
        isRead = messageRepository.findById(id).get().isRead();
        assertTrue(isRead);
    }

    @Test
    @SqlGroup({
            @Sql(scripts = "/test-data-clear.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD),
            @Sql(scripts = "/test-data.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD),
            @Sql(scripts = "/test-data-all-msg.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD),
    })
    @DisplayName("Should show totalPages 3 when size=1.")
    public void shouldShowTotalPages3_whenSizeOfPage1() throws Exception {
        /*ACT AND ASSERT*/
        mockMvc.perform(get("/api/messages/new/49368723-db5a-4232-a44e-71b3c54b8b83")
                        .param("size", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.page.totalElements").value(2))
                .andExpect(jsonPath("$.page.totalPages").value(2));

        mockMvc.perform(get("/api/messages/all/49368723-db5a-4232-a44e-71b3c54b8b83")
                        .param("size", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.page.totalElements").value(3))
                .andExpect(jsonPath("$.page.totalPages").value(3));
    }
}
