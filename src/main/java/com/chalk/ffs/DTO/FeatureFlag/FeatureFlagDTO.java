package com.chalk.ffs.DTO.FeatureFlag;

import com.chalk.ffs.DTO.Rule.RuleDTO;
import com.chalk.ffs.Entity.FeatureFlag;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

import java.util.Set;
import java.util.stream.Collectors;

public class FeatureFlagDTO {
    private Long id;

    @NotBlank(message = "Key cannot be blank")
    @Pattern(regexp = "^\\S+$", message = "Key cannot contain whitespace")
    private String key;

    @NotBlank(message = "Name cannot be blank")
    private String name;

    private String description;
    private String type;
    private String defaultValue;
    private Long environmentId;
    private Boolean enabled;
    private Set<RuleDTO> rules;

    public FeatureFlagDTO(){}

    public FeatureFlagDTO(FeatureFlag featureFlag){
        this.id=featureFlag.getId();
        this.key=featureFlag.getKey();
        this.name=featureFlag.getName();
        this.description=featureFlag.getDescription();
        this.type=featureFlag.getType().name();
        this.defaultValue=featureFlag.getDefaultValue();
        this.enabled=featureFlag.getEnabled();
        this.environmentId=featureFlag.getEnvironment().getId();
        this.rules=featureFlag.getRules()
                .stream()
                .map(rule->new RuleDTO(rule, featureFlag.getId()))
                .collect(Collectors.toSet());
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getDefaultValue() {
        return defaultValue;
    }

    public void setDefaultValue(String defaultValue) {
        this.defaultValue = defaultValue;
    }

    public Long getEnvironmentId() {
        return environmentId;
    }

    public void setEnvironmentId(Long environmentId) {
        this.environmentId = environmentId;
    }

    public Boolean getEnabled() {
        return enabled;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }

    public Set<RuleDTO> getRules() {
        return rules;
    }

    public void setRules(Set<RuleDTO> rules) {
        this.rules = rules;
    }
}
