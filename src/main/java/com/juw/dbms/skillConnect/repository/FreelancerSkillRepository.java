package com.juw.dbms.skillConnect.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.juw.dbms.skillConnect.entity.Freelancer;
import com.juw.dbms.skillConnect.entity.FreelancerSkill;

@Repository
public interface FreelancerSkillRepository extends JpaRepository<FreelancerSkill, Long>{
    @Query("SELECT fs.freelancer FROM FreelancerSkill fs WHERE fs.skillCategory.id = :skillCategoryId") //:skillCategoryId refers to a named variable (here, it refers to Param passed as argument)
    List<Freelancer> findFreelancersBySkillCategory(@Param("skillCategoryId") Long skillCategoryId);

    Optional<FreelancerSkill> findByFreelancerIdAndSkillCategoryId(Long freelancerId, Long skillCategoryId);

    @Query("SELECT fs.freelancer, fs.price FROM FreelancerSkill fs WHERE fs.skillCategory.id = :skillCategoryId")
    List<Object[]> findFreelancerAndPriceBySkillCategoryId(@Param("skillCategoryId") Long skillCategoryId);

    
}
