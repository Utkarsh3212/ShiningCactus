package com.chalk.ffs.DTO.Environment;

import com.chalk.ffs.Entity.Environment;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import com.fasterxml.jackson.annotation.JsonProperty;

public class EnvironmentDTO {

    private Long id;
    @NotNull
    private Long orgId;
    @NotBlank(message = "Environment name cannot be blank")
    private String env;
    private String description;
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private String clientKey;

    public EnvironmentDTO(){}

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public EnvironmentDTO(Environment environment){
        this.id= environment.getId();
        this.orgId= environment.getOrganization().getId();
        this.env=environment.getEnv();
        this.description=environment.getDescription();
        this.clientKey=environment.getClientKey();
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

    public Long getOrgId() {
        return orgId;
    }

    public void setOrgId(Long orgId) {
        this.orgId = orgId;
    }

    public String getClientKey() { return clientKey; }
    public void setClientKey(String clientKey) { this.clientKey = clientKey; }
}
