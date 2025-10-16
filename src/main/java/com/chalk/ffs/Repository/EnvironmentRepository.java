package com.chalk.ffs.Repository;

import com.chalk.ffs.Entity.Environment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface EnvironmentRepository extends JpaRepository<Environment,Long> {
    Optional<Environment> getEnvironmentsByEnv(String env);
}
