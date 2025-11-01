package com.juw.dbms.skillConnect.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import com.juw.dbms.skillConnect.entity.Freelancer;
import com.juw.dbms.skillConnect.repository.FreelancerRepository;

@Validated
@Service
public class FreelancerService {
    @Autowired
    private FreelancerRepository freelancerRepository;

    public Optional<Freelancer> findById(Freelancer freelancer) {
        return freelancerRepository.findById(freelancer.getId());
    }

    public Freelancer getFreelancerById(Long id) {
        return freelancerRepository.findById(id).orElseThrow();
    }


    public Optional<Freelancer> saveFreelancer(Freelancer freelancer) {
        Optional<Freelancer> existingFreelancer = freelancerRepository.findByEmail(freelancer.getEmail());
        if (existingFreelancer.isPresent()) {
            return Optional.empty(); 
        } else {
            Freelancer savedFreelancer = freelancerRepository.save(freelancer);
            return Optional.of(savedFreelancer);
        }
    }

    public Optional<Freelancer> authenticateFreelancer(String email, String password) {
        return freelancerRepository.findByEmailAndPassword(email, password);
    }

    public Optional<String> updateFreelancer(Freelancer freelancer, Freelancer updatedFreelancerRecord) {
        Optional<Freelancer> freelancerRecord = freelancerRepository.findById(freelancer.getId());

        if (freelancerRecord.isPresent()) {
            Freelancer updatedFreelancer = (Freelancer) freelancerRecord.get();

            updatedFreelancer.setFirst_name(updatedFreelancerRecord.getFirst_name());
            updatedFreelancer.setLast_name(updatedFreelancerRecord.getLast_name());
            updatedFreelancer.setEmail(updatedFreelancerRecord.getEmail());

            if (updatedFreelancerRecord.getPassword() != null && !updatedFreelancerRecord.getPassword().isBlank()) {
                updatedFreelancer.setPassword(updatedFreelancerRecord.getPassword());
            }

            freelancerRepository.save(updatedFreelancer);
            return Optional.empty();
        }
        else {
            return Optional.of("Some error occured.");
        }
    }

    public Optional<String> deleteFreelancer(Freelancer freelancerToDelete) {
        Optional<Freelancer> freelancerToDeleteRecord = freelancerRepository.findById(freelancerToDelete.getId());

        if (freelancerToDeleteRecord.isPresent()) {
            freelancerRepository.delete(freelancerToDelete);
            return Optional.empty();
        } 
        else {
            return Optional.of("Something went wrong");
        }
    }

    public long countAllFreelancers() {
        return freelancerRepository.count();
}
}
