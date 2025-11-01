package com.juw.dbms.skillConnect.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import com.juw.dbms.skillConnect.entity.SkillCategory;
import com.juw.dbms.skillConnect.repository.SkillCategoryRepository;

@Validated
@Service
public class SkillCategoryService {
    @Autowired
    private SkillCategoryRepository skillCategoryRepository;

    public List<SkillCategory> getAllSkillCategories() {
        return skillCategoryRepository.findAll();
    }

    public SkillCategory getSkillCategoryById(Long id) {
        return skillCategoryRepository.findById(id).orElseThrow();
    }

    public SkillCategory findById(Long id) {
        return skillCategoryRepository.findById(id).orElseThrow();
    }

}
