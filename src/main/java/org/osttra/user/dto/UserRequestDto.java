package org.osttra.user.dto;

import jakarta.validation.constraints.NotBlank;
import org.osttra.exceptions.Messages;
import org.osttra.user.repository.model.User;

public record UserRequestDto(@NotBlank(message = Messages.FIRST_NAME_MUST_NOT_BE_BLANK) String firstName,
                             @NotBlank(message = Messages.LAST_NAME_MUST_NOT_BE_BLANK) String lastName,
                             @NotBlank(message = Messages.EMAIL_MUST_NOT_BE_BLANK) String email) {
    public static User toUser(UserRequestDto userRequestDto) {
        return new User(userRequestDto.firstName(),
                userRequestDto.lastName(),
                userRequestDto.email());
    }
}
