package com.chalk.ffs.DTO.Organization;

import com.chalk.ffs.Entity.Organization;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.List;

public class OrganizationListDTO {

    @JsonProperty("organizations")
    private List<OrganizationDTO> organizationDTOList;

    public List<OrganizationDTO> getOrganizationDTOList() {
        return organizationDTOList;
    }

    public void setOrganizationDTOList(List<OrganizationDTO> organizationDTOList) {
        this.organizationDTOList = organizationDTOList;
    }

    public OrganizationListDTO(List<Organization> organizationList){
        this.organizationDTOList=new ArrayList<>();
        organizationList.forEach(organization->{
            this.organizationDTOList.add(new OrganizationDTO(organization));
        });
    }
}
