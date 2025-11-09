package com.chalk.ffs.Exceptions.FeatureFlag;

public class FeatureFlagNotFoundException extends RuntimeException {
    public FeatureFlagNotFoundException(String message) {
        super(message);
    }
}
