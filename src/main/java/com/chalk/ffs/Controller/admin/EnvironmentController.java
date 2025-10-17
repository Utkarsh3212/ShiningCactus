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

    @PostMapping ("/projects/{projectId}/environments/{environmentId}")
    public ResponseEntity<EnvironmentDTO> addEnvironmentToProject(@PathVariable Long projectId,@PathVariable Long environmentId){
        EnvironmentDTO environmentDTO=environmentService.addEnvironmentToProject(projectId,environmentId);
        return ResponseEntity.ok(environmentDTO);
    }

    @DeleteMapping("/projects/{projectId}/environments/{environmentId}")
    public ResponseEntity<Void> removeEnvironmentFromProject(@PathVariable Long projectId, @PathVariable Long environmentId){
        environmentService.removeEnvironmentFromProject(projectId,environmentId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/environments")
    public ResponseEntity<EnvironmentDTO> createEnvironment(@RequestBody EnvironmentDTO environmentDTO) {
        EnvironmentDTO created = environmentService.createEnvironment(environmentDTO);
        return ResponseEntity.ok(created);
    }

    @DeleteMapping("/environments/{id}")
    public ResponseEntity<Void> deleteEnvironment(@PathVariable Long id) {
        environmentService.deleteEnvironment(id);
        return ResponseEntity.ok().build();
    }
}
