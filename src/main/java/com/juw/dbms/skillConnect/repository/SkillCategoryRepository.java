package com.juw.dbms.skillConnect.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.juw.dbms.skillConnect.entity.SkillCategory;

@Repository
public interface SkillCategoryRepository extends JpaRepository<SkillCategory, Long>{
    
}