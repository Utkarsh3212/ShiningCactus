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
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

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

    @Transactional(isolation = Isolation.READ_COMMITTED)
    public EnvironmentDTO addEnvironmentToProject(Long projectId, Long environmentId){
        Environment environment=environmentRepository.findById(environmentId)
                .orElseThrow(()->new EnvironmentNotFoundException(
                        "No Environment found with id:" + environmentId
                ));
        Project project=projectRepository.getProjectById(projectId)
                .orElseThrow(()->new ProjectNotFoundException(
                        "No Project found with id: "+ projectId
                ));

        environment.getProjects().add(project);
        project.getEnvironments().add(environment);

        environmentRepository.save(environment);

        return new EnvironmentDTO(environment);
    }

    @Transactional(isolation = Isolation.READ_COMMITTED)
    public void removeEnvironmentFromProject(Long projectId, Long environmentId) {
        Environment environment = environmentRepository.findById(environmentId)
                .orElseThrow(() -> new EnvironmentNotFoundException(
                        "No Environment found with id:" + environmentId
                ));

        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new ProjectNotFoundException(
                        "No Project found with id:" + projectId
                ));

        environment.getProjects().remove(project);
        project.getEnvironments().remove(environment);

        environmentRepository.save(environment);
    }

    public EnvironmentDTO createEnvironment(EnvironmentDTO environmentDTO) {
        Environment environment = new Environment(environmentDTO);
        environment = environmentRepository.save(environment);
        return new EnvironmentDTO(environment);
    }

    public void deleteEnvironment(Long environmentId) {
        Environment environment = environmentRepository.findById(environmentId)
                .orElseThrow(() -> new EnvironmentNotFoundException(
                        "No Environment found with id:" + environmentId
                ));
        environmentRepository.delete(environment);
    }
}
