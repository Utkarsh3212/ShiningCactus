package com.chalk.ffs.DTO.Environment;

import com.chalk.ffs.Entity.Environment;
import com.chalk.ffs.Entity.Project;

import java.util.List;

public class EnvironmentListDTO {
    private String projectName;
    private List<String> environments;

    public EnvironmentListDTO(){}

    public EnvironmentListDTO(Project project){
        this.projectName= project.getName();
        this.environments=project.getEnvironments().stream().map(Environment::getEnv).toList();
    }
}
