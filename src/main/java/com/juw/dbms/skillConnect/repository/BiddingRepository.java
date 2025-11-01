package com.juw.dbms.skillConnect.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.juw.dbms.skillConnect.entity.Bidding;
import com.juw.dbms.skillConnect.entity.Freelancer;
import com.juw.dbms.skillConnect.entity.Project;

@Repository
public interface BiddingRepository extends JpaRepository<Bidding, Long> {
    List<Bidding> findByProject(Project project);

    List<Bidding> findByProjectId(Long projectId);

    List<Bidding> findByFreelancer(Freelancer freelancer);

    boolean existsByProjectAndFreelancer(Project project, Freelancer freelancer);
}


