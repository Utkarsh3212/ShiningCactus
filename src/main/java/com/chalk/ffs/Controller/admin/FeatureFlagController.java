package com.chalk.ffs.Controller.admin;

import com.chalk.ffs.DTO.FeatureFlag.FeatureFlagDTO;
import com.chalk.ffs.DTO.FeatureFlag.FeatureFlagListDTO;
import com.chalk.ffs.Service.FeatureFlagService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
public class FeatureFlagController {

    private final FeatureFlagService featureFlagService;

    public FeatureFlagController(FeatureFlagService featureFlagService) {
        this.featureFlagService = featureFlagService;
    }

    @PostMapping("/environments/{envId}/flags")
    public ResponseEntity<FeatureFlagDTO> createFeatureFlag(
            @PathVariable Long envId,
            @RequestBody FeatureFlagDTO flagDTO
    ) {
        FeatureFlagDTO createdFlag = featureFlagService.createFeatureFlag(envId, flagDTO);
        return ResponseEntity.status(201).body(createdFlag);
    }

    @GetMapping("/environments/{envId}/flags")
    public ResponseEntity<FeatureFlagListDTO> listFeatureFlags(@PathVariable Long envId) {
        FeatureFlagListDTO flags = featureFlagService.listFeatureFlags(envId);
        return ResponseEntity.ok(flags);
    }

    @GetMapping("/flags/{flagId}")
    public ResponseEntity<FeatureFlagDTO> getFeatureFlag(@PathVariable Long flagId) {
        FeatureFlagDTO flag = featureFlagService.getFeatureFlag(flagId);
        return ResponseEntity.ok(flag);
    }

    @PutMapping("/flags/{flagId}")
    public ResponseEntity<FeatureFlagDTO> updateFeatureFlag(
            @PathVariable Long flagId,
            @RequestBody FeatureFlagDTO flagDTO
    ) {
        FeatureFlagDTO updatedFlag = featureFlagService.updateFeatureFlag(flagId, flagDTO);
        return ResponseEntity.ok(updatedFlag);
    }

    @DeleteMapping("/flags/{flagId}")
    public ResponseEntity<Void> deleteFeatureFlag(@PathVariable Long flagId) {
        featureFlagService.deleteFeatureFlag(flagId);
        return ResponseEntity.noContent().build();
    }
}
