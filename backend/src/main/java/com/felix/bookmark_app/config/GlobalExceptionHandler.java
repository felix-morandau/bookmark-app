package com.felix.bookmark_app.config;

import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Map<String, String> handleValidationExceptions(MethodArgumentNotValidException ex) {
        BindingResult result = ex.getBindingResult();
        Map<String, String> errorMap = new HashMap<>();

        for (FieldError error : result.getFieldErrors()) {
            errorMap.put(error.getField(), error.getDefaultMessage());
        }

        log.error("Validation error: {}", errorMap);

        return errorMap;
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public Map<String, String> handleHttpMessageNotReadableException(HttpMessageNotReadableException ex) {
        Map<String, String> errorMap = new HashMap<>();

        if (ex.getCause() instanceof InvalidFormatException invalidFormatException) {
            if (invalidFormatException.getTargetType().isEnum()) {
                String invalidValue = invalidFormatException.getValue().toString();
                Class<?> enumType = invalidFormatException.getTargetType();
                String acceptedValues = Arrays.stream(enumType.getEnumConstants())
                        .map(Object::toString)
                        .collect(Collectors.joining(", "));

                errorMap.put("error", "Invalid value: " + invalidValue);
                errorMap.put("acceptedValues", acceptedValues);
            }
        } else {
            errorMap.put("error", "Invalid request body format.");
        }

        log.error("JSON parse error: {}", errorMap);

        return errorMap;
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(DataIntegrityViolationException.class)
    public Map<String, String> handleDataIntegrityViolationException(DataIntegrityViolationException ex) {
        Map<String, String> errorResponse = new HashMap<>();
        // You can inspect the exception message to tailor your response
        String detailedMessage = ex.getMostSpecificCause().getMessage();
        errorResponse.put("error", "Unique constraint violation");
        errorResponse.put("message", detailedMessage);
        log.error("Data integrity violation: {}", errorResponse);
        return errorResponse;
    }

    @ExceptionHandler(NoUserFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Map<String, String> handleNoUserFoundException(NoUserFoundException ex) {
        Map<String, String> errorResponse = new HashMap<>();
        errorResponse.put("error", "User not found");
        errorResponse.put("message", ex.getMessage());
        return errorResponse;
    }

    @ExceptionHandler(NoBookmarkFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Map<String, String> handleNoBookmarkFoundException(NoBookmarkFoundException ex) {
        Map<String, String> errorResponse = new HashMap<>();
        errorResponse.put("error", "Bookmark not found");
        errorResponse.put("message", ex.getMessage());
        return errorResponse;
    }
}
