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

@Service
public class EnvironmentService {

    private final EnvironmentRepository environmentRepository;
    private final ProjectRepository projectRepository;
    private final OrganizationRepository organizationRepository;

    public EnvironmentService(EnvironmentRepository environmentRepository, ProjectRepository projectRepository,OrganizationRepository organizationRepository){
        this.environmentRepository=environmentRepository;
        this.projectRepository=projectRepository;
        this.organizationRepository=organizationRepository;
    }

    public EnvironmentListDTO getEnvironmentsById(Long projectId){
        Project project=projectRepository.getProjectById(projectId)
                .orElseThrow(()->new ProjectNotFoundException(
                        "No Project found with id: "+ projectId
                ));

        return new EnvironmentListDTO(project);
    }

    @Transactional(isolation = Isolation.READ_COMMITTED)
    public EnvironmentDTO addEnvironmentToProject(Long projectId, Long environmentId){
        Environment environment=environmentRepository.findById(environmentId)
                .orElseThrow(()->new EnvironmentNotFoundException(
                        "No Environment found with id:" + environmentId
                ));
        Project project=projectRepository.getProjectById(projectId)
                .orElseThrow(()->new ProjectNotFoundException(
                        "No Project found with id: "+ projectId
                ));
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
        Environment environment = environmentRepository.findById(environmentId)
                .orElseThrow(() -> new EnvironmentNotFoundException(
                        "No Environment found with id:" + environmentId
                ));

        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new ProjectNotFoundException(
                        "No Project found with id:" + projectId
                ));
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
        Long orgId=environmentDTO.getOrgId();
        Organization organization=organizationRepository.getOrganizationById(orgId)
                .orElseThrow(()-> new OrganizationNotFoundException(
                        "No organization found with id:"+orgId
                ));
        Environment environment = new Environment(environmentDTO,organization);
        environment = environmentRepository.save(environment);
        return new EnvironmentDTO(environment);
    }

    @Transactional(isolation = Isolation.READ_COMMITTED)
    public void deleteEnvironment(Long environmentId) {
        Environment environment = environmentRepository.findById(environmentId)
                .orElseThrow(() -> new EnvironmentNotFoundException(
                        "No Environment found with id:" + environmentId
                ));
        Organization organization=environment.getOrganization();
        organization.getEnvironmentList().remove(environment);
        environmentRepository.delete(environment);
    }
}
