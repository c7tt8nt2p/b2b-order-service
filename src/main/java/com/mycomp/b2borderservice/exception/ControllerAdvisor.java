package com.mycomp.b2borderservice.exception;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.Path;
import java.time.ZonedDateTime;
import java.util.*;

@ControllerAdvice
public class ControllerAdvisor extends ResponseEntityExceptionHandler {

    @ExceptionHandler(value
            = {ValidationException.class, BusinessServiceException.class})
    public ResponseEntity<Object> handleApplicationExceptions(RuntimeException ex, WebRequest request) {
        HttpStatus status = HttpStatus.UNPROCESSABLE_ENTITY;

        Map<String, Object> responseBody = new LinkedHashMap<>();
        responseBody.put("timestamp", ZonedDateTime.now());
        responseBody.put("status", status.value());
        responseBody.put("message", ex.getMessage());

        return new ResponseEntity<>(responseBody, status);
    }

    @ExceptionHandler(value = {ConstraintViolationException.class})
    public ResponseEntity<Object> handleConstraintViolationException(ConstraintViolationException ex, WebRequest request) {
        HttpStatus status = HttpStatus.BAD_REQUEST;

        Map<String, Object> responseBody = new LinkedHashMap<>();
        responseBody.put("timestamp", ZonedDateTime.now());
        responseBody.put("status", status.value());
        responseBody.put("details", List.of(ex.getMessage()));

        return new ResponseEntity<>(responseBody, HttpStatus.BAD_REQUEST);
    }

    @Override
    public ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex, HttpHeaders headers,
                                                               HttpStatus status, WebRequest request) {
        List<String> detailList = new ArrayList<>();
        for (FieldError error : ex.getBindingResult().getFieldErrors()) {
            detailList.add(String.format("[%s] - %s", error.getField(), error.getDefaultMessage()));
        }

        Map<String, Object> responseBody = new LinkedHashMap<>();
        responseBody.put("timestamp", ZonedDateTime.now());
        responseBody.put("status", status.value());
        responseBody.put("detailList", detailList);

        return new ResponseEntity<>(responseBody, HttpStatus.BAD_REQUEST);
    }

}
