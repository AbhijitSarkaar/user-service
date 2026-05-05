
package com.rest.user_service.exception;

import com.rest.user_service.exception.response.APIResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> customMethodArgumentNotValidExceptionHandler(MethodArgumentNotValidException e) {
        Map<String, String> map = new HashMap<>();
        e.getBindingResult().getFieldErrors().forEach(item -> {
            map.put(item.getField(), item.getDefaultMessage());
        });

        return new ResponseEntity<Map<String, String>>(
                map,
                HttpStatus.OK
        );
    }

    @ExceptionHandler(CustomResourceNotFoundException.class)
    public ResponseEntity<APIResponse> customResourceNotFoundHandler(CustomResourceNotFoundException e) {
        return new ResponseEntity<APIResponse>(
                new APIResponse(e.getMessage()),
                HttpStatus.INTERNAL_SERVER_ERROR
        );
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<APIResponse> customRuntimeExceptionHandler(RuntimeException e) {
        return new ResponseEntity<APIResponse>(
                new APIResponse(e.getMessage()),
                HttpStatus.INTERNAL_SERVER_ERROR
        );
    }

}
