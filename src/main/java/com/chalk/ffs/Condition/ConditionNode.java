package com.chalk.ffs.Condition;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(use= JsonTypeInfo.Id.NAME,property = "type")
@JsonSubTypes({
        @JsonSubTypes.Type(value=SimpleCondition.class,name="condition"),
        @JsonSubTypes.Type(value=CompositeCondition.class,name="group")
})
public interface ConditionNode {}
