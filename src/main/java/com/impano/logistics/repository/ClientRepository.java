package com.impano.logistics.repository;

import com.impano.logistics.model.Client;

import java.util.*;

public class ClientRepository {
    private final Map<String, Client> store = new HashMap<>();

    public void save(Client client) {
        store.put(client.getUserId(), client);
    }

    public Optional<Client> findById(String id) {
        return Optional.ofNullable(store.get(id));
    }

    public List<Client> findAll() {
        return new ArrayList<>(store.values());
    }

    public boolean existsByEmail(String email) {
        return store.values().stream().anyMatch(c -> c.getEmail().equalsIgnoreCase(email));
    }
}
