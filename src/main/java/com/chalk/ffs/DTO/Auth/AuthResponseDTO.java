package com.chalk.ffs.DTO.Auth;

public class AuthResponseDTO {
    private String token;
    private Long userId;
    private Long orgId;
    private String name;
    private String email;
    private String role;

    public AuthResponseDTO() {}

    public AuthResponseDTO(String token, Long userId, Long orgId, String name, String email, String role) {
        this.token = token;
        this.userId = userId;
        this.orgId = orgId;
        this.name = name;
        this.email = email;
        this.role = role;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getOrgId() {
        return orgId;
    }

    public void setOrgId(Long orgId) {
        this.orgId = orgId;
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

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }
}
