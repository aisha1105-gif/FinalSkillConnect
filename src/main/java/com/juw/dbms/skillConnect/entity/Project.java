package com.juw.dbms.skillConnect.entity;

import java.sql.Timestamp;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Project {
    @Id
    @GeneratedValue (strategy = GenerationType.IDENTITY) 
    private Long id;

    @Column(nullable = false, columnDefinition = "VARCHAR(50)")
    private String name;

    @Column(nullable = false, columnDefinition = "VARCHAR(255)")
    private String criteria;

    @Column(nullable = false, columnDefinition = "VARCHAR(50)")
    private String delivery_time;

    @Column(nullable = true, columnDefinition = "TIMESTAMP")
    private Timestamp start_date;    

    @Column(nullable = true, columnDefinition = "TIMESTAMP")
    private Timestamp completion_date; 

    @ManyToOne
    private ProjectStatus projectStatus;

    @ManyToOne
    private SkillCategory skillCategory;

    @ManyToOne
    private FreelancerSkill freelancerSkill;

    @ManyToOne
    private Client client;

    @ManyToOne(cascade = CascadeType.ALL)
    private Freelancer freelancer;

    @OneToMany(mappedBy = "project", cascade = CascadeType.ALL) 
    // cascade means any operation performed on a Project (like save, delete, update) will also automatically 
    // apply to its associated Bidding objects like if a Project is deleted, its related bids will also be deleted.
    private List<Bidding> bids;

    // Add OneToOne relationship with Payment
    @OneToOne(mappedBy = "project", fetch = jakarta.persistence.FetchType.EAGER)
    private Payment payment;

    // to track which freelancer's bid which was accepted by the client.
    @OneToOne
    private Bidding selectedBid;

}
