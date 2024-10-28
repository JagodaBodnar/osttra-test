package org.osttra.unittests.message;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.osttra.exceptions.MessageNotFoundException;
import org.osttra.exceptions.Messages;
import org.osttra.message.dtos.MessageResponseDto;
import org.osttra.message.repository.MessageRepository;
import org.osttra.message.repository.model.Message;
import org.osttra.message.service.MessageService;
import org.osttra.user.repository.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class MessageServiceTest {
    @InjectMocks
    private MessageService messageService;
    @Mock
    private MessageRepository messageRepository;

    @Test
    @DisplayName("Should return only one message when get new messages.")
    public void shouldFetchOnlyMessagesWithReadFalse_whenGetNewMessages() {
        /*ARRANGE*/
        User user = new User(UUID.fromString("3551374a-0eba-4dd3-bb4b-f771dc061bdc"),
                "Henrik", "Eriksson", "henrik.eriksson@gmail.com");
        Message message = new Message(12L, "Kind reminder to register work time.",
                user,
                "System",
                false, LocalDateTime.now());
        UUID uuid = UUID.fromString("dc145f00-a937-4bc4-9c97-d6f1d8134c1e");
        when(messageRepository.findAllByRecipientIdAndReadFalseOrderByTimestampAsc(
                any(Pageable.class),
                any(UUID.class)))
                .thenReturn(new PageImpl<>(Collections.singletonList(message),
                        PageRequest.of(0, 1), 1));

        /*ACT*/
        Page<MessageResponseDto> result = messageService.getNewMessages(
                PageRequest.of(0, 1), uuid);

        /*ASSERT*/
        assertAll(
                "Assertions for getting new messages.",
                () -> assertEquals(12L, result.getContent().getFirst().id()),
                () -> assertEquals(1, result.getContent().size())
        );
    }

    @Test
    @DisplayName("When delete one message with non existing id throw MessageNotFoundException.")
    public void shouldReturnStatus400_whenDeleteMessageWithIdThatNotExists() {
        /*ARRANGE*/
        when(messageRepository.findById(4L))
                .thenThrow(new MessageNotFoundException(String.format(Messages.MESSAGE_WITH_ID_NOT_FOUND,4L)));
        /*ACT AND ASSERT*/
        assertThrows(MessageNotFoundException.class, () -> messageService.deleteMessage(4L));
    }
}
