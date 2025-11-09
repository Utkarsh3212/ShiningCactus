package com.chalk.ffs.Condition;

import java.util.List;

public class CompositeCondition implements ConditionNode{
    private String logicType;
    private List<ConditionNode> conditions;

    public String getLogicType() {
        return logicType;
    }

    public void setLogicType(String logicType) {
        this.logicType = logicType;
    }

    public List<ConditionNode> getConditions() {
        return conditions;
    }

    public void setConditions(List<ConditionNode> conditions) {
        this.conditions = conditions;
    }
}
