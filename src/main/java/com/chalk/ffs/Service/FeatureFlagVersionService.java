package com.chalk.ffs.Service;

import com.chalk.ffs.DTO.FeatureFlag.FeatureFlagDTO;
import com.chalk.ffs.DTO.FeatureFlag.FeatureFlagVariantDTO;
import com.chalk.ffs.DTO.FeatureFlag.FeatureFlagVersionDTO;
import com.chalk.ffs.Entity.FeatureFlag;
import com.chalk.ffs.Entity.FeatureFlagVariant;
import com.chalk.ffs.Entity.FeatureFlagVersion;
import com.chalk.ffs.Entity.Rule;
import com.chalk.ffs.Exceptions.FeatureFlag.FeatureFlagNotFoundException;
import com.chalk.ffs.Repository.FeatureFlagRepository;
import com.chalk.ffs.Repository.FeatureFlagVersionRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;

@Service
public class FeatureFlagVersionService {
    private final FeatureFlagRepository featureFlagRepository;
    private final FeatureFlagVersionRepository versionRepository;
    private final ObjectMapper objectMapper;
    private final TenantAccessService tenantAccessService;

    public FeatureFlagVersionService(FeatureFlagRepository featureFlagRepository,
                                      FeatureFlagVersionRepository versionRepository,
                                      ObjectMapper objectMapper, TenantAccessService tenantAccessService) {
        this.featureFlagRepository = featureFlagRepository;
        this.versionRepository = versionRepository;
        this.objectMapper = objectMapper;
        this.tenantAccessService = tenantAccessService;
    }

    @Transactional
    public FeatureFlagVersionDTO publish(Long flagId) {
        tenantAccessService.requireAdmin();
        FeatureFlag flag = tenantAccessService.requireFeatureFlagAccess(flagId);
        try {
            FeatureFlagVersion version = new FeatureFlagVersion();
            version.setFeatureFlag(flag);
            version.setVersion(versionRepository.findTopByFeatureFlagIdOrderByVersionDesc(flagId)
                    .map(existing -> existing.getVersion() + 1).orElse(1));
            version.setCreatedAt(Instant.now());
            version.setCreatedBy(tenantAccessService.currentUser().getEmail());
            version.setSnapshotJson(objectMapper.writeValueAsString(new FeatureFlagDTO(flag)));
            return new FeatureFlagVersionDTO(versionRepository.save(version));
        } catch (JsonProcessingException e) {
            throw new IllegalStateException("Unable to create feature flag release snapshot", e);
        }
    }

    @Transactional(readOnly = true)
    public List<FeatureFlagVersionDTO> list(Long flagId) {
        tenantAccessService.requireAdmin();
        tenantAccessService.requireFeatureFlagAccess(flagId);
        return versionRepository.findByFeatureFlagIdOrderByVersionDesc(flagId).stream()
                .map(FeatureFlagVersionDTO::new).toList();
    }

    @Transactional
    public FeatureFlagVersionDTO rollback(Long flagId, Long versionId) {
        tenantAccessService.requireAdmin();
        FeatureFlag flag = tenantAccessService.requireFeatureFlagAccess(flagId);
        FeatureFlagVersion version = versionRepository.findByIdAndFeatureFlagId(versionId, flagId)
                .orElseThrow(() -> new FeatureFlagNotFoundException("Release version not found with id: " + versionId));
        try {
            applySnapshot(flag, objectMapper.readValue(version.getSnapshotJson(), FeatureFlagDTO.class));
            featureFlagRepository.save(flag);
            return publish(flagId);
        } catch (JsonProcessingException e) {
            throw new IllegalStateException("Unable to restore feature flag release snapshot", e);
        }
    }

    private void applySnapshot(FeatureFlag flag, FeatureFlagDTO snapshot) {
        flag.setName(snapshot.getName());
        flag.setDescription(snapshot.getDescription());
        if (snapshot.getType() != null) flag.setType(FeatureFlag.FlagType.valueOf(snapshot.getType().toUpperCase()));
        flag.setDefaultValue(snapshot.getDefaultValue());
        flag.setEnabled(Boolean.TRUE.equals(snapshot.getEnabled()));
        flag.setRolloutPercentage(snapshot.getRolloutPercentage() == null ? 100 : snapshot.getRolloutPercentage());

        flag.getRules().clear();
        if (snapshot.getRules() != null) {
            snapshot.getRules().forEach(ruleDTO -> {
                Rule rule = new Rule();
                rule.setName(ruleDTO.getName());
                rule.setConditions(ruleDTO.getConditions());
                rule.setVariantKey(ruleDTO.getVariantKey());
                rule.setFeatureFlag(flag);
                flag.getRules().add(rule);
            });
        }

        flag.getVariants().clear();
        if (snapshot.getVariants() != null) {
            for (FeatureFlagVariantDTO variantDTO : snapshot.getVariants()) {
                FeatureFlagVariant variant = new FeatureFlagVariant(variantDTO.getKey(), variantDTO.getValue(), variantDTO.getPercentage());
                variant.setFeatureFlag(flag);
                flag.getVariants().add(variant);
            }
        }
    }
}
