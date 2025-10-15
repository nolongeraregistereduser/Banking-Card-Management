package dao;

import entity.AlerteFraude;
import util.DBUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


public class AlerteDAO implements BaseDAO<AlerteFraude, Integer> {

    @Override
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

    @Override
    public Optional<AlerteFraude> findById(Integer id) throws SQLException {
        String sql = "SELECT * FROM alertefraude WHERE id = ?";

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToAlerte(rs));
                }
            }
        }
        return Optional.empty();
    }

    @Override
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

    @Override
    public void update(AlerteFraude entity) throws SQLException {
        // Alertes are immutable (record), updates not allowed
        throw new UnsupportedOperationException("AlerteFraude is immutable and cannot be updated");
    }

    @Override
    public void delete(Integer id) throws SQLException {
        String sql = "DELETE FROM alertefraude WHERE id = ?";

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            stmt.executeUpdate();
        }
    }

    // Specific query methods (not CRUD)
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

    // Helper method to map ResultSet to AlerteFraude
    private AlerteFraude mapResultSetToAlerte(ResultSet rs) throws SQLException {
        return new AlerteFraude(
                rs.getInt("id"),
                rs.getString("description"),
                rs.getString("niveau"),
                rs.getInt("idcarte")
        );
    }
}
