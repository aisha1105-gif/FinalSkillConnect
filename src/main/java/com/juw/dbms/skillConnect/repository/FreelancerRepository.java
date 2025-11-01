package com.juw.dbms.skillConnect.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.juw.dbms.skillConnect.entity.Freelancer;

@Repository
public interface FreelancerRepository extends JpaRepository<Freelancer, Long> {
    Optional<Freelancer> findByEmail(String email);
    Optional<Freelancer> findByEmailAndPassword(String email, String password);
}
