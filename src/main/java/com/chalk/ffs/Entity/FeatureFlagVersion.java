package com.chalk.ffs.Entity;

import jakarta.persistence.*;
import java.time.Instant;

@Entity
public class FeatureFlagVersion {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "feature_flag_id", nullable = false)
    private FeatureFlag featureFlag;

    @Column(nullable = false)
    private Integer version;

    @Column(nullable = false)
    private Instant createdAt;

    private String createdBy;

    @Column(columnDefinition = "text", nullable = false)
    private String snapshotJson;

    public FeatureFlagVersion() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public FeatureFlag getFeatureFlag() { return featureFlag; }
    public void setFeatureFlag(FeatureFlag featureFlag) { this.featureFlag = featureFlag; }
    public Integer getVersion() { return version; }
    public void setVersion(Integer version) { this.version = version; }
    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }
    public String getCreatedBy() { return createdBy; }
    public void setCreatedBy(String createdBy) { this.createdBy = createdBy; }
    public String getSnapshotJson() { return snapshotJson; }
    public void setSnapshotJson(String snapshotJson) { this.snapshotJson = snapshotJson; }
}
