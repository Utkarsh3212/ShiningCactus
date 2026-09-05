package com.chalk.ffs;

import com.chalk.ffs.Condition.CompositeCondition;
import com.chalk.ffs.Condition.SimpleCondition;
import com.chalk.ffs.Service.ConditionEvaluator;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ConditionEvaluatorTest {
    private final ConditionEvaluator evaluator = new ConditionEvaluator();

    @Test
    void evaluatesSimpleAndNumericConditions() {
        SimpleCondition plan = new SimpleCondition();
        plan.setFlag("plan");
        plan.setOperator("equals");
        plan.setValue("pro");

        SimpleCondition seats = new SimpleCondition();
        seats.setFlag("seats");
        seats.setOperator(">=");
        seats.setValue(10);

        CompositeCondition group = new CompositeCondition();
        group.setLogicType("AND");
        group.setConditions(List.of(plan, seats));

        assertTrue(evaluator.matches(group, Map.of("plan", "pro", "seats", 12)));
        assertFalse(evaluator.matches(group, Map.of("plan", "free", "seats", 12)));
    }

    @Test
    void supportsMembershipAndExistenceOperators() {
        SimpleCondition condition = new SimpleCondition();
        condition.setFlag("country");
        condition.setOperator("in");
        condition.setValue(List.of("IN", "US"));

        assertTrue(evaluator.matches(condition, Map.of("country", "IN")));
        condition.setOperator("not_exists");
        assertTrue(evaluator.matches(condition, Map.of()));
    }
}
