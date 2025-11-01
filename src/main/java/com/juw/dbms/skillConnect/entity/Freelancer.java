package com.juw.dbms.skillConnect.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Entity
@Data
@EqualsAndHashCode(callSuper=false)
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "Freelancer")
public class Freelancer extends User{
    @Column(nullable = false, columnDefinition = "VARCHAR(50) DEFAULT 'Beginner'")
    private String ranking = "beginner";

    @Column(nullable = false, columnDefinition = "INT DEFAULT 0")
    private int works_completed = 0;
}
