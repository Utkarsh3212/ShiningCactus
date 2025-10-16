package com.chalk.ffs.DTO.Project;

import com.chalk.ffs.Entity.Organization;
import com.chalk.ffs.Entity.Project;

import java.util.List;

public class ProjectListDTO {
    private String organizationName;
    private List<String> projectList;

    public ProjectListDTO(Organization organization){
        this.organizationName=organization.getName();
        this.projectList=organization.getProjectList().stream().map(Project::getName).toList();
    }

    public String getOrganizationName() {
        return organizationName;
    }

    public void setOrganizationName(String organizationName) {
        this.organizationName = organizationName;
    }

    public List<String> getProjectList() {
        return projectList;
    }

    public void setProjectList(List<String> projectList) {
        this.projectList = projectList;
    }
}
