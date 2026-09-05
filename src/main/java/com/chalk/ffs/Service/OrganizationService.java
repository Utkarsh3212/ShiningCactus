package com.chalk.ffs.Service;

import com.chalk.ffs.DTO.Organization.OrganizationDTO;
import com.chalk.ffs.Entity.Organization;
import com.chalk.ffs.Exceptions.Organization.OrganizationNotFoundException;
import com.chalk.ffs.Repository.OrganizationRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class OrganizationService {
    private final OrganizationRepository organizationRepository;
    private final TenantAccessService tenantAccessService;

    public OrganizationService(OrganizationRepository organizationRepository, TenantAccessService tenantAccessService){
        this.organizationRepository=organizationRepository;
        this.tenantAccessService=tenantAccessService;
    }

    @Transactional(isolation = Isolation.READ_COMMITTED)
    public OrganizationDTO addOrganization(OrganizationDTO organizationDTO){
        Organization organization=new Organization(organizationDTO);
        organization=organizationRepository.save(organization);
        return new OrganizationDTO(organization);
    }

    public List<Organization> listAllOrgs(){
        tenantAccessService.requireAdmin();
        return List.of(tenantAccessService.requireOrganizationAccess(tenantAccessService.currentUser().getOrganization().getId()));
    }

    @Transactional(isolation = Isolation.READ_COMMITTED)
    public void deleteOrganizationById(Long orgId){
        tenantAccessService.requireAdmin();
        organizationRepository.delete(tenantAccessService.requireOrganizationAccess(orgId));
    }

    public OrganizationDTO getOrganizationById(Long orgId){
        tenantAccessService.requireAdmin();
        return new OrganizationDTO(tenantAccessService.requireOrganizationAccess(orgId));
    }
}
