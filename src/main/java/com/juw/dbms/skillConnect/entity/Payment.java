package com.juw.dbms.skillConnect.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Payment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(nullable = false)
    private String payment_status;

    @Column(nullable = false)
    private String paymentDate;

    @Column(nullable = false)
    private String final_payment;

    @ManyToOne(cascade = CascadeType.ALL)
    private Freelancer freelancer;

    @OneToOne
    private Project project;
}
