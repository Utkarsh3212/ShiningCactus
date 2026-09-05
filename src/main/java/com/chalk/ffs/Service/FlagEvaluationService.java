package com.chalk.ffs.Service;

import com.chalk.ffs.DTO.FeatureFlag.*;
import com.chalk.ffs.Entity.Environment;
import com.chalk.ffs.Entity.FeatureFlag;
import com.chalk.ffs.Entity.FeatureFlagVariant;
import com.chalk.ffs.Entity.Rule;
import com.chalk.ffs.Exceptions.Environment.EnvironmentNotFoundException;
import com.chalk.ffs.Exceptions.FeatureFlag.FeatureFlagNotFoundException;
import com.chalk.ffs.Repository.EnvironmentRepository;
import com.chalk.ffs.Repository.FeatureFlagVersionRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.LinkedHashMap;
import java.util.Map;

@Service
public class FlagEvaluationService {
    private final EnvironmentRepository environmentRepository;
    private final ObjectMapper objectMapper;
    private final FeatureFlagVersionRepository versionRepository;
    private final ConditionEvaluator conditionEvaluator = new ConditionEvaluator();

    public FlagEvaluationService(EnvironmentRepository environmentRepository, ObjectMapper objectMapper,
                                 FeatureFlagVersionRepository versionRepository) {
        this.environmentRepository = environmentRepository;
        this.objectMapper = objectMapper;
        this.versionRepository = versionRepository;
    }

    @Transactional(readOnly = true)
    public FeatureFlagEvaluationResponseDTO evaluate(Long environmentId, String key, String clientKey,
                                                     Map<String, Object> context) {
        Environment environment = environmentRepository.findById(environmentId)
                .orElseThrow(() -> new EnvironmentNotFoundException("Environment not found with the given id:" + environmentId));
        if (environment.getClientKey() == null || !environment.getClientKey().equals(clientKey)) {
            throw new com.chalk.ffs.Exceptions.AccessDeniedException("Invalid environment client key");
        }
        FeatureFlag flag = environment.getFeatureFlagSet().stream()
                .filter(candidate -> candidate.getKey().equals(key))
                .findFirst()
                .orElseThrow(() -> new FeatureFlagNotFoundException("Feature Flag not found with key: " + key));
        return evaluate(flag, context == null ? Map.of() : context);
    }

    @Transactional(readOnly = true)
    public Map<String, FeatureFlagEvaluationResponseDTO> evaluateAll(Long environmentId, String clientKey,
                                                                       BulkFeatureFlagEvaluationRequestDTO request) {
        Map<String, FeatureFlagEvaluationResponseDTO> results = new LinkedHashMap<>();
        for (String key : request.getKeys()) {
            results.put(key, evaluate(environmentId, key, clientKey, request.getContext()));
        }
        return results;
    }

    private FeatureFlagEvaluationResponseDTO evaluate(FeatureFlag flag, Map<String, Object> context) {
        if (!Boolean.TRUE.equals(flag.getEnabled())) {
            return response(flag, false, typedValue(flag.getDefaultValue(), flag.getType()), null, "DISABLED");
        }

        for (Rule rule : flag.getRules()) {
            if (rule.getConditions() != null && conditionEvaluator.matches(rule.getConditions(), context)) {
                FeatureFlagVariant variant = findVariant(flag, rule.getVariantKey());
                if (variant != null) {
                    return response(flag, true, typedValue(variant.getValue(), flag.getType()), variant.getKey(), "TARGETING_MATCH");
                }
                return response(flag, true, typedValue(flag.getDefaultValue(), flag.getType()), null, "TARGETING_MATCH");
            }
        }

        Integer rollout = flag.getRolloutPercentage() == null ? 100 : flag.getRolloutPercentage();
        if (rollout < 100 && bucket(flag, context) >= rollout) {
            return response(flag, false, typedValue(flag.getDefaultValue(), flag.getType()), null, "ROLLOUT_EXCLUDED");
        }

        FeatureFlagVariant variant = chooseVariant(flag, context, rollout);
        if (variant != null) {
            return response(flag, true, typedValue(variant.getValue(), flag.getType()), variant.getKey(), "VARIANT_ROLLOUT");
        }
        return response(flag, true, typedValue(flag.getDefaultValue(), flag.getType()), null, "DEFAULT");
    }

    private FeatureFlagEvaluationResponseDTO response(FeatureFlag flag, boolean enabled, Object value,
                                                      String variantKey, String reason) {
        return new FeatureFlagEvaluationResponseDTO(flag.getKey(), flag.getEnvironment().getId(), enabled,
                value, variantKey, reason, versionRepository.findTopByFeatureFlagIdOrderByVersionDesc(flag.getId())
                        .map(version -> version.getVersion()).orElse(null));
    }

    private FeatureFlagVariant findVariant(FeatureFlag flag, String key) {
        if (key == null) return null;
        return flag.getVariants().stream().filter(variant -> key.equals(variant.getKey())).findFirst().orElse(null);
    }

    private FeatureFlagVariant chooseVariant(FeatureFlag flag, Map<String, Object> context, int rollout) {
        if (flag.getVariants() == null || flag.getVariants().isEmpty()) return null;
        int point = bucket(flag, context);
        if (rollout > 0 && rollout < 100) point = (point * 100) / rollout;
        int cumulative = 0;
        for (FeatureFlagVariant variant : flag.getVariants()) {
            cumulative += variant.getPercentage() == null ? 0 : variant.getPercentage();
            if (point < cumulative) return variant;
        }
        return null;
    }

    private int bucket(FeatureFlag flag, Map<String, Object> context) {
        Object identifier = context.get("userId");
        if (identifier == null) identifier = context.get("id");
        if (identifier == null) identifier = context.get("key");
        if (identifier == null) return 0;
        try {
            byte[] digest = MessageDigest.getInstance("SHA-256")
                    .digest((flag.getKey() + ":" + identifier).getBytes(StandardCharsets.UTF_8));
            long value = 0;
            for (int i = 0; i < 4; i++) value = (value << 8) | (digest[i] & 0xffL);
            return (int) (value % 100);
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("SHA-256 is unavailable", e);
        }
    }

    private Object typedValue(String value, FeatureFlag.FlagType type) {
        if (value == null || type == null || type == FeatureFlag.FlagType.STRING) return value;
        try {
            return switch (type) {
                case BOOLEAN -> Boolean.parseBoolean(value);
                case NUMBER -> new BigDecimal(value);
                case JSON -> objectMapper.readValue(value, Object.class);
                case STRING -> value;
            };
        } catch (JsonProcessingException | NumberFormatException e) {
            return value;
        }
    }
}
