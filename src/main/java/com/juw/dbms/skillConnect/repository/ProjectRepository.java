package com.juw.dbms.skillConnect.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.juw.dbms.skillConnect.entity.Project;

@Repository
public interface ProjectRepository extends JpaRepository<Project, Long>{
    public List<Project> findByClientId(Long clientId);

    List<Project> findByFreelancerId(Long freelancerId);

}
