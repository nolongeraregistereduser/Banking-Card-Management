package dao;

import entity.OperationCarte;
import util.DBUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


public class OperationDAO implements BaseDAO<OperationCarte, Integer> {

    @Override
    public void save(OperationCarte operation) throws SQLException {
        String sql = "INSERT INTO operationcarte (date, montant, type, lieu, idcarte) VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setTimestamp(1, operation.date());
            stmt.setDouble(2, operation.montant());
            stmt.setString(3, operation.type());
            stmt.setString(4, operation.lieu());
            stmt.setInt(5, operation.idCarte());

            stmt.executeUpdate();
        }
    }

    @Override
    public Optional<OperationCarte> findById(Integer id) throws SQLException {
        String sql = "SELECT * FROM operationcarte WHERE id = ?";

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToOperation(rs));
                }
            }
        }
        return Optional.empty();
    }

    @Override
    public List<OperationCarte> findAll() throws SQLException {
        String sql = "SELECT * FROM operationcarte ORDER BY date DESC";
        List<OperationCarte> operations = new ArrayList<>();

        try (Connection conn = DBUtil.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                operations.add(mapResultSetToOperation(rs));
            }
        }
        return operations;
    }

    @Override
    public void update(OperationCarte entity) throws SQLException {
        // Operations are immutable (record), updates not allowed
        throw new UnsupportedOperationException("OperationCarte is immutable and cannot be updated");
    }

    @Override
    public void delete(Integer id) throws SQLException {
        String sql = "DELETE FROM operationcarte WHERE id = ?";

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            stmt.executeUpdate();
        }
    }

    // Specific query methods (not CRUD)
    public List<OperationCarte> findByCarteId(int carteId) throws SQLException {
        String sql = "SELECT * FROM operationcarte WHERE idcarte = ? ORDER BY date DESC";
        List<OperationCarte> operations = new ArrayList<>();

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, carteId);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    operations.add(mapResultSetToOperation(rs));
                }
            }
        }
        return operations;
    }

    public List<OperationCarte> findByType(String type) throws SQLException {
        String sql = "SELECT * FROM operationcarte WHERE type = ? ORDER BY date DESC";
        List<OperationCarte> operations = new ArrayList<>();

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, type);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    operations.add(mapResultSetToOperation(rs));
                }
            }
        }
        return operations;
    }

    // Helper method to map ResultSet to OperationCarte
    private OperationCarte mapResultSetToOperation(ResultSet rs) throws SQLException {
        return new OperationCarte(
                rs.getInt("id"),
                rs.getTimestamp("date"),
                rs.getDouble("montant"),
                rs.getString("type"),
                rs.getString("lieu"),
                rs.getInt("idcarte")
        );
    }
}
