package org.osttra.message.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.osttra.message.dtos.IdsRequestDto;
import org.osttra.message.dtos.MessageRequestDto;
import org.osttra.message.dtos.MessageResponseDto;
import org.osttra.message.service.MessageFacade;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.config.EnableSpringDataWebSupport;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;


@RestController
@RequestMapping("/api/messages")
@RequiredArgsConstructor
@EnableSpringDataWebSupport(pageSerializationMode = EnableSpringDataWebSupport.PageSerializationMode.VIA_DTO)
public class MessageController {
    private final MessageFacade messageFacade;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public void postMessage(@Valid @RequestBody MessageRequestDto requestMessage) {
        messageFacade.postMessage(requestMessage);
    }

    @GetMapping("/new/{recipient}")
    @ResponseStatus(HttpStatus.OK)
    public Page<MessageResponseDto> getNewMessages(@PathVariable UUID recipient,
                                                         Pageable pageable) {
        return messageFacade.getNewMessages(pageable, recipient);
    }

    @GetMapping("/all/{recipient}")
    @ResponseStatus(HttpStatus.OK)
    public Page<MessageResponseDto> getAllMessages(@PathVariable UUID recipient,
                                                   Pageable pageable) {
        return messageFacade.getAllMessages(pageable, recipient);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteMessage(@PathVariable Long id) {
        messageFacade.deleteMessage(id);
    }

    @DeleteMapping
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteMessages(@RequestBody IdsRequestDto idsRequestDto) {
        messageFacade.deleteMessages(idsRequestDto.ids());
    }
}
