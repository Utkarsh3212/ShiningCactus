package com.chalk.ffs.DTO.Project;

import com.chalk.ffs.Entity.Project;
import com.chalk.ffs.Enums.ProjectStatus;
import com.chalk.ffs.Exceptions.Project.InvalidDateException;
import jakarta.annotation.PostConstruct;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

import static com.chalk.ffs.Enums.ProjectStatus.*;

public class ProjectDTO {

    private Long orgId;
    @NotBlank(message = "Project name cannot be blank")
    private String name;
    private ProjectStatus projectStatus;
    @NotNull(message = "Start date required")
    private LocalDate startDate;
    @NotNull(message = "End date required")
    private LocalDate endDate;

    @PostConstruct
    private void dateValidation(){
        if(startDate.isAfter(endDate))throw new InvalidDateException("Start date cannot be after end date");
        if(startDate.isEqual(LocalDate.now())) this.projectStatus=IN_PROGRESS;
        else this.projectStatus=PLANNED;
    }

    public ProjectDTO(){}

    public ProjectDTO(Project project){
        this.orgId =project.getOrganization().getId();
        this.name=project.getName();
        this.projectStatus=project.getProjectStatus();
        this.startDate=project.getStartDate();
        this.endDate=project.getEndDate();
    }

    public Long getOrgId() {
        return orgId;
    }

    public void setOrgId(Long orgId) {
        this.orgId = orgId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ProjectStatus getProjectStatus() {
        return projectStatus;
    }

    public void setProjectStatus(ProjectStatus projectStatus) {
        this.projectStatus = projectStatus;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }
}
