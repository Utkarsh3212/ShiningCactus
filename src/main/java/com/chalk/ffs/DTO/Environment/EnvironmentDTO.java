package com.chalk.ffs.DTO.Environment;

import com.chalk.ffs.Entity.Environment;

public class EnvironmentDTO {
    private String env;
    private String description;

    public EnvironmentDTO(){}

    public EnvironmentDTO(Environment environment){
        this.env=environment.getEnv();
        this.description=getDescription();
    }

    public String getEnv() {
        return env;
    }

    public void setEnv(String env) {
        this.env = env;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
