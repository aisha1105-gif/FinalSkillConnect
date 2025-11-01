package com.juw.dbms.skillConnect.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.juw.dbms.skillConnect.entity.Payment;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {
    Payment findByProjectId(Long prejectId); 
    
}
