package com.chalk.ffs.Service;

import com.chalk.ffs.DTO.Rule.RuleDTO;
import com.chalk.ffs.Entity.FeatureFlag;
import com.chalk.ffs.Entity.Rule;
import com.chalk.ffs.Exceptions.FeatureFlag.FeatureFlagNotFoundException;
import com.chalk.ffs.Exceptions.Rule.RuleNotFoundException;
import com.chalk.ffs.Repository.FeatureFlagRepository;
import com.chalk.ffs.Repository.RuleRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

@Service
public class RuleService {
    private final RuleRepository ruleRepository;
    private final FeatureFlagRepository featureFlagRepository;

    RuleService(RuleRepository ruleRepository, FeatureFlagRepository featureFlagRepository){
        this.ruleRepository=ruleRepository;
        this.featureFlagRepository = featureFlagRepository;
    }

    @Transactional(isolation = Isolation.READ_COMMITTED)
    public RuleDTO createRule(Long flagId, RuleDTO ruleDTO) {
        FeatureFlag flag=featureFlagRepository.findById(flagId)
                .orElseThrow(()->new FeatureFlagNotFoundException(
                   "No flag found with id:"+flagId
                ));

        Rule rule = new Rule(ruleDTO);
        rule.setFeatureFlag(flag);
        flag.getRules().add(rule);

        ruleRepository.save(rule);

        return new RuleDTO(rule,flag.getId());
    }

    @Transactional(isolation = Isolation.READ_COMMITTED)
    public RuleDTO updateRule(Long ruleId, RuleDTO ruleDTO) {
        Rule rule=ruleRepository.findById(ruleId)
                .orElseThrow(()->new RuleNotFoundException(
                        "No rule found with id:"+ruleId
                ));

        rule.setName(ruleDTO.getName());
        rule.setConditions(ruleDTO.getConditions());

        return new RuleDTO(rule);
    }

    public void deleteRule(Long ruleId) {
        ruleRepository.deleteById(ruleId);
    }
}
