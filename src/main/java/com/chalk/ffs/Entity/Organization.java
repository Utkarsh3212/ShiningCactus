package com.chalk.ffs.Entity;

import com.chalk.ffs.DTO.Organization.OrganizationDTO;
import com.chalk.ffs.Enums.OrganizationStatus;
import jakarta.persistence.*;

import java.util.List;

@Entity
public class Organization {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String name;
    private Integer projectCount;
    private OrganizationStatus status;

    @OneToMany(mappedBy = "organization")
    private List<Project> projectList;

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

    public Organization(OrganizationDTO organizationDTO){
        this.name=organizationDTO.getName();
        this.projectCount=organizationDTO.getProjectCount();
        this.status=organizationDTO.getStatus();
    }

    public Organization() {}
}
