package dao;

import util.DBUtil;
import java.sql.*;
import entity.Client;
import java.util.ArrayList;
import java.util.List;


public class ClientDAO {

    public void createClient(Client client) throws SQLException {
        String sql = "INSERT INTO Client (nom, email, telephone, password) VALUES (?, ?, ?, ?)";

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, client.getNom());
            stmt.setString(2, client.getEmail());
            stmt.setString(3, client.getTelephone()); // Fixed: was getPhone()
            stmt.setString(4, client.getPassword());

            stmt.executeUpdate();

            ResultSet resultSet = stmt.getGeneratedKeys();
            if (resultSet.next()) {
                int generatedId = resultSet.getInt(1);
            }
        }
    }

    public Client getClientById(int clientId) throws SQLException {
        String sql = "SELECT * FROM Client WHERE id = ?";

        try (Connection connection = DBUtil.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {

            stmt.setInt(1, clientId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                int id = rs.getInt("id");
                String nom = rs.getString("nom");
                String email = rs.getString("email");
                String telephone = rs.getString("telephone");
                String password = rs.getString("password");
                return new Client(id, nom, email, telephone, password);
            }
        }
        return null;
    }

    public Client getClientByEmailAndPassword(String email, String password) throws SQLException {
        String sql = "SELECT * FROM Client WHERE email = ? AND password = ?";
        try (Connection connection = DBUtil.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, email);
            stmt.setString(2, password);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                int id = rs.getInt("id");
                String nom = rs.getString("nom");
                String telephone = rs.getString("telephone");
                return new Client(id, nom, email, telephone, password);
            }
        }
        return null;
    }

    public List<Client> getAllClients() throws SQLException {
        List<Client> clients = new ArrayList<>();
        String sql = "SELECT * FROM Client";
        try (Connection connection = DBUtil.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                int id = rs.getInt("id");
                String nom = rs.getString("nom");
                String email = rs.getString("email");
                String telephone = rs.getString("telephone");
                String password = rs.getString("password");
                clients.add(new Client(id, nom, email, telephone, password));
            }
        }
        return clients;
    }

    public void updateClient(Client client) throws SQLException {
        String sql = "UPDATE Client SET nom = ?, email = ?, telephone = ?, password = ? WHERE id = ?";
        try (Connection connection = DBUtil.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, client.getNom());
            stmt.setString(2, client.getEmail());
            stmt.setString(3, client.getTelephone()); // Fixed: was getPhone()
            stmt.setString(4, client.getPassword());
            stmt.setInt(5, client.getId());
            stmt.executeUpdate();
        }
    }

    public void deleteClient(int clientId) throws SQLException {
        String sql = "DELETE FROM Client WHERE id = ?";
        try (Connection connection = DBUtil.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, clientId);
            stmt.executeUpdate();
        }
    }
}
