package com.chalk.ffs.DTO.User;

import com.chalk.ffs.Entity.Organization;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class UserListDTO {
    private String organizationName;
    @JsonProperty("users")
    private List<UserDTO> userDTOList;

    public UserListDTO() {}

    public UserListDTO(Organization organization) {
        this.organizationName = organization.getName();
        this.userDTOList = organization.getUserList().stream().map(UserDTO::new).toList();
    }

    public String getOrganizationName() {
        return organizationName;
    }

    public void setOrganizationName(String organizationName) {
        this.organizationName = organizationName;
    }

    public List<UserDTO> getUserDTOList() {
        return userDTOList;
    }

    public void setUserDTOList(List<UserDTO> userDTOList) {
        this.userDTOList = userDTOList;
    }
}
