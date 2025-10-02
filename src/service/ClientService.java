package service;

import dao.ClientDAO;

import java.sql.SQLException;
import java.util.List;

import entity.Client;




public class ClientService {

    private ClientDAO clientDAO;

    public ClientService(){
        this.clientDAO = new ClientDAO();
    }


    public void addClient(Client client) throws SQLException {
        if (client == null) {
            throw new IllegalArgumentException("Client cannot be null.");
        }
        if (client.getNom() == null || client.getNom().trim().isEmpty()) {
            throw new IllegalArgumentException("Client name is required.");
        }
        if (client.getPassword() == null || client.getPassword().trim().isEmpty()) {
            throw new IllegalArgumentException("Password is required.");
        }
        clientDAO.createClient(client);
        System.out.println("Client Ajoutée avec succes.");
    }


    public Client findClientById(int clientId) throws SQLException {
        if (clientId <= 0) {
            throw new IllegalArgumentException("Client ID doit etre positive.");
        }
        return clientDAO.getClientById(clientId);
    }

    public void removeClient(int clientId) throws SQLException {
        if (clientId <= 0) {
            throw new IllegalArgumentException("Client ID doit etre positive.");
        }
        clientDAO.deleteClient(clientId);
        System.out.println("Client supprimée avec succes.");
    }

    public void updateClient(Client client) throws SQLException {
        if (client == null) {
            throw new IllegalArgumentException("Client cannot be null.");
        }
        if (client.getNom() == null || client.getNom().trim().isEmpty()) {
            throw new IllegalArgumentException("Client name is required.");
        }
        if (client.getPassword() == null || client.getPassword().trim().isEmpty()) {
            throw new IllegalArgumentException("Password is required.");
        }
        clientDAO.updateClient(client);
        System.out.println("Client mis à jour avec succes.");
    }

    public List<Client> getAllClients() throws SQLException {
        return clientDAO.getAllClients();
    }

    public Client findClientByEmail(String email) throws SQLException {
        if (email == null || email.trim().isEmpty()) {
            throw new IllegalArgumentException("Email is required.");
        }
        for (Client c : clientDAO.getAllClients()) {
            if (c.getEmail().equalsIgnoreCase(email)) {
                return c;
            }
        }
        return null;
    }

    public Client login(String email, String password) throws SQLException {
        if (email == null || email.trim().isEmpty()) {
            throw new IllegalArgumentException("Email is required.");
        }
        if (password == null || password.trim().isEmpty()) {
            throw new IllegalArgumentException("Password is required.");
        }
        Client client = clientDAO.getClientByEmailAndPassword(email, password);
        if (client == null) {
            throw new IllegalArgumentException("Invalid email or password.");
        }
        return client;
    }

    public void displayAllClients() throws SQLException {
        List<Client> clients = getAllClients();
        if (clients.isEmpty()) {
            System.out.println("Aucun client trouvé.");
        } else {
            for (Client client : clients) {
                System.out.println(client);
            }
        }
    }
}
