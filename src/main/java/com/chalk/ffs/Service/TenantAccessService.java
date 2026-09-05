package com.chalk.ffs.Service;

import com.chalk.ffs.Entity.*;
import com.chalk.ffs.Enums.UserRole;
import com.chalk.ffs.Exceptions.AccessDeniedException;
import com.chalk.ffs.Exceptions.Environment.EnvironmentNotFoundException;
import com.chalk.ffs.Exceptions.FeatureFlag.FeatureFlagNotFoundException;
import com.chalk.ffs.Exceptions.Organization.OrganizationNotFoundException;
import com.chalk.ffs.Exceptions.Project.ProjectNotFoundException;
import com.chalk.ffs.Exceptions.Rule.RuleNotFoundException;
import com.chalk.ffs.Exceptions.User.UserNotFoundException;
import com.chalk.ffs.Repository.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
public class TenantAccessService {
    private final UserRepository userRepository;
    private final OrganizationRepository organizationRepository;
    private final ProjectRepository projectRepository;
    private final EnvironmentRepository environmentRepository;
    private final FeatureFlagRepository featureFlagRepository;
    private final RuleRepository ruleRepository;

    public TenantAccessService(UserRepository userRepository, OrganizationRepository organizationRepository,
                               ProjectRepository projectRepository, EnvironmentRepository environmentRepository,
                               FeatureFlagRepository featureFlagRepository, RuleRepository ruleRepository) {
        this.userRepository = userRepository;
        this.organizationRepository = organizationRepository;
        this.projectRepository = projectRepository;
        this.environmentRepository = environmentRepository;
        this.featureFlagRepository = featureFlagRepository;
        this.ruleRepository = ruleRepository;
    }

    public User currentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new AccessDeniedException("Authentication is required");
        }
        return userRepository.findByEmail(authentication.getName())
                .orElseThrow(() -> new UserNotFoundException("Authenticated user was not found"));
    }

    public User requireAdmin() {
        User user = currentUser();
        if (user.getRole() != UserRole.ADMIN) {
            throw new AccessDeniedException("Administrator access is required");
        }
        return user;
    }

    public Organization requireOrganizationAccess(Long organizationId) {
        Organization organization = organizationRepository.findById(organizationId)
                .orElseThrow(() -> new OrganizationNotFoundException("No organization found with id:" + organizationId));
        if (!organization.getId().equals(currentUser().getOrganization().getId())) {
            throw new AccessDeniedException("The resource belongs to another organization");
        }
        return organization;
    }

    public Project requireProjectAccess(Long projectId) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new ProjectNotFoundException("Project not found with the given id: " + projectId));
        requireOrganizationAccess(project.getOrganization().getId());
        return project;
    }

    public Environment requireEnvironmentAccess(Long environmentId) {
        Environment environment = environmentRepository.findById(environmentId)
                .orElseThrow(() -> new EnvironmentNotFoundException("No Environment found with id:" + environmentId));
        requireOrganizationAccess(environment.getOrganization().getId());
        return environment;
    }

    public FeatureFlag requireFeatureFlagAccess(Long flagId) {
        FeatureFlag flag = featureFlagRepository.findById(flagId)
                .orElseThrow(() -> new FeatureFlagNotFoundException("Feature Flag not found with given id:" + flagId));
        requireEnvironmentAccess(flag.getEnvironment().getId());
        return flag;
    }

    public Rule requireRuleAccess(Long ruleId) {
        Rule rule = ruleRepository.findById(ruleId)
                .orElseThrow(() -> new RuleNotFoundException("No rule found with id:" + ruleId));
        requireFeatureFlagAccess(rule.getFeatureFlag().getId());
        return rule;
    }

    public User requireUserAccess(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found with the given id: " + userId));
        requireOrganizationAccess(user.getOrganization().getId());
        return user;
    }
}
