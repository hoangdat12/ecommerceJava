package com.example.catchingdata.handler;

import com.example.catchingdata.dto.MessageException;
import com.example.catchingdata.response.errorResponse.BadRequest;
import com.example.catchingdata.response.errorResponse.Forbbiden;
import com.example.catchingdata.response.errorResponse.InternalServerError;
import com.example.catchingdata.response.errorResponse.NotFound;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.web.ErrorResponse;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Objects;


@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {
    @ExceptionHandler(BindException.class)
    public ResponseEntity<?> handleBindException(BindException e) {
        MessageException<Object> message = MessageException.builder()
                .code(HttpStatus.BAD_REQUEST.value())
                .status("Error")
                .metadata(Objects.requireNonNull(e.getFieldError()).getDefaultMessage())
                .build();
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST.value())
                .body(message);
    }
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<?> handleExceptionIllegalArgumentException(IllegalArgumentException exception) {
        MessageException<Object> message = MessageException.builder()
                .code(HttpStatus.BAD_REQUEST.value())
                .status("Error")
                .metadata(exception.getMessage())
                .build();
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST.value())
                .body(message);
    }
    @ExceptionHandler(NotFound.class)
    public ResponseEntity<?> handleExceptionNotFound(NotFound exception) {
        MessageException<Object> message = MessageException.builder()
                .code(HttpStatus.NOT_FOUND.value())
                .status("Error")
                .metadata(exception.getMessage())
                .build();
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND.value())
                .body(message);
    }
    @ExceptionHandler(InternalServerError.class)
    public ResponseEntity<?> handleExceptionInternalServerError(InternalServerError exception) {
        MessageException<Object> message = MessageException.builder()
                .code(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .status("Error")
                .metadata(exception.getMessage())
                .build();
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .body(message);
    }
    @ExceptionHandler(Forbbiden.class)
    public ResponseEntity<?> handleExceptionForbbiden(Forbbiden exception) {
        MessageException<Object> message = MessageException.builder()
                .code(HttpStatus.FORBIDDEN.value())
                .status("Error")
                .metadata(exception.getMessage())
                .build();
        return ResponseEntity
                .status(HttpStatus.FORBIDDEN.value())
                .body(message);
    }
    @ExceptionHandler(BadRequest.class)
    public ResponseEntity<?> handleExceptionBadRequest(BadRequest exception) {
        MessageException<Object> message = MessageException.builder()
                .code(HttpStatus.BAD_REQUEST.value())
                .status("Error")
                .metadata(exception.getMessage())
                .build();
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST.value())
                .body(message);
    }
}
