package com.chalk.ffs.Exceptions;

import com.chalk.ffs.Exceptions.Environment.EnvironmentNotFoundException;
import com.chalk.ffs.Exceptions.Organization.OrganizationNotFoundException;
import com.chalk.ffs.Exceptions.Project.InvalidDateException;
import com.chalk.ffs.Exceptions.Project.ProjectNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;

@ControllerAdvice
public class GlobalExceptionHandler{

    @ExceptionHandler(OrganizationNotFoundException.class)
    public ResponseEntity<Map<String,Object>> handleOrganizationNotFound(OrganizationNotFoundException e){
        Map<String,Object> body= new LinkedHashMap<>();
        body.put("timestamp", LocalDateTime.now());
        body.put("status:", HttpStatus.NOT_FOUND);
        body.put("error:", "Organization Not Found");
        body.put("message", e.getMessage());

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(body);
    }

    @ExceptionHandler(ProjectNotFoundException.class)
    public ResponseEntity<Map<String,Object>> handleProjectNotFound(ProjectNotFoundException e){
        Map<String,Object> body= new LinkedHashMap<>();
        body.put("timestamp", LocalDateTime.now());
        body.put("status:", HttpStatus.NOT_FOUND);
        body.put("error:", "Project Not Found");
        body.put("message", e.getMessage());

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(body);
    }

    @ExceptionHandler(EnvironmentNotFoundException.class)
    public ResponseEntity<Map<String,Object>> handleEnvironmentNotFound(EnvironmentNotFoundException e){
        Map<String,Object> body= new LinkedHashMap<>();
        body.put("timestamp", LocalDateTime.now());
        body.put("status:", HttpStatus.NOT_FOUND);
        body.put("error:", "Environment Not Found");
        body.put("message", e.getMessage());

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(body);
    }

    @ExceptionHandler(InvalidDateException.class)
    public ResponseEntity<Map<String,Object>> handleInvalidDate(InvalidDateException e){
        Map<String,Object> body= new LinkedHashMap<>();
        body.put("timestamp", LocalDateTime.now());
        body.put("status:", HttpStatus.BAD_REQUEST);
        body.put("error:", "Invalid date range");
        body.put("message", e.getMessage());

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
    }
}
