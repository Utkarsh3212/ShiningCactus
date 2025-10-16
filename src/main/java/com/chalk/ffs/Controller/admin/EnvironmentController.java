package com.chalk.ffs.Controller.admin;

import com.chalk.ffs.DTO.Environment.EnvironmentDTO;
import com.chalk.ffs.DTO.Environment.EnvironmentListDTO;
import com.chalk.ffs.Service.EnvironmentService;
import com.chalk.ffs.Service.ProjectService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
public class EnvironmentController {

    private final EnvironmentService environmentService;

    public EnvironmentController(EnvironmentService environmentService, ProjectService projectService){
        this.environmentService=environmentService;
    }

    @GetMapping("/projects/{projectId}/environments")
    public ResponseEntity<EnvironmentListDTO> getEnvironmentList(@PathVariable Long projectId){
        EnvironmentListDTO environmentListDTO=environmentService.getEnvironmentsById(projectId);
        return ResponseEntity.ok(environmentListDTO);
    }

    @PostMapping ("/projects/{projectId}/environments")
    public ResponseEntity<Void> addEnvironment(@PathVariable Long projectId,@Valid @RequestBody EnvironmentDTO environmentDTO){
        environmentService.addEnvironmentToProject(projectId,environmentDTO);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/projects/{projectId}/environments")
    public ResponseEntity<Void> removeEnvironment(@PathVariable Long projectId,@Valid @RequestBody EnvironmentDTO environmentDTO){
        environmentService.removeEnvironmentFromProject(projectId,environmentDTO);
        return ResponseEntity.ok().build();
    }
}
