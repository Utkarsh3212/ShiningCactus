package com.chalk.ffs.Repository;

import com.chalk.ffs.Entity.FeatureFlag;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FeatureFlagRepository extends JpaRepository<FeatureFlag,Long> {
    boolean existsByKeyAndEnvironment_Id(String key, Long environmentId);
}
