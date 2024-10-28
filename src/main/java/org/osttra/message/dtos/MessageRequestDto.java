package org.osttra.message.dtos;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.osttra.exceptions.Messages;

import java.util.UUID;

public record MessageRequestDto(@NotNull(message = Messages.RECIPIENT_ID_MUST_NOT_BE_NULL) UUID recipientId,
                                @NotBlank(message = Messages.MESSAGE_CONTENT_MUST_NOT_BE_BLANK) String content) {
}
