package com.chalk.ffs.Entity;

import com.chalk.ffs.DTO.Organization.OrganizationDTO;
import com.chalk.ffs.Enums.OrganizationStatus;
import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
public class Organization {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String name;
    private Integer projectCount;
    private OrganizationStatus status;

    @OneToMany(mappedBy = "organization",cascade = {CascadeType.PERSIST,CascadeType.REMOVE})
    private List<Project> projectList = new ArrayList<>();

    @OneToMany(mappedBy = "organization",cascade = {CascadeType.PERSIST,CascadeType.REMOVE})
    private Set<Environment> environmentList=new HashSet<>();

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getProjectCount() {
        return projectCount;
    }

    public void setProjectCount(Integer projectCount) {
        this.projectCount = projectCount;
    }

    public OrganizationStatus getStatus() {
        return status;
    }

    public void setStatus(OrganizationStatus status) {
        this.status = status;
    }

    public List<Project> getProjectList() {
        return projectList;
    }

    public void setProjectList(List<Project> projectList) {
        this.projectList = projectList;
    }

    public Set<Environment> getEnvironmentList() {
        return environmentList;
    }

    public void setEnvironmentList(Set<Environment> environmentList) {
        this.environmentList = environmentList;
    }

    public Organization(OrganizationDTO organizationDTO){
        this.name=organizationDTO.getName();
        this.projectCount=organizationDTO.getProjectCount();
        this.status=organizationDTO.getStatus();
    }

    public Organization() {}
}
