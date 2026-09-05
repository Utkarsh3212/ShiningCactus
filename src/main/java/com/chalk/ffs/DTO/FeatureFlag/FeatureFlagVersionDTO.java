package com.chalk.ffs.DTO.FeatureFlag;

import com.chalk.ffs.Entity.FeatureFlagVersion;
import java.time.Instant;

public class FeatureFlagVersionDTO {
    private Long id;
    private Long featureFlagId;
    private Integer version;
    private Instant createdAt;
    private String createdBy;

    public FeatureFlagVersionDTO() {}

    public FeatureFlagVersionDTO(FeatureFlagVersion source) {
        this.id = source.getId();
        this.featureFlagId = source.getFeatureFlag().getId();
        this.version = source.getVersion();
        this.createdAt = source.getCreatedAt();
        this.createdBy = source.getCreatedBy();
    }

    public Long getId() { return id; }
    public Long getFeatureFlagId() { return featureFlagId; }
    public Integer getVersion() { return version; }
    public Instant getCreatedAt() { return createdAt; }
    public String getCreatedBy() { return createdBy; }
    public void setId(Long id) { this.id = id; }
    public void setFeatureFlagId(Long featureFlagId) { this.featureFlagId = featureFlagId; }
    public void setVersion(Integer version) { this.version = version; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }
    public void setCreatedBy(String createdBy) { this.createdBy = createdBy; }
}
