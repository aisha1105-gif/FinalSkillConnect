package com.juw.dbms.skillConnect.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import com.juw.dbms.skillConnect.entity.Freelancer;
import com.juw.dbms.skillConnect.entity.FreelancerSkill;
import com.juw.dbms.skillConnect.repository.FreelancerSkillRepository;

@Validated
@Service
public class FreelancerSkillService {
    @Autowired
    FreelancerSkillRepository freelancerSkillRepository;

    @Transactional
    public void saveFreelancerSkill(FreelancerSkill freelancerSkill) {
        freelancerSkillRepository.save(freelancerSkill);
    }

    public List<Freelancer> findFreelancersBySkillCategory (Long skillCategoryId) {
        return freelancerSkillRepository.findFreelancersBySkillCategory(skillCategoryId);
    }

    public Optional<FreelancerSkill> findByFreelancerIdAndSkillCategoryId(Long freelancerId, Long skillCategoryId) {
        return freelancerSkillRepository.findByFreelancerIdAndSkillCategoryId(freelancerId, skillCategoryId);
    }

    public List<Object[]> getFreelancersAndPricesBySkillCategory(Long skillCategoryId) {
        return freelancerSkillRepository.findFreelancerAndPriceBySkillCategoryId(skillCategoryId);
    }
    
}
