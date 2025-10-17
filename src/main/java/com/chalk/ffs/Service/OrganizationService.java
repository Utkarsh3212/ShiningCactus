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

    public OrganizationService(OrganizationRepository organizationRepository){
        this.organizationRepository=organizationRepository;
    }

    @Transactional(isolation = Isolation.READ_COMMITTED)
    public OrganizationDTO addOrganization(OrganizationDTO organizationDTO){
        Organization organization=new Organization(organizationDTO);
        organization=organizationRepository.save(organization);
        return new OrganizationDTO(organization);
    }

    public List<Organization> listAllOrgs(){
        return organizationRepository.findAll();
    }

    @Transactional(isolation = Isolation.READ_COMMITTED)
    public void deleteOrganizationById(Long orgId){
        organizationRepository.deleteById(orgId);
    }

    public OrganizationDTO getOrganizationById(Long orgId){
        Organization organization = organizationRepository.getOrganizationById(orgId)
                .orElseThrow(()-> new OrganizationNotFoundException(
                        "No organization found with id:"+orgId));

        return new OrganizationDTO(organization);
    }
}
