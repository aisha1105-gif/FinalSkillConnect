package com.juw.dbms.skillConnect.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import com.juw.dbms.skillConnect.entity.Bidding;
import com.juw.dbms.skillConnect.entity.Freelancer;
import com.juw.dbms.skillConnect.entity.Project;
import com.juw.dbms.skillConnect.repository.BiddingRepository;

@Validated
@Service
public class BiddingService {
    @Autowired
    BiddingRepository biddingRepository;

    public void saveBid(Bidding bid) {
        biddingRepository.save(bid);
    }

    public List<Bidding> getBidsByProjectId(Long projectId) {
        return biddingRepository.findByProjectId(projectId);
    }

    public Bidding getBidById(Long bidId) {
        return biddingRepository.findById(bidId).orElseThrow();
    }

    public boolean existsByProjectAndFreelancer(Project project, Freelancer freelancer) {
        return biddingRepository.existsByProjectAndFreelancer(project, freelancer);
    }

    public List<Bidding> getAllBidsByFreelancer(Freelancer freelancer) {
        return biddingRepository.findByFreelancer(freelancer);
    }
}
