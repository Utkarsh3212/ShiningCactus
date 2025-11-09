package com.chalk.ffs.Exceptions;

import com.chalk.ffs.Exceptions.Environment.EnvironmentNotFoundException;
import com.chalk.ffs.Exceptions.FeatureFlag.FeatureFlagNotFoundException;
import com.chalk.ffs.Exceptions.FeatureFlag.KeyAlreadyExistsException;
import com.chalk.ffs.Exceptions.Organization.OrganizationNotFoundException;
import com.chalk.ffs.Exceptions.Project.InvalidDateException;
import com.chalk.ffs.Exceptions.Project.ProjectNotFoundException;
import com.chalk.ffs.Exceptions.Rule.RuleNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;

@ControllerAdvice
public class GlobalExceptionHandler{

    private ResponseEntity<Map<String,Object>> buildErrorResponse(Exception e, HttpStatus status, String errorMessage){
        Map<String,Object> body = new LinkedHashMap<>();
        body.put("timestamp", LocalDateTime.now());
        body.put("status", status.value());
        body.put("error", errorMessage);
        body.put("message", e.getMessage());

        return ResponseEntity.status(status).body(body);
    }

    @ExceptionHandler(OrganizationNotFoundException.class)
    public ResponseEntity<Map<String,Object>> handleOrganizationNotFound(OrganizationNotFoundException e){
        return buildErrorResponse(e, HttpStatus.NOT_FOUND, "Organization Not Found");
    }

    @ExceptionHandler(ProjectNotFoundException.class)
    public ResponseEntity<Map<String,Object>> handleProjectNotFound(ProjectNotFoundException e){
        return buildErrorResponse(e, HttpStatus.NOT_FOUND, "Project Not Found");
    }

    @ExceptionHandler(EnvironmentNotFoundException.class)
    public ResponseEntity<Map<String,Object>> handleEnvironmentNotFound(EnvironmentNotFoundException e){
        return buildErrorResponse(e, HttpStatus.NOT_FOUND, "Environment Not Found");
    }

    @ExceptionHandler(InvalidDateException.class)
    public ResponseEntity<Map<String,Object>> handleInvalidDate(InvalidDateException e){
        return buildErrorResponse(e, HttpStatus.BAD_REQUEST, "Invalid date range");
    }

    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<Map<String,Object>> handleIllegalState(IllegalStateException e){
        return buildErrorResponse(e, HttpStatus.BAD_REQUEST, "Invalid parameter state");
    }

    @ExceptionHandler(FeatureFlagNotFoundException.class)
    public ResponseEntity<Map<String,Object>> handleFeatureFlagNotFound(FeatureFlagNotFoundException e){
        return buildErrorResponse(e,HttpStatus.NOT_FOUND,"Feature Flag Not Found");
    }

    @ExceptionHandler(RuleNotFoundException.class)
    public ResponseEntity<Map<String,Object>> handleRuleNotFound(RuleNotFoundException e){
        return buildErrorResponse(e,HttpStatus.NOT_FOUND,"Rule Not Found");
    }

    @ExceptionHandler(KeyAlreadyExistsException.class)
    public ResponseEntity<Map<String,Object>> handleKeyAlreadyExists(KeyAlreadyExistsException e){
        return buildErrorResponse(e,HttpStatus.CONFLICT,"Key Already Exists");
    }
}
