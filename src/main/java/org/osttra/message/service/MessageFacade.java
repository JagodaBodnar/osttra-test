package org.osttra.message.service;

import lombok.RequiredArgsConstructor;
import org.osttra.message.dtos.MessageRequestDto;
import org.osttra.message.dtos.MessageResponseDto;
import org.osttra.user.dto.UserRequestDto;
import org.osttra.user.repository.model.User;
import org.osttra.user.service.UserService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class MessageFacade {
    private final MessageService messageService;
    private final UserService userService;

    public void postMessage(MessageRequestDto requestMessage) {
        User user = userService.getUserById(requestMessage.recipientId());
        messageService.postMessage(requestMessage, user);
    }

    public Page<MessageResponseDto> getNewMessages(Pageable pageable, UUID recipient){
       return messageService.getNewMessages(pageable, recipient);
    }

    public Page<MessageResponseDto> getAllMessages(Pageable pageable, UUID recipient){
        return messageService.getAllMessages(pageable, recipient);
    }

    public void deleteMessage(Long id) {
        messageService.deleteMessage(id);
    }

    public void deleteMessages(List<Long> ids) {
        messageService.deleteMessages(ids);
    }

    public UUID createUser(UserRequestDto userRequestDto) {
        return userService.saveUser(UserRequestDto.toUser(userRequestDto));
    }
}
