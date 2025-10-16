package com.chalk.ffs.Entity;

import com.chalk.ffs.DTO.Environment.EnvironmentDTO;
import jakarta.persistence.*;

import java.util.HashSet;
import java.util.Set;

@Entity
public class Environment {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String env;
    private String description;

    @ManyToMany
    @JoinTable(
            name="project_environment",
            joinColumns = @JoinColumn(name="environment_id"),
            inverseJoinColumns = @JoinColumn(name="project_id")
    )
    private Set<Project> projects =new HashSet<>();

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Environment(){}

    public Environment(EnvironmentDTO environmentDTO){
        this.env=environmentDTO.getEnv();
        this.description=environmentDTO.getDescription();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getEnv() {
        return env;
    }

    public void setEnv(String env) {
        this.env = env;
    }

    public Set<Project> getProjects() {
        return projects;
    }

    public void setProjects(Set<Project> projects) {
        this.projects = projects;
    }
}
