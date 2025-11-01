package com.juw.dbms.skillConnect.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class FreelancerSkill {
    @Id
    @GeneratedValue (strategy = GenerationType.IDENTITY) 
    private Long id;

    @Column(nullable = false)
    private float price;

    @ManyToOne(cascade = CascadeType.ALL)
    private Freelancer freelancer;

    @ManyToOne
    private SkillCategory skillCategory;
}
