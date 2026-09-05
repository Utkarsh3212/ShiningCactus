package com.chalk.ffs.Service;

import com.chalk.ffs.DTO.Environment.EnvironmentDTO;
import com.chalk.ffs.DTO.Environment.EnvironmentListDTO;
import com.chalk.ffs.Entity.Environment;
import com.chalk.ffs.Entity.Organization;
import com.chalk.ffs.Exceptions.IllegalStateException;
import com.chalk.ffs.Entity.Project;
import com.chalk.ffs.Exceptions.Environment.EnvironmentNotFoundException;
import com.chalk.ffs.Exceptions.Organization.OrganizationNotFoundException;
import com.chalk.ffs.Exceptions.Project.ProjectNotFoundException;
import com.chalk.ffs.Repository.EnvironmentRepository;
import com.chalk.ffs.Repository.OrganizationRepository;
import com.chalk.ffs.Repository.ProjectRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import java.util.UUID;

@Service
public class EnvironmentService {

    private final EnvironmentRepository environmentRepository;
    private final ProjectRepository projectRepository;
    private final OrganizationRepository organizationRepository;
    private final TenantAccessService tenantAccessService;

    public EnvironmentService(EnvironmentRepository environmentRepository, ProjectRepository projectRepository,OrganizationRepository organizationRepository,
                              TenantAccessService tenantAccessService){
        this.environmentRepository=environmentRepository;
        this.projectRepository=projectRepository;
        this.organizationRepository=organizationRepository;
        this.tenantAccessService=tenantAccessService;
    }

    @Transactional(readOnly = true)
    public EnvironmentListDTO getEnvironmentsById(Long projectId){
        tenantAccessService.requireAdmin();
        Project project=tenantAccessService.requireProjectAccess(projectId);

        return new EnvironmentListDTO(project);
    }

    @Transactional(isolation = Isolation.READ_COMMITTED)
    public EnvironmentDTO addEnvironmentToProject(Long projectId, Long environmentId){
        tenantAccessService.requireAdmin();
        Environment environment=tenantAccessService.requireEnvironmentAccess(environmentId);
        Project project=tenantAccessService.requireProjectAccess(projectId);
        if(!environment.getOrganization().getId().equals(project.getOrganization().getId())){
            throw new IllegalStateException(
                    "Cannot assign as organization of the environment and project are not the same."
            );
        }
        environment.getProjects().add(project);
        project.getEnvironments().add(environment);

        environmentRepository.save(environment);

        return new EnvironmentDTO(environment);
    }

    @Transactional(isolation = Isolation.READ_COMMITTED)
    public void removeEnvironmentFromProject(Long projectId, Long environmentId) {
        tenantAccessService.requireAdmin();
        Environment environment = tenantAccessService.requireEnvironmentAccess(environmentId);
        Project project = tenantAccessService.requireProjectAccess(projectId);
        if(!project.getEnvironments().contains(environment)){
            throw new EnvironmentNotFoundException(
                    "Specified environment not found in the project."
            );
        }
        environment.getProjects().remove(project);
        project.getEnvironments().remove(environment);

        environmentRepository.save(environment);
    }

    @Transactional(isolation = Isolation.READ_COMMITTED)
    public EnvironmentDTO createEnvironment(EnvironmentDTO environmentDTO) {
        tenantAccessService.requireAdmin();
        Long orgId=environmentDTO.getOrgId();
        Organization organization=tenantAccessService.requireOrganizationAccess(orgId);
        Environment environment = new Environment(environmentDTO,organization);
        environment = environmentRepository.save(environment);
        return new EnvironmentDTO(environment);
    }

    @Transactional(isolation = Isolation.READ_COMMITTED)
    public void deleteEnvironment(Long environmentId) {
        tenantAccessService.requireAdmin();
        Environment environment = tenantAccessService.requireEnvironmentAccess(environmentId);
        Organization organization=environment.getOrganization();
        organization.getEnvironmentList().remove(environment);
        environmentRepository.delete(environment);
    }

    @Transactional(isolation = Isolation.READ_COMMITTED)
    public EnvironmentDTO rotateClientKey(Long environmentId) {
        tenantAccessService.requireAdmin();
        Environment environment = tenantAccessService.requireEnvironmentAccess(environmentId);
        environment.setClientKey(UUID.randomUUID().toString());
        return new EnvironmentDTO(environmentRepository.save(environment));
    }
}
