package com.chalk.ffs.Service;

import com.chalk.ffs.DTO.Environment.EnvironmentDTO;
import com.chalk.ffs.DTO.Environment.EnvironmentListDTO;
import com.chalk.ffs.Entity.Environment;
import com.chalk.ffs.Entity.Project;
import com.chalk.ffs.Exceptions.Environment.EnvironmentNotFoundException;
import com.chalk.ffs.Exceptions.Project.ProjectNotFoundException;
import com.chalk.ffs.Repository.EnvironmentRepository;
import com.chalk.ffs.Repository.ProjectRepository;
import org.springframework.stereotype.Service;

@Service
public class EnvironmentService {

    private final EnvironmentRepository environmentRepository;
    private final ProjectRepository projectRepository;

    public EnvironmentService(EnvironmentRepository environmentRepository, ProjectRepository projectRepository){
        this.environmentRepository=environmentRepository;
        this.projectRepository=projectRepository;
    }

    public EnvironmentListDTO getEnvironmentsById(Long projectId){
        Project project=projectRepository.getProjectById(projectId)
                .orElseThrow(()->new ProjectNotFoundException(
                        "No Project found with id: "+ projectId
                ));

        return new EnvironmentListDTO(project);
    }

    public void addEnvironmentToProject(Long projectId, EnvironmentDTO environmentDTO){
        Environment environment=environmentRepository.getEnvironmentsByEnv(environmentDTO.getEnv())
                .orElse(new Environment(environmentDTO));
        Project project=projectRepository.getProjectById(projectId)
                .orElseThrow(()->new ProjectNotFoundException(
                        "No Project found with id: "+ projectId
                ));

        environment.getProjects().add(project);
        project.getEnvironments().add(environment);

        environmentRepository.save(environment);
        projectRepository.save(project);
    }

    public void removeEnvironmentFromProject(Long projectId, EnvironmentDTO environmentDTO){
        Environment environment=environmentRepository.getEnvironmentsByEnv(environmentDTO.getEnv())
                .orElseThrow(()->new EnvironmentNotFoundException(
                        "No environment named: "+environmentDTO.getEnv()
                ));
        Project project=projectRepository.getProjectById(projectId)
                .orElseThrow(()->new ProjectNotFoundException(
                        "No Project found with id: "+ projectId
                ));

        environment.getProjects().remove(project);
        project.getEnvironments().remove(environment);

        environmentRepository.save(environment);
        projectRepository.save(project);
    }
}
