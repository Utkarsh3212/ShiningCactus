package com.chalk.ffs.Controller.admin;

import com.chalk.ffs.DTO.FeatureFlag.FeatureFlagVersionDTO;
import com.chalk.ffs.Service.FeatureFlagVersionService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class FeatureFlagVersionController {
    private final FeatureFlagVersionService versionService;

    public FeatureFlagVersionController(FeatureFlagVersionService versionService) {
        this.versionService = versionService;
    }

    @PostMapping("/flags/{flagId}/releases")
    public ResponseEntity<FeatureFlagVersionDTO> publish(@PathVariable Long flagId) {
        return ResponseEntity.status(201).body(versionService.publish(flagId));
    }

    @GetMapping("/flags/{flagId}/releases")
    public ResponseEntity<List<FeatureFlagVersionDTO>> list(@PathVariable Long flagId) {
        return ResponseEntity.ok(versionService.list(flagId));
    }

    @PostMapping("/flags/{flagId}/releases/{versionId}/rollback")
    public ResponseEntity<FeatureFlagVersionDTO> rollback(@PathVariable Long flagId, @PathVariable Long versionId) {
        return ResponseEntity.ok(versionService.rollback(flagId, versionId));
    }
}
