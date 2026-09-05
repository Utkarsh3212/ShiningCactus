package com.chalk.ffs.Entity;

import jakarta.persistence.*;

@Entity
public class FeatureFlagVariant {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "feature_flag_id", nullable = false)
    private FeatureFlag featureFlag;

    @Column(name = "variant_key", nullable = false)
    private String key;

    @Column(columnDefinition = "text")
    private String value;

    @Column(nullable = false)
    private Integer percentage = 0;

    public FeatureFlagVariant() {}

    public FeatureFlagVariant(String key, String value, Integer percentage) {
        this.key = key;
        this.value = value;
        this.percentage = percentage;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public FeatureFlag getFeatureFlag() { return featureFlag; }
    public void setFeatureFlag(FeatureFlag featureFlag) { this.featureFlag = featureFlag; }
    public String getKey() { return key; }
    public void setKey(String key) { this.key = key; }
    public String getValue() { return value; }
    public void setValue(String value) { this.value = value; }
    public Integer getPercentage() { return percentage; }
    public void setPercentage(Integer percentage) { this.percentage = percentage; }
}
