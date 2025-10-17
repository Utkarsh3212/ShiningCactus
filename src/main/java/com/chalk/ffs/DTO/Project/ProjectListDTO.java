package com.chalk.ffs.DTO.Project;

import com.chalk.ffs.Entity.Organization;
import com.chalk.ffs.Entity.Project;

import java.util.ArrayList;
import java.util.List;

public class ProjectListDTO {
    private String organizationName;
    private List<List<String>> projectList;

    public ProjectListDTO(Organization organization){
        this.organizationName=organization.getName();
        this.projectList=organization.getProjectList().stream().map(project -> {
            List<String> obj=new ArrayList<>();
            obj.add(project.getId().toString());
            obj.add(project.getName());
            return obj;
        }).toList();
    }

    public String getOrganizationName() {
        return organizationName;
    }

    public void setOrganizationName(String organizationName) {
        this.organizationName = organizationName;
    }

    public List<List<String>> getProjectList() {
        return projectList;
    }

    public void setProjectList(List<List<String>> projectList) {
        this.projectList = projectList;
    }
}
