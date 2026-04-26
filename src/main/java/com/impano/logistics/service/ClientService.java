package com.impano.logistics.service;

import com.impano.logistics.model.Client;
import com.impano.logistics.repository.ClientRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class ClientService {
    private final ClientRepository clientRepo;

    public ClientService(ClientRepository clientRepo) {
        this.clientRepo = clientRepo;
    }

    public Client registerClient(String name, String email, String password, String phone, String address) {
        if (clientRepo.existsByEmail(email)) {
            throw new IllegalArgumentException("Client with email " + email + " already exists.");
        }
        String id = "CLT-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        Client client = new Client(id, name, email, password, phone, address);
        clientRepo.save(client);
        return client;
    }

    public List<Client> getAllClients() {
        return clientRepo.findAll();
    }

    public Optional<Client> findById(String id) {
        return clientRepo.findById(id);
    }
}
