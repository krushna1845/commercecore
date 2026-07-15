package com.krushna.commercecore.exception;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.NoHandlerFoundException;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    private Map<String, Object> createErrorResponse(HttpStatus status, String error, String message) {
        Map<String, Object> response = new HashMap<>();
        response.put("timestamp", LocalDateTime.now());
        response.put("status", status.value());
        response.put("error", error);
        response.put("message", message);
        response.put("success", false);
        return response;
    }

    private Map<String, Object> createSuccessResponse(String message) {
        Map<String, Object> response = new HashMap<>();
        response.put("timestamp", LocalDateTime.now());
        response.put("status", HttpStatus.OK.value());
        response.put("message", message);
        response.put("success", true);
        return response;
    }

    // Product not found
    @ExceptionHandler(ProductNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleProductNotFound(ProductNotFoundException ex) {
        Map<String, Object> response = createErrorResponse(
            HttpStatus.NOT_FOUND,
            "Product Not Found",
            ex.getMessage() + " Please check our other amazing products or try searching with different keywords."
        );
        return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
    }

    // User not found
    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleUserNotFound(UserNotFoundException ex) {
        Map<String, Object> response = createErrorResponse(
            HttpStatus.NOT_FOUND,
            "User Not Found",
            ex.getMessage() + " Please verify your account or contact support for assistance."
        );
        return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
    }

    // Order not found
    @ExceptionHandler(OrderNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleOrderNotFound(OrderNotFoundException ex) {
        Map<String, Object> response = createErrorResponse(
            HttpStatus.NOT_FOUND,
            "Order Not Found",
            ex.getMessage() + " Please check your order history or contact customer support."
        );
        return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
    }

    // Validation errors
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidationException(MethodArgumentNotValidException ex) {
        Map<String, String> errors = ex.getBindingResult()
            .getFieldErrors()
            .stream()
            .collect(Collectors.toMap(
                error -> error.getField(),
                error -> error.getDefaultMessage() != null ? error.getDefaultMessage() : "Invalid value"
            ));

        Map<String, Object> response = createErrorResponse(
            HttpStatus.BAD_REQUEST,
            "Validation Failed",
            "Please check your input and try again. Some fields need correction."
        );
        response.put("errors", errors);
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    // Constraint violation
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<Map<String, Object>> handleConstraintViolation(ConstraintViolationException ex) {
        Map<String, String> errors = ex.getConstraintViolations()
            .stream()
            .collect(Collectors.toMap(
                violation -> violation.getPropertyPath().toString(),
                ConstraintViolation::getMessage
            ));

        Map<String, Object> response = createErrorResponse(
            HttpStatus.BAD_REQUEST,
            "Validation Failed",
            "Please check your input and try again."
        );
        response.put("errors", errors);
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    // Access denied
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<Map<String, Object>> handleAccessDenied(AccessDeniedException ex) {
        Map<String, Object> response = createErrorResponse(
            HttpStatus.FORBIDDEN,
            "Access Denied",
            "You don't have permission to access this resource. Please contact administrator if you believe this is an error."
        );
        return new ResponseEntity<>(response, HttpStatus.FORBIDDEN);
    }

    // Bad credentials
    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<Map<String, Object>> handleBadCredentials(BadCredentialsException ex) {
        Map<String, Object> response = createErrorResponse(
            HttpStatus.UNAUTHORIZED,
            "Authentication Failed",
            "Invalid email or password. Please try again or reset your password if you've forgotten it."
        );
        return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
    }

    // 404 Not Found
    @ExceptionHandler(NoHandlerFoundException.class)
    public ResponseEntity<Map<String, Object>> handleNotFound(NoHandlerFoundException ex) {
        Map<String, Object> response = createErrorResponse(
            HttpStatus.NOT_FOUND,
            "Resource Not Found",
            "The requested resource could not be found. Please check the URL or navigate to our homepage."
        );
        return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
    }

    // Generic runtime exception
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<Map<String, Object>> handleRuntimeException(RuntimeException ex) {
        log.error("Exception: ", ex);
        try {
            java.nio.file.Files.writeString(java.nio.file.Paths.get("error.log"), 
                ex.toString() + "\n" + java.util.Arrays.toString(ex.getStackTrace()));
        } catch (Exception e) {}
        
        Map<String, Object> response = createErrorResponse(
            HttpStatus.INTERNAL_SERVER_ERROR,
            "Internal Server Error",
            "Something went wrong on our end. Our team has been notified and we're working to fix it. Please try again later."
        );
        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    // Generic exception
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleGenericException(Exception ex) {
        log.error("Exception: ", ex);
        try {
            java.nio.file.Files.writeString(java.nio.file.Paths.get("error.log"), 
                ex.toString() + "\n" + java.util.Arrays.toString(ex.getStackTrace()));
        } catch (Exception e) {}
        
        Map<String, Object> response = createErrorResponse(
            HttpStatus.INTERNAL_SERVER_ERROR,
            "Internal Server Error",
            "An unexpected error occurred. Please try again or contact support if the problem persists."
        );
        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
