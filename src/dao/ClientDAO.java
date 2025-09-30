package dao;

import util.DBUtil;
import java.sql.*;
import entity.Client;

public class ClientDAO {

    public void createClient(Client client) throws SQLException {
        String sql = "INSERT INTO Client (nom, email, telephone) VALUES (?, ?, ?)";

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, client.getNom());
            stmt.setString(2, client.getEmail());
            stmt.setString(3, client.getPhone());

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
                return new Client(id, nom, email, telephone);
            }
        }
        return null;
    }
}
