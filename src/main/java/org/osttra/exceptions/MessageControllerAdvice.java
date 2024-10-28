package org.osttra.exceptions;

import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.stream.Collectors;

@RestControllerAdvice
public class MessageControllerAdvice {
    @ExceptionHandler(value = MessageNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ApiError resourceNotFoundExceptionHandler(MessageNotFoundException exception) {
        return ApiError.builder()
                .message(exception.getMessage())
                .statusCode(HttpStatus.NOT_FOUND.value())
                .build();
    }

    @ExceptionHandler(value = MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiError argumentNotValidExceptionHandler(MethodArgumentNotValidException exception) {
        return ApiError.builder()
                .message(createErrorsMessage(exception))
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .build();
    }

    @ExceptionHandler(value = HttpMessageNotReadableException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiError argumentNotValidExceptionHandler(HttpMessageNotReadableException exception) {
        return ApiError.builder()
                .message(exception.getMessage())
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .build();
    }

    @ExceptionHandler(value = {EmailAlreadyExistsException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiError argumentNotValidExceptionHandler(EmailAlreadyExistsException exception) {
        return ApiError.builder()
                .message(exception.getMessage())
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .build();
    }

    private String createErrorsMessage(MethodArgumentNotValidException exception) {
        return exception.getFieldErrors()
                .stream()
                .map(DefaultMessageSourceResolvable::getDefaultMessage)
                .collect(Collectors.joining(" "));
    }
}
