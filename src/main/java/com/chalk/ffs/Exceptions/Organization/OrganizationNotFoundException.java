package com.chalk.ffs.Exceptions.Organization;

public class OrganizationNotFoundException extends RuntimeException{
    public OrganizationNotFoundException(String message){
        super(message);
    }
}
