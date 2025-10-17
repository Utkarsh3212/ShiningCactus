package com.chalk.ffs.DTO.Environment;

import com.chalk.ffs.Entity.Project;

import java.util.List;

public class EnvironmentListDTO {
    private String projectName;
    private List<EnvironmentDTO> environmentDTOList;

    public EnvironmentListDTO(){}

    public EnvironmentListDTO(Project project){
        this.projectName= project.getName();
        this.environmentDTOList=project.getEnvironments().stream().map(EnvironmentDTO::new).toList();
    }

    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    public List<EnvironmentDTO> getEnvironmentDTOList() {
        return environmentDTOList;
    }

    public void setEnvironmentDTOList(List<EnvironmentDTO> environmentDTOList) {
        this.environmentDTOList = environmentDTOList;
    }
}
