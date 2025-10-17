package com.chalk.ffs.DTO.Environment;

import com.chalk.ffs.Entity.Environment;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

public class EnvironmentDTO {

    private Long id;
    @NotBlank(message = "Environment name cannot be blank")
    private String env;
    private String description;

    public EnvironmentDTO(){}

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public EnvironmentDTO(Environment environment){
        this.id= environment.getId();
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
