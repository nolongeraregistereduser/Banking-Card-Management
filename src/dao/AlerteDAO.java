package dao;

import entity.AlerteFraude;
import util.DBUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class AlerteDAO {

    public void save(AlerteFraude alerte) throws SQLException {
        String sql = "INSERT INTO alertefraude (description, niveau, idcarte) VALUES (?, ?, ?)";

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, alerte.description());
            stmt.setString(2, alerte.niveau());
            stmt.setInt(3, alerte.idCarte());

            stmt.executeUpdate();
        }
    }

    public Optional<AlerteFraude> findById(Long id) throws SQLException {
        String sql = "SELECT * FROM alertefraude WHERE id = ?";

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, id);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToAlerte(rs));
                }
            }
        }
        return Optional.empty();
    }

    public List<AlerteFraude> findByCarteId(int carteId) throws SQLException {
        String sql = "SELECT * FROM alertefraude WHERE idcarte = ? ORDER BY id DESC";
        List<AlerteFraude> alertes = new ArrayList<>();

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, carteId);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    alertes.add(mapResultSetToAlerte(rs));
                }
            }
        }
        return alertes;
    }

    public List<AlerteFraude> findByNiveau(String niveau) throws SQLException {
        String sql = "SELECT * FROM alertefraude WHERE niveau = ? ORDER BY id DESC";
        List<AlerteFraude> alertes = new ArrayList<>();

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, niveau);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    alertes.add(mapResultSetToAlerte(rs));
                }
            }
        }
        return alertes;
    }

    public List<AlerteFraude> findAll() throws SQLException {
        String sql = "SELECT * FROM alertefraude ORDER BY id DESC";
        List<AlerteFraude> alertes = new ArrayList<>();

        try (Connection conn = DBUtil.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                alertes.add(mapResultSetToAlerte(rs));
            }
        }
        return alertes;
    }

    public void delete(Long id) throws SQLException {
        String sql = "DELETE FROM alertefraude WHERE id = ?";

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, id);
            stmt.executeUpdate();
        }
    }

    private AlerteFraude mapResultSetToAlerte(ResultSet rs) throws SQLException {
        return new AlerteFraude(
                rs.getInt("id"),
                rs.getString("description"),
                rs.getString("niveau"),
                rs.getInt("idcarte")
        );
    }
}
