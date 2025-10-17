package com.chalk.ffs.DTO.Organization;

import com.chalk.ffs.Entity.Organization;
import com.chalk.ffs.Enums.OrganizationStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

public class OrganizationDTO {

    private Long id;
    @NotBlank(message = "Organization cannot be blank")
    private String name;
    private Integer projectCount;
    private OrganizationStatus status;

    public String getName() {
        return name;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public OrganizationDTO(){
        this.projectCount=0;
        this.status=OrganizationStatus.ACTIVE;
    }

    public OrganizationDTO(Organization organization){
        this.id= organization.getId();
        this.name=organization.getName();
        this.projectCount=organization.getProjectCount();
        this.status=organization.getStatus();
    }
}
