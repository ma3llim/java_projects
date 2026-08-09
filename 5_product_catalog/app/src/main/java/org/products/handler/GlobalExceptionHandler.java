package org.products.handler;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.products.Dtos.response.ApiResponse;
import org.products.exceptions.CustomException;
import org.products.exceptions.ProductNotFound;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {
    @ExceptionHandler(ProductNotFound.class)
    public ResponseEntity<ApiResponse<Void>> handleProductNotFound(ProductNotFound ex, HttpServletRequest request) {
        log.warn("Product not found: {} | Path: {}", ex.getMessage(), request.getRequestURI());

        return buildErrorResponse(HttpStatus.NOT_FOUND, ex.getMessage(), List.of(), request.getRequestURI());
    }

    @ExceptionHandler(CustomException.class)
    public ResponseEntity<ApiResponse<Void>> handleCustomException(CustomException ex, HttpServletRequest request) {
        log.warn("Business error: {} | Path: {}", ex.getMessage(), request.getRequestURI());

        return buildErrorResponse(ex.getStatus(), ex.getMessage(), ex.getErrors(), request.getRequestURI());
    }

    @ExceptionHandler({
            MethodArgumentNotValidException.class,
            AuthorizationDeniedException.class
    })
    public ResponseEntity<ApiResponse<Void>> handleValidation(MethodArgumentNotValidException ex, HttpServletRequest request) {

        List<String> errors = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(e -> e.getField() + ": " + e.getDefaultMessage())
                .toList();

        log.warn("Validation failed: {} | Path: {}", errors, request.getRequestURI());

        return buildErrorResponse(HttpStatus.BAD_REQUEST, "Validation failed", errors, request.getRequestURI()
        );
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiResponse<Void>> handleMissingBody(HttpMessageNotReadableException ex, HttpServletRequest request) {
        log.warn("Missing request body: {} | Path: {}", ex.getMessage(), request.getRequestURI());

        return buildErrorResponse(HttpStatus.BAD_REQUEST, "Request body is required", List.of(ex.getMessage()), request.getRequestURI());
    }


    // ==================== 5xx SERVER ERRORS ====================
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Void>> handleAll(Exception ex, HttpServletRequest request) {
        log.error("Unexpected error: {}", ex.getMessage(), ex);
        return buildErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, "Something went wrong. Please try again later.", List.of(), request.getRequestURI());
    }

    private ResponseEntity<ApiResponse<Void>> buildErrorResponse(
            HttpStatus status, String message, List<String> errors, String path) {
        return ResponseEntity.status(status).body(ApiResponse.error(message, errors, path));
    }
}