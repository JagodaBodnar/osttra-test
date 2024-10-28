package org.osttra.unittests.message;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.osttra.exceptions.Messages;
import org.osttra.message.controller.MessageController;
import org.osttra.message.dtos.IdsRequestDto;
import org.osttra.message.dtos.MessageRequestDto;
import org.osttra.message.dtos.MessageResponseDto;
import org.osttra.message.service.MessageFacade;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(MessageController.class)
public class MessageControllerTest {
    @Autowired
    MockMvc mockMvc;
    @MockBean
    MessageFacade messageFacade;
    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("When post method createMessage should save message and return 201.")
    public void shouldSaveUserAndReturnUUID_whenPostCreateUser() throws Exception {
        /*ARRANGE*/
        UUID recipientId = UUID.fromString("042484f0-be2f-49b1-a741-6962ef991718");
        MessageRequestDto messageRequestDto = new MessageRequestDto(recipientId,
                "Remember about meeting tomorrow 15:30");
        String jsonRequest = objectMapper.writeValueAsString(messageRequestDto);

        /*ACT AND ASSERT*/
        mockMvc.perform(post("/api/messages")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                .andExpect(status().isCreated());
    }

    @Test
    @DisplayName("When post message and body has empty content should return 400 status and error message.")
    public void shouldReturn400_whenPostCreateUserWithEmptyContent() throws Exception {
        /*ARRANGE*/
        UUID recipientId = UUID.fromString("042484f0-be2f-49b1-a741-6962ef991718");
        MessageRequestDto messageRequestDto = new MessageRequestDto(recipientId,
                "");
        String jsonRequest = objectMapper.writeValueAsString(messageRequestDto);

        /*ACT AND ASSERT*/
        mockMvc.perform(post("/api/messages")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value(Messages.MESSAGE_CONTENT_MUST_NOT_BE_BLANK));
    }

    @Test
    @DisplayName("When post message and body has null recipient should return 400 status and error message.")
    public void shouldReturn400_whenPostCreateUserWhenRecipientNull() throws Exception {
        /*ARRANGE*/
        MessageRequestDto messageRequestDto = new MessageRequestDto(null,
                "Remember about meeting tomorrow 15:30.");
        String jsonRequest = objectMapper.writeValueAsString(messageRequestDto);

        /*ACT AND ASSERT*/
        mockMvc.perform(post("/api/messages")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value(Messages.RECIPIENT_ID_MUST_NOT_BE_NULL));
    }

    @Test
    @DisplayName("When post message and body has wrong recipientId should return 400 status and error message.")
    public void shouldReturn400_whenPostCreateUserWhenRecipientIdWrong() throws Exception {
        /*ARRANGE*/
        String jsonRequest = "{" +
                "    \"recipientId\": \"042484f0-be2f-49b1-a741-6962ef99\"," +
                "    \"content\": \"Remember about meeting tomorrow 15:30.\"" +
                "}";

        /*ACT AND ASSERT*/
        mockMvc.perform(post("/api/messages")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("When get new messages return 200 status and 1 new message.")
    public void shouldReturnOneNewMessageAndStatus200_whenGetNewMessages() throws Exception {
        /*ARRANGE*/
        MessageResponseDto messageResponseDto = new MessageResponseDto(2L,
                "Kind reminder to register time.",
                LocalDateTime.now());

        when(messageFacade.getNewMessages(any(Pageable.class), any(UUID.class)))
                .thenReturn(new PageImpl<>(Collections.singletonList(messageResponseDto),
                        PageRequest.of(0, 1), 1));

        /*ACT AND ASSERT*/
        mockMvc.perform(get("/api/messages/new/042484f0-be2f-49b1-a741-6962ef991718"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id").value(2L))
                .andExpect(jsonPath("$.content[0].content").value("Kind reminder to register time."));
    }

    @Test
    @DisplayName("When get all messages return 200 status and 2 messages.")
    public void shouldReturnTwoMessagedAndStatus200_whenGetAllMessages() throws Exception {
        /*ARRANGE*/
        MessageResponseDto messageResponseDto = new MessageResponseDto(2L,
                "Kind reminder to register time.",
                LocalDateTime.now());
        MessageResponseDto messageResponseDto2 = new MessageResponseDto(3L,
                "Remember about meeting tomorrow 15:30.",
                LocalDateTime.now());
        when(messageFacade.getAllMessages(any(Pageable.class), any(UUID.class)))
                .thenReturn(new PageImpl<>(List.of(messageResponseDto, messageResponseDto2),
                        PageRequest.of(0, 2), 2));

        /*ACT AND ASSERT*/
        mockMvc.perform(get("/api/messages/all/042484f0-be2f-49b1-a741-6962ef991718"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[*]").value(hasSize(2)))
                .andExpect(jsonPath("$.content[1].id").value(3L))
                .andExpect(jsonPath("$.content[1].content").value("Remember about meeting tomorrow 15:30."));
    }
    @Test
    @DisplayName("When delete one message return 204 status.")
    public void shouldReturnStatus204_whenDeleteSingleMessage() throws Exception {
        /*ARRANGE*/
        doNothing().when(messageFacade).deleteMessage(any(Long.class));

        /*ACT AND ASSERT*/
        mockMvc.perform(delete("/api/messages/2"))
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("When delete more messages return 204 status.")
    public void shouldReturnStatus204_whenDeleteMoreMessages() throws Exception {
        /*ARRANGE*/
        IdsRequestDto messageRequestDto = new IdsRequestDto(List.of(2L,3L));
        String jsonRequest = objectMapper.writeValueAsString(messageRequestDto);

        doNothing().when(messageFacade).deleteMessages(anyList());

        /*ACT AND ASSERT*/
        mockMvc.perform(delete("/api/messages")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonRequest))
                .andExpect(status().isNoContent());
    }

}
