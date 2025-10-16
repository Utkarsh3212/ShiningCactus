package com.chalk.ffs.Entity;

import com.chalk.ffs.DTO.Project.ProjectDTO;
import com.chalk.ffs.Enums.ProjectStatus;
import com.chalk.ffs.Exceptions.Project.InvalidDateException;
import jakarta.persistence.*;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Entity
public class Project{
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne
    @JoinColumn(name="organization_id")
    private Organization organization;

    private String name;
    private ProjectStatus projectStatus;
    private LocalDate startDate;
    private LocalDate endDate;

    @ManyToMany(mappedBy = "projects")
    private Set<Environment> environments = new HashSet<>();

    public Project(){}
    public Project(ProjectDTO projectDTO, Organization organization){
        this.organization=organization;
        this.name=projectDTO.getName();
        this.startDate=projectDTO.getStartDate();
        this.endDate=projectDTO.getEndDate();

        if(startDate.isAfter(endDate))throw new InvalidDateException("The start date cannot be after end date");
        this.projectStatus=startDate.isEqual(LocalDate.now())?ProjectStatus.IN_PROGRESS:ProjectStatus.PLANNED;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Organization getOrganization() {
        return organization;
    }

    public void setOrganization(Organization organization) {
        this.organization = organization;
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

    public Set<Environment> getEnvironments() {
        return environments;
    }

    public void setEnvironments(Set<Environment> environments) {
        this.environments = environments;
    }
}
