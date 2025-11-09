package com.chalk.ffs.DTO.FeatureFlag;

import com.chalk.ffs.Entity.FeatureFlag;

public class FeatureFlagSummaryDTO {
    private Long id;
    private String key;
    private String name;
    private String description;
    private String type;
    private String defaultValue;

    public FeatureFlagSummaryDTO(FeatureFlag featureFlag) {
        this.id = featureFlag.getId();
        this.key = featureFlag.getKey();
        this.name = featureFlag.getName();
        this.description = featureFlag.getDescription();
        this.type = featureFlag.getType().name();
        this.defaultValue = featureFlag.getDefaultValue();
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
}
