package com.juw.dbms.skillConnect.service;

import com.juw.dbms.skillConnect.entity.Payment;
import com.juw.dbms.skillConnect.repository.PaymentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

@Validated
@Service
public class PaymentService {

    @Autowired
    private PaymentRepository paymentRepository;

    public Payment savePayment(Payment payment) {
        return paymentRepository.save(payment);
    }

    public Payment getPaymentByProjectId(Long projectId) {
        return paymentRepository.findByProjectId(projectId);
    }
}