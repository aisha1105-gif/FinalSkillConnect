package com.juw.dbms.skillConnect.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import com.juw.dbms.skillConnect.entity.Client;
import com.juw.dbms.skillConnect.repository.ClientRepository;

@Validated
@Service
/* The service class contains business logic related to the entity (user). 
It interacts with both the controller to handle requests and the repository to 
perform database operations. */
public class ClientService {
    @Autowired // Automatically create clientRepository instance
    ClientRepository clientRepository;

    public Client findById(Long id) {
        return clientRepository.findById(id).orElse(null);
    }
    
    public Optional<String> saveClient(Client client) {
        Optional<Client> existingClient = clientRepository.findByEmail(client.getEmail());

        if (existingClient.isPresent()) {
            return Optional.of("Client with that email already exists.");
        }
        else {
            clientRepository.save(client);
            return Optional.empty();
        }
    }

    public Optional<Client> authenticateClient(String email, String password) {
        return clientRepository.findByEmailAndPassword(email, password);
    } 

    public Optional<String> updateClient(Client client, Client updatedClientRecord) {
        Optional<Client> clientRecord = clientRepository.findById(client.getId());
        
        if (clientRecord.isPresent()) {
            Client updatedClient = (Client) clientRecord.get();
            
            updatedClient.setFirst_name(updatedClientRecord.getFirst_name());
            updatedClient.setLast_name(updatedClientRecord.getLast_name());
            updatedClient.setEmail(updatedClientRecord.getEmail());
            updatedClient.setCompanyName(updatedClientRecord.getCompanyName());
            updatedClient.setRelatedIndustry(updatedClientRecord.getRelatedIndustry());

            // Update password only if password is explicitly updated since HTML keeps input fields of password empty by default
            if (updatedClientRecord.getPassword() != null && !updatedClientRecord.getPassword().isBlank()) {
                updatedClient.setPassword(updatedClientRecord.getPassword());
            }
            clientRepository.save(updatedClient);

            return Optional.empty();
        }
        else {
            return Optional.of("Some error occurred.");
        }
    }

    public Optional<String> deleteClient(Client client) {
        Optional<Client> clientToDelete  = clientRepository.findById(client.getId());
        if (clientToDelete.isPresent()) {
            clientRepository.deleteById(client.getId());
            return Optional.empty();
        }
        else {
            return Optional.of("Something went wrong");
        }
    }

    public long countAllClients() {
        return clientRepository.count();
    }
}
