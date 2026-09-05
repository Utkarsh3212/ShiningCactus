package com.chalk.ffs.Service;

import com.chalk.ffs.Condition.CompositeCondition;
import com.chalk.ffs.Condition.ConditionNode;
import com.chalk.ffs.Condition.SimpleCondition;
import java.math.BigDecimal;
import java.util.Collection;
import java.util.Map;
import java.util.Objects;

public class ConditionEvaluator {
    public boolean matches(ConditionNode node, Map<String, Object> context) {
        if (node instanceof SimpleCondition simple) {
            return matchesSimple(simple, context);
        }
        if (node instanceof CompositeCondition composite) {
            String logic = composite.getLogicType() == null ? "AND" : composite.getLogicType().toUpperCase();
            var conditions = composite.getConditions() == null ? java.util.List.<ConditionNode>of() : composite.getConditions();
            if ("OR".equals(logic)) {
                return conditions.stream().anyMatch(condition -> matches(condition, context));
            }
            return conditions.stream().allMatch(condition -> matches(condition, context));
        }
        return false;
    }

    private boolean matchesSimple(SimpleCondition condition, Map<String, Object> context) {
        Object actual = context.get(condition.getFlag());
        Object expected = condition.getValue();
        String operator = condition.getOperator() == null ? "equals" : condition.getOperator().toLowerCase();
        return switch (operator) {
            case "equals", "equal", "eq", "==" -> Objects.equals(stringify(actual), stringify(expected));
            case "not_equals", "not_equal", "neq", "!=" -> !Objects.equals(stringify(actual), stringify(expected));
            case "in" -> asCollection(expected).stream().anyMatch(item -> Objects.equals(stringify(actual), stringify(item)));
            case "not_in" -> asCollection(expected).stream().noneMatch(item -> Objects.equals(stringify(actual), stringify(item)));
            case "contains" -> actual != null && String.valueOf(actual).contains(String.valueOf(expected));
            case "starts_with" -> actual != null && String.valueOf(actual).startsWith(String.valueOf(expected));
            case "ends_with" -> actual != null && String.valueOf(actual).endsWith(String.valueOf(expected));
            case "greater_than", ">" -> compare(actual, expected) > 0;
            case "greater_than_or_equal", ">=" -> compare(actual, expected) >= 0;
            case "less_than", "<" -> compare(actual, expected) < 0;
            case "less_than_or_equal", "<=" -> compare(actual, expected) <= 0;
            case "exists" -> actual != null;
            case "not_exists" -> actual == null;
            default -> false;
        };
    }

    private Object stringify(Object value) {
        return value == null ? null : String.valueOf(value);
    }

    private int compare(Object actual, Object expected) {
        if (actual == null || expected == null) return -1;
        try {
            return new BigDecimal(String.valueOf(actual)).compareTo(new BigDecimal(String.valueOf(expected)));
        } catch (NumberFormatException ignored) {
            return String.valueOf(actual).compareTo(String.valueOf(expected));
        }
    }

    private Collection<?> asCollection(Object value) {
        if (value == null) return java.util.List.of();
        return value instanceof Collection<?> collection ? collection : java.util.List.of(value);
    }
}
