package com.epam.trainer_workload.controller.handler;

import com.epam.trainer_workload.dto.response.ErrorResponseDto;
import com.epam.trainer_workload.exception.AuthenticationException;
import com.epam.trainer_workload.exception.ParseTokenException;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class RestExceptionHandler {

    @ExceptionHandler(InvalidFormatException.class)
    public ResponseEntity<ErrorResponseDto> handleInvalidFormatException(InvalidFormatException ex) {
        return ResponseEntity.badRequest().body(ErrorResponseDto.of(ex.getOriginalMessage()));
    }

    @ExceptionHandler(ParseTokenException.class)
    public ResponseEntity<ErrorResponseDto> handleParseTokenException(ParseTokenException e) {
        return ResponseEntity.badRequest().body(ErrorResponseDto.of(e.getMessage()));
    }

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ErrorResponseDto> handleAuthenticationException(AuthenticationException e) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ErrorResponseDto.of(e.getMessage()));
    }

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<ErrorResponseDto> handleEntityNotFoundException(EntityNotFoundException e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ErrorResponseDto.of(e.getMessage()));
    }
}
