package org.osttra.message.service;

import lombok.RequiredArgsConstructor;
import org.osttra.message.dtos.MessageRequestDto;
import org.osttra.message.dtos.MessageResponseDto;
import org.osttra.exceptions.MessageNotFoundException;
import org.osttra.exceptions.Messages;
import org.osttra.message.repository.model.Message;
import org.osttra.message.repository.MessageRepository;
import org.osttra.user.repository.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class MessageService {
    private final MessageRepository messageRepository;

    public void postMessage(MessageRequestDto requestMessage, User user) {
        messageRepository.save(new Message(requestMessage.content(), user));
    }

    public Page<MessageResponseDto> getNewMessages(Pageable pageable, UUID recipient) {
        Page<Message> message = messageRepository.findAllByRecipientIdAndReadFalseOrderByTimestampAsc(pageable, recipient);
        return changeStatusToReadAndCreateResponse(message, pageable);
    }

    public Page<MessageResponseDto> getAllMessages(Pageable pageable, UUID recipient) {
        Page<Message> message = messageRepository.findAllByRecipientIdOrderByTimestampAsc(pageable, recipient);
        return changeStatusToReadAndCreateResponse(message, pageable);
    }

    private Page<MessageResponseDto> changeStatusToReadAndCreateResponse(Page<Message> messages, Pageable pageable) {
        messages.forEach(el -> el.setRead(true));
        messageRepository.saveAll(messages);
        return new PageImpl<>(messages.map(MessageResponseDto::toMessageResponseDto).toList(), pageable, messages.getTotalElements());
    }

    public void deleteMessage(Long id) {
        messageRepository.findById(id).ifPresentOrElse(
                messageRepository::delete,
                ()-> {
                    throw new MessageNotFoundException(
                            String.format(Messages.MESSAGE_WITH_ID_NOT_FOUND, id)
                    );
                }
        );
    }
    public void deleteMessages(List<Long> ids) {
        List<Message> messages = messageRepository.findAllById(ids);
        messageRepository.deleteAll(messages);
    }

}
