package com.chalk.ffs.DTO.Rule;

import com.chalk.ffs.Condition.ConditionNode;
import com.chalk.ffs.Entity.Rule;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class RuleDTO {

    private Long id;
    @NotBlank
    private String name;
    private Long featureFlagId;
    @NotNull
    private ConditionNode conditions;

    public RuleDTO(){}

    public RuleDTO(Rule rule,Long featureFlagId){
        this.id=rule.getId();
        this.featureFlagId=featureFlagId;
        this.conditions=rule.getConditions();
        this.name= rule.getName();
    }

    public RuleDTO(Rule rule){
        this.id=rule.getId();
        this.featureFlagId=rule.getFeatureFlag().getId();
        this.conditions=rule.getConditions();
        this.name=rule.getName();
    }

    @Override
    public String toString() {
        return "RuleDTO{name=" + name + ", conditions=" + conditions + "}";
    }


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getFeatureFlagId() {
        return featureFlagId;
    }

    public void setFeatureFlagId(Long featureFlagId) {
        this.featureFlagId = featureFlagId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ConditionNode getConditions() {
        return conditions;
    }

    public void setConditions(ConditionNode conditions) {
        this.conditions = conditions;
    }
}
