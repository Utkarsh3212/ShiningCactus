package com.chalk.ffs.Controller.admin;

import com.chalk.ffs.DTO.Environment.EnvironmentDTO;
import com.chalk.ffs.DTO.Environment.EnvironmentListDTO;
import com.chalk.ffs.Entity.Project;
import com.chalk.ffs.Service.EnvironmentService;
import com.chalk.ffs.Service.ProjectService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
public class EnvironmentController {

    private final EnvironmentService environmentService;
    private final ProjectService projectService;

    public EnvironmentController(EnvironmentService environmentService, ProjectService projectService){
        this.environmentService=environmentService;
        this.projectService=projectService;
    }

    @GetMapping("/projects/{projectId}/environments")
    public ResponseEntity<EnvironmentListDTO> getEnvironment(@PathVariable Long projectId){
        Project project=projectService.getProjectById(projectId);
        EnvironmentListDTO environmentListDTO=new EnvironmentListDTO(project);

        return ResponseEntity.ok(environmentListDTO);
    }

    @PostMapping ("/projects/{projectId}")
    public ResponseEntity<Void> addEnvironment(@PathVariable Long projectId,@RequestBody EnvironmentDTO environmentDTO){
        environmentService.addEnvironmentToProject(projectId,environmentDTO);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/projects/{projectId}")
    public ResponseEntity<Void> removeEnvironment(@PathVariable Long projectId,@RequestBody EnvironmentDTO environmentDTO){
        environmentService.removeEnvironmentFromProject(projectId,environmentDTO);
        return ResponseEntity.ok().build();
    }
}
