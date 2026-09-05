package com.chalk.ffs.Service;

import com.chalk.ffs.DTO.Rule.RuleDTO;
import com.chalk.ffs.Entity.FeatureFlag;
import com.chalk.ffs.Entity.Rule;
import com.chalk.ffs.Exceptions.FeatureFlag.FeatureFlagNotFoundException;
import com.chalk.ffs.Exceptions.Rule.RuleNotFoundException;
import com.chalk.ffs.Exceptions.InvalidConfigurationException;
import com.chalk.ffs.Repository.FeatureFlagRepository;
import com.chalk.ffs.Repository.RuleRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

@Service
public class RuleService {
    private final RuleRepository ruleRepository;
    private final FeatureFlagRepository featureFlagRepository;
    private final TenantAccessService tenantAccessService;

    RuleService(RuleRepository ruleRepository, FeatureFlagRepository featureFlagRepository,
                TenantAccessService tenantAccessService){
        this.ruleRepository=ruleRepository;
        this.featureFlagRepository = featureFlagRepository;
        this.tenantAccessService = tenantAccessService;
    }

    @Transactional(isolation = Isolation.READ_COMMITTED)
    public RuleDTO createRule(Long flagId, RuleDTO ruleDTO) {
        tenantAccessService.requireAdmin();
        FeatureFlag flag=tenantAccessService.requireFeatureFlagAccess(flagId);
        validateVariant(flag, ruleDTO.getVariantKey());

        Rule rule = new Rule(ruleDTO);
        rule.setVariantKey(ruleDTO.getVariantKey());
        rule.setFeatureFlag(flag);
        flag.getRules().add(rule);

        ruleRepository.save(rule);

        return new RuleDTO(rule,flag.getId());
    }

    @Transactional(isolation = Isolation.READ_COMMITTED)
    public RuleDTO updateRule(Long ruleId, RuleDTO ruleDTO) {
        tenantAccessService.requireAdmin();
        Rule rule=tenantAccessService.requireRuleAccess(ruleId);
        validateVariant(rule.getFeatureFlag(), ruleDTO.getVariantKey());

        rule.setName(ruleDTO.getName());
        rule.setConditions(ruleDTO.getConditions());
        rule.setVariantKey(ruleDTO.getVariantKey());
        ruleRepository.save(rule);

        return new RuleDTO(rule);
    }

    public void deleteRule(Long ruleId) {
        tenantAccessService.requireAdmin();
        ruleRepository.delete(tenantAccessService.requireRuleAccess(ruleId));
    }

    private void validateVariant(FeatureFlag flag, String variantKey) {
        if (variantKey != null && flag.getVariants().stream().noneMatch(variant -> variantKey.equals(variant.getKey()))) {
            throw new InvalidConfigurationException("Rule variantKey must reference a configured variant");
        }
    }
}
