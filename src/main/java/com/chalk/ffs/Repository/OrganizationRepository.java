package com.chalk.ffs.Repository;

import com.chalk.ffs.Entity.Organization;
import com.chalk.ffs.Enums.OrganizationStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface OrganizationRepository extends JpaRepository<Organization,Long> {
    List<Organization> findByStatus(OrganizationStatus status);
    Optional<Organization> getOrganizationById(Long id);
}
