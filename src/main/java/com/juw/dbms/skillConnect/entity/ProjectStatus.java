package com.juw.dbms.skillConnect.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProjectStatus {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, columnDefinition = "VARCHAR(15) DEFAULT 'Created'")
    private String status = "Created";

    public void updateStatus(String newStatus) {
        if (newStatus != null && !newStatus.trim().isEmpty()) {
            this.status = newStatus;
        } else {
            throw new IllegalArgumentException("Status cannot be null or empty");
        }
    }
}