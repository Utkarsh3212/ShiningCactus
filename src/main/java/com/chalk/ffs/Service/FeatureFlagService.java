package com.chalk.ffs.Service;

import com.chalk.ffs.DTO.FeatureFlag.FeatureFlagDTO;
import com.chalk.ffs.DTO.FeatureFlag.FeatureFlagListDTO;
import com.chalk.ffs.Entity.Environment;
import com.chalk.ffs.Entity.FeatureFlag;
import com.chalk.ffs.Entity.Rule;
import com.chalk.ffs.Exceptions.Environment.EnvironmentNotFoundException;
import com.chalk.ffs.Exceptions.FeatureFlag.FeatureFlagNotFoundException;
import com.chalk.ffs.Exceptions.FeatureFlag.KeyAlreadyExistsException;
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

    public FeatureFlagService(EnvironmentRepository environmentRepository, FeatureFlagRepository featureFlagRepository, RuleRepository ruleRepository){
        this.environmentRepository=environmentRepository;
        this.featureFlagRepository=featureFlagRepository;
    }

    @Transactional(isolation = Isolation.READ_COMMITTED)
    public FeatureFlagDTO createFeatureFlag(Long envId, FeatureFlagDTO flagDTO) {
        if(featureFlagRepository.existsByKey(flagDTO.getKey())){
            throw new KeyAlreadyExistsException("The key "+flagDTO.getKey()+" already exists");
        }
        FeatureFlag featureFlag=new FeatureFlag(flagDTO);
        Environment environment = environmentRepository.findById(envId)
                .orElseThrow(()->new EnvironmentNotFoundException("Environment not found with the given id:"+envId
                ));
        featureFlag.setEnvironment(environment);
        environment.getFeatureFlagSet().add(featureFlag);
        featureFlagRepository.save(featureFlag);

        return new FeatureFlagDTO(featureFlag);
    }

    @Transactional(isolation = Isolation.READ_COMMITTED)
    public FeatureFlagListDTO listFeatureFlags(Long envId) {
        Environment environment=environmentRepository.findById(envId)
                .orElseThrow(()-> new EnvironmentNotFoundException("Environment not found with the given id:"+envId
                ));
        List<FeatureFlag> featureFlagList =environment.getFeatureFlagSet()
                .stream()
                .toList();

        return new FeatureFlagListDTO(featureFlagList);
    }

    public FeatureFlagDTO getFeatureFlag(Long flagId) {
        FeatureFlag featureFlag=featureFlagRepository.findById(flagId)
                .orElseThrow(()->new EnvironmentNotFoundException("Feature Flag not found with given id:"+flagId
                ));
        return new FeatureFlagDTO(featureFlag);
    }

    @Transactional(isolation = Isolation.READ_COMMITTED)
    public FeatureFlagDTO updateFeatureFlag(Long flagId, FeatureFlagDTO flagDTO) {
        FeatureFlag featureFlag=featureFlagRepository.findById(flagId)
                .orElseThrow(()->new FeatureFlagNotFoundException("Feature Flag not found with given id:"+flagId
                ));

        Set<Rule> rules=flagDTO.getRules()
                .stream()
                .map(Rule::new)
                .collect(Collectors.toSet());

        featureFlag.getRules().clear();

        rules.forEach(rule -> rule.setFeatureFlag(featureFlag));

        featureFlag.getRules().addAll(rules);

        featureFlagRepository.save(featureFlag);
        return new FeatureFlagDTO(featureFlag);
    }

    public void deleteFeatureFlag(Long flagId) {
        featureFlagRepository.deleteById(flagId);
    }
}
