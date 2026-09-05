package com.chalk.ffs.DTO.FeatureFlag;

import com.chalk.ffs.Entity.FeatureFlagVariant;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

public class FeatureFlagVariantDTO {
    private Long id;
    @NotBlank
    private String key;
    private String value;
    @Min(0)
    @Max(100)
    private Integer percentage = 0;

    public FeatureFlagVariantDTO() {}

    public FeatureFlagVariantDTO(FeatureFlagVariant variant) {
        this.id = variant.getId();
        this.key = variant.getKey();
        this.value = variant.getValue();
        this.percentage = variant.getPercentage();
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getKey() { return key; }
    public void setKey(String key) { this.key = key; }
    public String getValue() { return value; }
    public void setValue(String value) { this.value = value; }
    public Integer getPercentage() { return percentage; }
    public void setPercentage(Integer percentage) { this.percentage = percentage; }
}
