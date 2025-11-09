package com.chalk.ffs.DTO.FeatureFlag;

import com.chalk.ffs.Entity.FeatureFlag;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.List;

public class FeatureFlagListDTO {

    @JsonProperty("featureFlags")
    private List<FeatureFlagSummaryDTO> featureFlagDTOList;

    public FeatureFlagListDTO() {}

    public FeatureFlagListDTO(List<FeatureFlag> featureFlags) {
        this.featureFlagDTOList = new ArrayList<>();
        featureFlags.forEach(flag -> this.featureFlagDTOList.add(new FeatureFlagSummaryDTO(flag)));
    }

    public List<FeatureFlagSummaryDTO> getFeatureFlagDTOList() {
        return featureFlagDTOList;
    }

    public void setFeatureFlagDTOList(List<FeatureFlagSummaryDTO> featureFlagDTOList) {
        this.featureFlagDTOList = featureFlagDTOList;
    }
}
