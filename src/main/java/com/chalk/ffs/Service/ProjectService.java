package com.chalk.ffs.Service;

import com.chalk.ffs.DTO.Project.ProjectDTO;
import com.chalk.ffs.DTO.Project.ProjectListDTO;
import com.chalk.ffs.Entity.Organization;
import com.chalk.ffs.Entity.Project;
import com.chalk.ffs.Exceptions.Organization.OrganizationNotFoundException;
import com.chalk.ffs.Exceptions.Project.ProjectNotFoundException;
import com.chalk.ffs.Repository.OrganizationRepository;
import com.chalk.ffs.Repository.ProjectRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ProjectService {
    private final ProjectRepository projectRepository;
    private final OrganizationRepository organizationRepository;

    public ProjectService(ProjectRepository projectRepository, OrganizationRepository organizationRepository){
        this.projectRepository=projectRepository;
        this.organizationRepository=organizationRepository;
    }

    public ProjectDTO getProjectById(Long projectId){
        Project project=projectRepository.getProjectById(projectId)
                .orElseThrow(()->new ProjectNotFoundException(
                   "Project not found with the given id: "+projectId
                ));
        return new ProjectDTO(project);
    }

    public ProjectListDTO getProjectListByOrgId(Long orgId){
        Organization organization=organizationRepository.getOrganizationById(orgId)
                .orElseThrow(()->new OrganizationNotFoundException(
                        "No organization found with id:"+orgId
                ));

        return new ProjectListDTO(organization);
    }

    @Transactional(isolation = Isolation.READ_COMMITTED)
    public ProjectDTO createProjectByOrgId(Long orgId,ProjectDTO projectDTO){
        Organization organization=organizationRepository.getOrganizationById(orgId)
                .orElseThrow(()->new OrganizationNotFoundException(
                        "No organization found with id: "+orgId
                ));

        Project project=new Project(projectDTO,organization);
        project=projectRepository.save(project);
        organization.getProjectList().add(project);
        organization.setProjectCount(organization.getProjectCount()+1);
        organization=organizationRepository.save(organization);

        return new ProjectDTO(project);
    }
}
