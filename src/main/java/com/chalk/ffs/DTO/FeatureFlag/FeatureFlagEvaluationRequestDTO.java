package com.chalk.ffs.DTO.FeatureFlag;

import java.util.HashMap;
import java.util.Map;

public class FeatureFlagEvaluationRequestDTO {
    private Map<String, Object> context = new HashMap<>();

    public Map<String, Object> getContext() { return context == null ? Map.of() : context; }
    public void setContext(Map<String, Object> context) { this.context = context; }
}
