package com.chalk.ffs.Service;

import com.chalk.ffs.Repository.EnvironmentRepository;
import org.springframework.stereotype.Service;

@Service
public class EnvironmentService {

    private final EnvironmentRepository environmentRepository;

    public EnvironmentService(EnvironmentRepository environmentRepository){
        this.environmentRepository=environmentRepository;
    }
}
