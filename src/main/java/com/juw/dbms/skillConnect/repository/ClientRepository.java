package com.juw.dbms.skillConnect.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.juw.dbms.skillConnect.entity.Client;

@Repository
public interface ClientRepository extends JpaRepository<Client, Long> {
    public Optional<Client> findByEmail(String email);
    public Optional<Client> findByEmailAndPassword(String email, String Password);
}
