package com.chalk.ffs.Controller.client;

import com.chalk.ffs.DTO.FeatureFlag.BulkFeatureFlagEvaluationRequestDTO;
import com.chalk.ffs.DTO.FeatureFlag.FeatureFlagEvaluationRequestDTO;
import com.chalk.ffs.DTO.FeatureFlag.FeatureFlagEvaluationResponseDTO;
import com.chalk.ffs.Service.FlagEvaluationService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/client")
public class ClientEvaluationController {
    private final FlagEvaluationService evaluationService;

    public ClientEvaluationController(FlagEvaluationService evaluationService) {
        this.evaluationService = evaluationService;
    }

    @PostMapping("/environments/{environmentId}/flags/{key}/evaluate")
    public ResponseEntity<FeatureFlagEvaluationResponseDTO> evaluate(
            @PathVariable Long environmentId, @PathVariable String key,
            @RequestHeader("X-Environment-Key") String clientKey,
            @Valid @RequestBody(required = false) FeatureFlagEvaluationRequestDTO request) {
        Map<String, Object> context = request == null ? Map.of() : request.getContext();
        return ResponseEntity.ok(evaluationService.evaluate(environmentId, key, clientKey, context));
    }

    @PostMapping("/environments/{environmentId}/flags/evaluate")
    public ResponseEntity<Map<String, FeatureFlagEvaluationResponseDTO>> evaluateAll(
            @PathVariable Long environmentId,
            @RequestHeader("X-Environment-Key") String clientKey,
            @Valid @RequestBody BulkFeatureFlagEvaluationRequestDTO request) {
        return ResponseEntity.ok(evaluationService.evaluateAll(environmentId, clientKey, request));
    }
}
