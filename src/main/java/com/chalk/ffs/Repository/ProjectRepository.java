package com.chalk.ffs.Repository;

import com.chalk.ffs.Entity.Project;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ProjectService extends JpaRepository<Project,Long> {
    Optional<Project> getProjectById(Long projectId);
}
