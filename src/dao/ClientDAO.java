package dao;

import util.DBUtil;
import java.sql.*;
import entity.Client;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


public class ClientDAO implements BaseDAO<Client, Integer> {

    @Override
    public void save(Client client) throws SQLException {
        String sql = "INSERT INTO Client (nom, email, telephone, password) VALUES (?, ?, ?, ?)";

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, client.getNom());
            stmt.setString(2, client.getEmail());
            stmt.setString(3, client.getTelephone());
            stmt.setString(4, client.getPassword());

            stmt.executeUpdate();
        }
    }

    @Override
    public Optional<Client> findById(Integer clientId) throws SQLException {
        String sql = "SELECT * FROM Client WHERE id = ?";

        try (Connection connection = DBUtil.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {

            stmt.setInt(1, clientId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return Optional.of(mapResultSetToClient(rs));
            }
        }
        return Optional.empty();
    }

    @Override
    public List<Client> findAll() throws SQLException {
        List<Client> clients = new ArrayList<>();
        String sql = "SELECT * FROM Client";

        try (Connection connection = DBUtil.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                clients.add(mapResultSetToClient(rs));
            }
        }
        return clients;
    }

    @Override
    public void update(Client client) throws SQLException {
        String sql = "UPDATE Client SET nom = ?, email = ?, telephone = ?, password = ? WHERE id = ?";

        try (Connection connection = DBUtil.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {

            stmt.setString(1, client.getNom());
            stmt.setString(2, client.getEmail());
            stmt.setString(3, client.getTelephone());
            stmt.setString(4, client.getPassword());
            stmt.setInt(5, client.getId());
            stmt.executeUpdate();
        }
    }

    @Override
    public void delete(Integer clientId) throws SQLException {
        String sql = "DELETE FROM Client WHERE id = ?";

        try (Connection connection = DBUtil.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {

            stmt.setInt(1, clientId);
            stmt.executeUpdate();
        }
    }

    // Specific query methods (not CRUD)
    public Optional<Client> findByEmailAndPassword(String email, String password) throws SQLException {
        String sql = "SELECT * FROM Client WHERE email = ? AND password = ?";

        try (Connection connection = DBUtil.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {

            stmt.setString(1, email);
            stmt.setString(2, password);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return Optional.of(mapResultSetToClient(rs));
            }
        }
        return Optional.empty();
    }

    public Optional<Client> findByEmail(String email) throws SQLException {
        String sql = "SELECT * FROM Client WHERE email = ?";

        try (Connection connection = DBUtil.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {

            stmt.setString(1, email);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return Optional.of(mapResultSetToClient(rs));
            }
        }
        return Optional.empty();
    }

    // Helper method to map ResultSet to Client
    private Client mapResultSetToClient(ResultSet rs) throws SQLException {
        return new Client(
            rs.getInt("id"),
            rs.getString("nom"),
            rs.getString("email"),
            rs.getString("telephone"),
            rs.getString("password")
        );
    }
}
