package com.chalk.ffs.Entity;

import com.chalk.ffs.Condition.ConditionConverter;
import com.chalk.ffs.Condition.ConditionNode;
import com.chalk.ffs.DTO.Rule.RuleDTO;
import jakarta.persistence.*;

@Entity
public class Rule {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @ManyToOne
    @JoinColumn(name = "flag_id")
    private FeatureFlag featureFlag;
    private String name;
    private String variantKey;

    @Column(columnDefinition = "text")
    @Convert(converter = ConditionConverter.class)
    private ConditionNode conditions;

    public Rule() {}

    public Rule(RuleDTO ruleDTO){
        this.conditions=ruleDTO.getConditions();
        this.name=ruleDTO.getName();
        this.variantKey=ruleDTO.getVariantKey();
    }

    public ConditionNode getConditions() {
        return conditions;
    }

    public void setConditions(ConditionNode conditions) {
        this.conditions = conditions;
    }

    public FeatureFlag getFeatureFlag() {
        return featureFlag;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getVariantKey() {
        return variantKey;
    }

    public void setVariantKey(String variantKey) {
        this.variantKey = variantKey;
    }

    public void setFeatureFlag(FeatureFlag featureFlag) {
        this.featureFlag = featureFlag;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}
