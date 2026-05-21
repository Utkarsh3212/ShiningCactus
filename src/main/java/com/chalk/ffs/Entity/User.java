package com.chalk.ffs.Entity;

import com.chalk.ffs.DTO.User.UserDTO;
import com.chalk.ffs.Enums.UserRole;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Column;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;

@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "organization_id")
    private Organization organization;

    private String name;
    @Column(nullable = false, unique = true)
    private String email;
    private String password;
    @Enumerated(EnumType.STRING)
    private UserRole role;

    public User() {}

    public User(UserDTO userDTO, Organization organization) {
        this.id = userDTO.getId();
        this.organization = organization;
        this.name = userDTO.getName();
        this.email = userDTO.getEmail();
        this.role = userDTO.getRole() == null ? UserRole.USER : UserRole.valueOf(userDTO.getRole().toUpperCase());
    }

    public void updateFromDTO(UserDTO userDTO) {
        if (userDTO.getName() != null) {
            this.name = userDTO.getName();
        }

        if (userDTO.getEmail() != null) {
            this.email = userDTO.getEmail();
        }

        if (userDTO.getRole() != null) {
            this.role = UserRole.valueOf(userDTO.getRole().toUpperCase());
        }
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Organization getOrganization() {
        return organization;
    }

    public void setOrganization(Organization organization) {
        this.organization = organization;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public UserRole getRole() {
        return role;
    }

    public void setRole(UserRole role) {
        this.role = role;
    }
}
