package org.osttra.user.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.osttra.message.service.MessageFacade;
import org.osttra.user.dto.UserRequestDto;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {
    private final MessageFacade messageFacade;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UUID createUser(@Valid @RequestBody UserRequestDto user) {
        return messageFacade.createUser(user);
    }
}
