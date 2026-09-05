package com.chalk.ffs.DTO.FeatureFlag;

public class FeatureFlagEvaluationResponseDTO {
    private String key;
    private Long environmentId;
    private Boolean enabled;
    private Object value;
    private String variantKey;
    private String reason;
    private Integer version;

    public FeatureFlagEvaluationResponseDTO() {}

    public FeatureFlagEvaluationResponseDTO(String key, Long environmentId, boolean enabled, Object value,
                                             String variantKey, String reason, Integer version) {
        this.key = key;
        this.environmentId = environmentId;
        this.enabled = enabled;
        this.value = value;
        this.variantKey = variantKey;
        this.reason = reason;
        this.version = version;
    }

    public String getKey() { return key; }
    public Long getEnvironmentId() { return environmentId; }
    public Boolean getEnabled() { return enabled; }
    public Object getValue() { return value; }
    public String getVariantKey() { return variantKey; }
    public String getReason() { return reason; }
    public Integer getVersion() { return version; }
    public void setKey(String key) { this.key = key; }
    public void setEnvironmentId(Long environmentId) { this.environmentId = environmentId; }
    public void setEnabled(Boolean enabled) { this.enabled = enabled; }
    public void setValue(Object value) { this.value = value; }
    public void setVariantKey(String variantKey) { this.variantKey = variantKey; }
    public void setReason(String reason) { this.reason = reason; }
    public void setVersion(Integer version) { this.version = version; }
}
