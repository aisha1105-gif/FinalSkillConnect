package com.juw.dbms.skillConnect.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import com.juw.dbms.skillConnect.entity.Freelancer;
import com.juw.dbms.skillConnect.entity.Project;
import com.juw.dbms.skillConnect.entity.ProjectStatus;
import com.juw.dbms.skillConnect.repository.FreelancerRepository;
import com.juw.dbms.skillConnect.repository.ProjectRepository;
import com.juw.dbms.skillConnect.repository.ProjectStatusRepository;


@Validated
@Service
public class ProjectService {
    @Autowired
    ProjectRepository projectRepository;
    @Autowired
    FreelancerRepository freelancerRepository;
    @Autowired
    ProjectStatusRepository projectStatusRepository;//ProjectStatusService projectStatusService;
    public void saveProject(Project project) {
        // Ensure projectStatus is set; if not, set default "Created" status
        if (project.getProjectStatus() == null) {
            ProjectStatus defaultStatus = new ProjectStatus();
            defaultStatus.updateStatus("Created");
            projectStatusRepository.save(defaultStatus);
            project.setProjectStatus(defaultStatus);
        }
        projectRepository.save(project);
    }

    public Project getProjectById(Long projectId) {
        return projectRepository.findById(projectId).orElseThrow();
    }

    public void assignProjectToFreelancer(Long projectId, Long freelancerId) {
    Project project = projectRepository.findById(projectId).orElseThrow();
    Freelancer freelancer = freelancerRepository.findById(freelancerId).orElseThrow();

    project.setFreelancer(freelancer);

    // Update project status to "Pending"
    ProjectStatus pendingStatus = new ProjectStatus();
    pendingStatus.updateStatus("Pending");
    projectStatusRepository.save(pendingStatus);
    project.setProjectStatus(pendingStatus);

    projectRepository.save(project);
    }

    public List<Project> getProjectsByClientId(Long clientId) {
        return projectRepository.findByClientId(clientId);
    }
    
    public List<Project> getProjectsByFreelancerId(Long freelancerId) {
        return projectRepository.findByFreelancerId(freelancerId);
    }
    

}
