package com.chalk.ffs.Service;

import com.chalk.ffs.DTO.FeatureFlag.FeatureFlagDTO;
import com.chalk.ffs.DTO.FeatureFlag.FeatureFlagListDTO;
import com.chalk.ffs.Entity.Environment;
import com.chalk.ffs.Entity.FeatureFlag;
import com.chalk.ffs.Entity.Rule;
import com.chalk.ffs.Exceptions.Environment.EnvironmentNotFoundException;
import com.chalk.ffs.Exceptions.FeatureFlag.FeatureFlagNotFoundException;
import com.chalk.ffs.Exceptions.FeatureFlag.KeyAlreadyExistsException;
import com.chalk.ffs.Exceptions.InvalidConfigurationException;
import com.chalk.ffs.Repository.EnvironmentRepository;
import com.chalk.ffs.Repository.FeatureFlagRepository;
import com.chalk.ffs.Repository.RuleRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class FeatureFlagService {

    private final EnvironmentRepository environmentRepository;
    private final FeatureFlagRepository featureFlagRepository;

    private final TenantAccessService tenantAccessService;

    public FeatureFlagService(EnvironmentRepository environmentRepository, FeatureFlagRepository featureFlagRepository,
                              RuleRepository ruleRepository, TenantAccessService tenantAccessService){
        this.environmentRepository=environmentRepository;
        this.featureFlagRepository=featureFlagRepository;
        this.tenantAccessService=tenantAccessService;
    }

    @Transactional(isolation = Isolation.READ_COMMITTED)
    public FeatureFlagDTO createFeatureFlag(Long envId, FeatureFlagDTO flagDTO) {
        tenantAccessService.requireAdmin();
        Environment environment = tenantAccessService.requireEnvironmentAccess(envId);
        if(featureFlagRepository.existsByKeyAndEnvironment_Id(flagDTO.getKey(), envId)){
            throw new KeyAlreadyExistsException("The key "+flagDTO.getKey()+" already exists in this environment");
        }
        validateVariants(flagDTO);
        FeatureFlag featureFlag=new FeatureFlag(flagDTO);
        featureFlag.setEnvironment(environment);
        replaceVariants(featureFlag, flagDTO);
        environment.getFeatureFlagSet().add(featureFlag);
        featureFlagRepository.save(featureFlag);

        return new FeatureFlagDTO(featureFlag);
    }

    @Transactional(isolation = Isolation.READ_COMMITTED)
    public FeatureFlagListDTO listFeatureFlags(Long envId) {
        tenantAccessService.requireAdmin();
        Environment environment=tenantAccessService.requireEnvironmentAccess(envId);
        List<FeatureFlag> featureFlagList =environment.getFeatureFlagSet()
                .stream()
                .toList();

        return new FeatureFlagListDTO(featureFlagList);
    }

    @Transactional(readOnly = true)
    public FeatureFlagDTO getFeatureFlag(Long flagId) {
        tenantAccessService.requireAdmin();
        FeatureFlag featureFlag=tenantAccessService.requireFeatureFlagAccess(flagId);
        return new FeatureFlagDTO(featureFlag);
    }

    @Transactional(isolation = Isolation.READ_COMMITTED)
    public FeatureFlagDTO updateFeatureFlagDTO(Long flagId, FeatureFlagDTO flagDTO) {
        tenantAccessService.requireAdmin();
        FeatureFlag featureFlag=tenantAccessService.requireFeatureFlagAccess(flagId);
        validateVariants(flagDTO);
        featureFlag.updateFromDTO(flagDTO);
        if (flagDTO.getRules() != null) {
            Set<Rule> rules=flagDTO.getRules().stream().map(Rule::new).collect(Collectors.toSet());
            featureFlag.getRules().clear();
            rules.forEach(rule -> {
                rule.setFeatureFlag(featureFlag);
            });
            featureFlag.getRules().addAll(rules);
        }
        if (flagDTO.getVariants() != null) replaceVariants(featureFlag, flagDTO);

        featureFlagRepository.save(featureFlag);
        return new FeatureFlagDTO(featureFlag);
    }

    public void deleteFeatureFlag(Long flagId) {
        tenantAccessService.requireAdmin();
        featureFlagRepository.delete(tenantAccessService.requireFeatureFlagAccess(flagId));
    }

    private void replaceVariants(FeatureFlag featureFlag, FeatureFlagDTO flagDTO) {
        featureFlag.getVariants().clear();
        if (flagDTO.getVariants() == null) return;
        flagDTO.getVariants().forEach(variantDTO -> {
            com.chalk.ffs.Entity.FeatureFlagVariant variant = new com.chalk.ffs.Entity.FeatureFlagVariant(
                    variantDTO.getKey(), variantDTO.getValue(), variantDTO.getPercentage());
            variant.setFeatureFlag(featureFlag);
            featureFlag.getVariants().add(variant);
        });
    }

    private void validateVariants(FeatureFlagDTO flagDTO) {
        if (flagDTO.getRolloutPercentage() != null && (flagDTO.getRolloutPercentage() < 0 || flagDTO.getRolloutPercentage() > 100)) {
            throw new InvalidConfigurationException("Rollout percentage must be between 0 and 100");
        }
        if (flagDTO.getVariants() == null) return;
        java.util.Set<String> variantKeys = flagDTO.getVariants().stream()
                .map(com.chalk.ffs.DTO.FeatureFlag.FeatureFlagVariantDTO::getKey).collect(Collectors.toSet());
        long total = flagDTO.getVariants().stream().mapToLong(variant -> variant.getPercentage() == null ? 0 : variant.getPercentage()).sum();
        if (variantKeys.size() != flagDTO.getVariants().size()) throw new InvalidConfigurationException("Variant keys must be unique");
        if (total > 100) throw new InvalidConfigurationException("Variant percentages cannot exceed 100");
        if (flagDTO.getRules() != null && flagDTO.getRules().stream().anyMatch(rule -> rule.getVariantKey() != null && !variantKeys.contains(rule.getVariantKey()))) {
            throw new InvalidConfigurationException("Every rule variantKey must reference a configured variant");
        }
    }
}
