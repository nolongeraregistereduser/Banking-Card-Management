package service;

import dao.ClientDAO;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

import entity.Client;


public class ClientService implements BaseService<Client, Integer> {

    private final ClientDAO clientDAO;

    public ClientService(){
        this.clientDAO = new ClientDAO();
    }

    @Override
    public void add(Client client) throws SQLException {
        // Business validation
        if (client == null) {
            throw new IllegalArgumentException("Client cannot be null.");
        }
        if (client.getNom() == null || client.getNom().trim().isEmpty()) {
            throw new IllegalArgumentException("Client name is required.");
        }
        if (client.getPassword() == null || client.getPassword().trim().isEmpty()) {
            throw new IllegalArgumentException("Password is required.");
        }
        if (client.getEmail() == null || client.getEmail().trim().isEmpty()) {
            throw new IllegalArgumentException("Email is required.");
        }

        // Business logic: Check if email already exists
        Optional<Client> existingClient = clientDAO.findByEmail(client.getEmail());
        if (existingClient.isPresent()) {
            throw new SQLException("A client with this email already exists.");
        }

        clientDAO.save(client);
        System.out.println("Client ajouté avec succès.");
    }

    @Override
    public Client findById(Integer clientId) throws SQLException {
        // Business validation
        if (clientId == null || clientId <= 0) {
            throw new IllegalArgumentException("Client ID must be positive.");
        }

        Optional<Client> client = clientDAO.findById(clientId);
        return client.orElse(null);
    }

    @Override
    public List<Client> findAll() throws SQLException {
        return clientDAO.findAll();
    }

    @Override
    public void update(Client client) throws SQLException {
        // Business validation
        if (client == null) {
            throw new IllegalArgumentException("Client cannot be null.");
        }
        if (client.getId() <= 0) {
            throw new IllegalArgumentException("Client ID must be positive.");
        }
        if (client.getNom() == null || client.getNom().trim().isEmpty()) {
            throw new IllegalArgumentException("Client name is required.");
        }
        if (client.getPassword() == null || client.getPassword().trim().isEmpty()) {
            throw new IllegalArgumentException("Password is required.");
        }

        // Business logic: Check if client exists
        if (clientDAO.findById(client.getId()).isEmpty()) {
            throw new SQLException("Client not found with ID: " + client.getId());
        }

        clientDAO.update(client);
        System.out.println("Client mis à jour avec succès.");
    }

    @Override
    public void remove(Integer clientId) throws SQLException {
        // Business validation
        if (clientId == null || clientId <= 0) {
            throw new IllegalArgumentException("Client ID must be positive.");
        }

        // Business logic: Check if client exists
        if (clientDAO.findById(clientId).isEmpty()) {
            throw new SQLException("Client not found with ID: " + clientId);
        }

        clientDAO.delete(clientId);
        System.out.println("Client supprimé avec succès.");
    }

    // Business-specific methods
    public Client login(String email, String password) throws SQLException {
        // Validation
        if (email == null || email.trim().isEmpty()) {
            throw new IllegalArgumentException("Email is required.");
        }
        if (password == null || password.trim().isEmpty()) {
            throw new IllegalArgumentException("Password is required.");
        }

        // Business logic: Authenticate user
        Optional<Client> client = clientDAO.findByEmailAndPassword(email, password);
        if (client.isEmpty()) {
            throw new SQLException("Invalid email or password.");
        }
        return client.get();
    }

    public Client findByEmail(String email) throws SQLException {
        // Validation
        if (email == null || email.trim().isEmpty()) {
            throw new IllegalArgumentException("Email is required.");
        }

        Optional<Client> client = clientDAO.findByEmail(email);
        return client.orElse(null);
    }

    public void displayAllClients() throws SQLException {
        List<Client> clients = findAll();
        if (clients.isEmpty()) {
            System.out.println("Aucun client trouvé.");
        } else {
            System.out.println("\n=== Liste des Clients ===");
            for (Client client : clients) {
                System.out.println("ID: " + client.getId() + " | Nom: " + client.getNom() +
                                 " | Email: " + client.getEmail() + " | Tél: " + client.getTelephone());
            }
        }
    }
}
