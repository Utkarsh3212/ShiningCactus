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
    private final TenantAccessService tenantAccessService;

    public ProjectService(ProjectRepository projectRepository, OrganizationRepository organizationRepository,
                          TenantAccessService tenantAccessService){
        this.projectRepository=projectRepository;
        this.organizationRepository=organizationRepository;
        this.tenantAccessService=tenantAccessService;
    }

    @Transactional(readOnly = true)
    public ProjectDTO getProjectById(Long projectId){
        tenantAccessService.requireAdmin();
        return new ProjectDTO(tenantAccessService.requireProjectAccess(projectId));
    }

    @Transactional(readOnly = true)
    public ProjectListDTO getProjectListByOrgId(Long orgId){
        tenantAccessService.requireAdmin();
        return new ProjectListDTO(tenantAccessService.requireOrganizationAccess(orgId));
    }

    @Transactional(isolation = Isolation.READ_COMMITTED)
    public ProjectDTO createProjectByOrgId(Long orgId,ProjectDTO projectDTO){
        tenantAccessService.requireAdmin();
        Organization organization=tenantAccessService.requireOrganizationAccess(orgId);

        Project project=new Project(projectDTO,organization);
        project=projectRepository.save(project);
        organization.getProjectList().add(project);
        organization.setProjectCount(organization.getProjectCount()+1);
        organization=organizationRepository.save(organization);

        return new ProjectDTO(project);
    }

    @Transactional(isolation = Isolation.READ_COMMITTED)
    public ProjectDTO updateProject(Long projectId, ProjectDTO projectDTO) {
        tenantAccessService.requireAdmin();
        Project project = tenantAccessService.requireProjectAccess(projectId);
        if (projectDTO.getStartDate() == null || projectDTO.getEndDate() == null || projectDTO.getStartDate().isAfter(projectDTO.getEndDate())) {
            throw new com.chalk.ffs.Exceptions.Project.InvalidDateException("Start date cannot be after end date");
        }
        project.setName(projectDTO.getName());
        project.setStartDate(projectDTO.getStartDate());
        project.setEndDate(projectDTO.getEndDate());
        project.setProjectStatus(projectDTO.getStartDate().isAfter(java.time.LocalDate.now()) ? com.chalk.ffs.Enums.ProjectStatus.PLANNED : com.chalk.ffs.Enums.ProjectStatus.IN_PROGRESS);
        return new ProjectDTO(projectRepository.save(project));
    }

    @Transactional(isolation = Isolation.READ_COMMITTED)
    public void deleteProject(Long projectId) {
        tenantAccessService.requireAdmin();
        Project project = tenantAccessService.requireProjectAccess(projectId);
        Organization organization = project.getOrganization();
        organization.getProjectList().remove(project);
        organization.setProjectCount(Math.max(0, (organization.getProjectCount() == null ? 0 : organization.getProjectCount()) - 1));
        projectRepository.delete(project);
    }
}
