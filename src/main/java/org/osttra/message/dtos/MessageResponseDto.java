package org.osttra.message.dtos;

import org.osttra.message.repository.model.Message;

import java.time.LocalDateTime;

public record MessageResponseDto(Long id, String content, LocalDateTime date) {
    public static MessageResponseDto toMessageResponseDto(Message message) {
       return new MessageResponseDto(message.getId(), message.getContent(), message.getTimestamp());
    }
}
