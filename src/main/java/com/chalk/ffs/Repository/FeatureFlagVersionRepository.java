package com.chalk.ffs.Repository;

import com.chalk.ffs.Entity.FeatureFlagVersion;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface FeatureFlagVersionRepository extends JpaRepository<FeatureFlagVersion, Long> {
    List<FeatureFlagVersion> findByFeatureFlagIdOrderByVersionDesc(Long featureFlagId);
    Optional<FeatureFlagVersion> findByIdAndFeatureFlagId(Long id, Long featureFlagId);
    Optional<FeatureFlagVersion> findTopByFeatureFlagIdOrderByVersionDesc(Long featureFlagId);
}
