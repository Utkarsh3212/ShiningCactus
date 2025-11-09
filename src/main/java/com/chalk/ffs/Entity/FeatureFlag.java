package com.chalk.ffs.Entity;

import com.chalk.ffs.DTO.FeatureFlag.FeatureFlagDTO;
import jakarta.persistence.*;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Entity
public class FeatureFlag {

    public enum FlagType{
        BOOLEAN,
        STRING,
        JSON,
        NUMBER
    }

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Column(nullable = false,updatable = false,unique = true)
    private String key;
    private String name;
    private String description;

    @ManyToOne
    @JoinColumn(name = "environment_id")
    private Environment environment;

    @OneToMany(mappedBy = "featureFlag",cascade = CascadeType.ALL,orphanRemoval = true)
    private Set<Rule> rules=new HashSet<>();

    private FlagType type;
    private String defaultValue;
    private Boolean enabled;

    public FeatureFlag(){}

    public FeatureFlag(FeatureFlagDTO featureFlagDTO) {
        this.key = featureFlagDTO.getKey();
        this.name = featureFlagDTO.getName();
        this.description = featureFlagDTO.getDescription();
        if(featureFlagDTO.getType()!=null){
            this.type = FeatureFlag.FlagType.valueOf(featureFlagDTO.getType().toUpperCase());
            this.defaultValue = featureFlagDTO.getDefaultValue();
        }else {
            this.type= FlagType.BOOLEAN;
            this.defaultValue="false";
        }
        this.enabled = Boolean.TRUE.equals(featureFlagDTO.getEnabled());
    }

    public void updateFromDTO(FeatureFlagDTO featureFlagDTO) {

        if (featureFlagDTO.getName() != null) {
            this.name = featureFlagDTO.getName();
        }

        if (featureFlagDTO.getDescription() != null) {
            this.description = featureFlagDTO.getDescription();
        }

        if (featureFlagDTO.getType() != null) {
            this.type = FlagType.valueOf(featureFlagDTO.getType().toUpperCase());
        }

        if (featureFlagDTO.getDefaultValue() != null) {
            this.defaultValue = featureFlagDTO.getDefaultValue();
        }

        this.enabled = Boolean.TRUE.equals(featureFlagDTO.getEnabled());
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

    public Environment getEnvironment() {
        return environment;
    }

    public void setEnvironment(Environment environment) {
        this.environment = environment;
    }

    public Set<Rule> getRules() {
        return rules;
    }

    public void setRules(Set<Rule> rules) {
        this.rules = rules;
    }

    public FlagType getType() {
        return type;
    }

    public void setType(FlagType type) {
        this.type = type;
    }

    public String getDefaultValue() {
        return defaultValue;
    }

    public void setDefaultValue(String defaultValue) {
        this.defaultValue = defaultValue;
    }

    public Boolean getEnabled() {
        return enabled;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }
}
