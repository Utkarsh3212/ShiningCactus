package com.chalk.ffs.Exceptions.FeatureFlag;

public class KeyAlreadyExistsException extends RuntimeException {
    public KeyAlreadyExistsException(String message) {
        super(message);
    }
}
