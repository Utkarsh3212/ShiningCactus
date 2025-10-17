package com.chalk.ffs.Controller.admin;

import com.chalk.ffs.DTO.Project.ProjectDTO;
import com.chalk.ffs.DTO.Project.ProjectListDTO;
import com.chalk.ffs.Service.ProjectService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
public class ProjectController{

    private final ProjectService projectService;

    public ProjectController(ProjectService projectService){
        this.projectService = projectService;
    }

    @PostMapping("/organizations/{orgId}/projects")
    public ResponseEntity<ProjectDTO> createProjectByOrgId(@PathVariable Long orgId,@Valid @RequestBody ProjectDTO projectDTO){
        ProjectDTO created=projectService.createProjectByOrgId(orgId,projectDTO);
        return ResponseEntity.ok(created);
    }

    @GetMapping("/organizations/{orgId}/projects")
    public ResponseEntity<ProjectListDTO> getProjectListByOrgId(@PathVariable Long orgId){
        ProjectListDTO projectListDTO=projectService.getProjectListByOrgId(orgId);
        return ResponseEntity.ok(projectListDTO);
    }

    @GetMapping("/projects/{projectId}")
    public ResponseEntity<ProjectDTO> getProjectById(@PathVariable Long projectId){
        ProjectDTO projectDTO=projectService.getProjectById(projectId);
        return ResponseEntity.ok(projectDTO);
    }
}
