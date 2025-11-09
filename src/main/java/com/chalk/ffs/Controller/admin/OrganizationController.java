package com.chalk.ffs.Controller.admin;

import com.chalk.ffs.DTO.Organization.OrganizationDTO;
import com.chalk.ffs.DTO.Organization.OrganizationListDTO;
import com.chalk.ffs.Service.OrganizationService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
public class OrganizationController {

    private final OrganizationService organizationService;

    OrganizationController(OrganizationService organizationService){
        this.organizationService=organizationService;
    }

    @PostMapping("/organizations")
    public ResponseEntity<OrganizationDTO> createOrganization(@Valid @RequestBody OrganizationDTO organizationDTO){
        OrganizationDTO created=organizationService.addOrganization(organizationDTO);
        return ResponseEntity.status(201).body(created);
    }

    @GetMapping("/organizations")
    public ResponseEntity<OrganizationListDTO> getAllOrganizations(){
        OrganizationListDTO organizationListDTO = new OrganizationListDTO(organizationService.listAllOrgs());
        return ResponseEntity.ok(organizationListDTO);
    }

    @GetMapping("/organizations/{orgId}")
    public ResponseEntity<?> getOrgById(@PathVariable Long orgId){
        OrganizationDTO organizationDTO = organizationService.getOrganizationById(orgId);
        return ResponseEntity.ok(organizationDTO);
    }

    @DeleteMapping("/organizations/{orgId}")
    public ResponseEntity<Void> deleteOrgById(@PathVariable Long orgId){
        organizationService.deleteOrganizationById(orgId);
        return ResponseEntity.noContent().build();
    }
}
