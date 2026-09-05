package com.chalk.ffs.DTO.FeatureFlag;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BulkFeatureFlagEvaluationRequestDTO {
    private List<String> keys = new ArrayList<>();
    private Map<String, Object> context = new HashMap<>();

    public List<String> getKeys() { return keys == null ? List.of() : keys; }
    public void setKeys(List<String> keys) { this.keys = keys; }
    public Map<String, Object> getContext() { return context == null ? Map.of() : context; }
    public void setContext(Map<String, Object> context) { this.context = context; }
}
