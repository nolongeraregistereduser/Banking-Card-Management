package dao;

import entity.*;
import util.DBUtil;

import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


public class CarteDAO implements BaseDAO<Carte, Integer> {

    @Override
    public void save(Carte carte) throws SQLException {
        String sql = """
            INSERT INTO carte (numero, dateexpiration, statut, typecarte, 
                             plafondjournalier, plafondmensuel, tauxinteret, 
                             soldedisponible, idclient) 
            VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)
            """;

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, carte.getNumero());
            stmt.setDate(2, carte.getDateExpiration());
            stmt.setString(3, carte.getStatus());
            stmt.setString(4, carte.getClass().getSimpleName());

            // Set type-specific fields
            if (carte instanceof CarteDebit debit) {
                stmt.setBigDecimal(5, debit.getPlafondJournalier());
                stmt.setNull(6, Types.DECIMAL);
                stmt.setNull(7, Types.DECIMAL);
                stmt.setNull(8, Types.DECIMAL);
            } else if (carte instanceof CarteCredit credit) {
                stmt.setNull(5, Types.DECIMAL);
                stmt.setBigDecimal(6, credit.getPlafondMensuel());
                stmt.setBigDecimal(7, credit.getTauxInteret());
                stmt.setNull(8, Types.DECIMAL);
            } else if (carte instanceof CartePrepayee prepayee) {
                stmt.setNull(5, Types.DECIMAL);
                stmt.setNull(6, Types.DECIMAL);
                stmt.setNull(7, Types.DECIMAL);
                stmt.setBigDecimal(8, prepayee.getSoldeDisponible());
            }

            stmt.setLong(9, carte.getClientId());

            stmt.executeUpdate();

            try (ResultSet keys = stmt.getGeneratedKeys()) {
                if (keys.next()) {
                    carte.setId((int) keys.getLong(1));
                }
            }
        }
    }

    @Override
    public Optional<Carte> findById(Integer id) throws SQLException {
        String sql = "SELECT * FROM carte WHERE id = ?";

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToCarte(rs));
                }
            }
        }
        return Optional.empty();
    }

    @Override
    public List<Carte> findAll() throws SQLException {
        String sql = "SELECT * FROM carte";
        List<Carte> cartes = new ArrayList<>();

        try (Connection conn = DBUtil.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                cartes.add(mapResultSetToCarte(rs));
            }
        }
        return cartes;
    }

    @Override
    public void update(Carte carte) throws SQLException {
        String sql = """
            UPDATE carte SET numero = ?, dateexpiration = ?, statut = ?, 
                           typecarte = ?, plafondjournalier = ?, plafondmensuel = ?, 
                           tauxinteret = ?, soldedisponible = ?, idclient = ? 
            WHERE id = ?
            """;

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, carte.getNumero());
            stmt.setDate(2, carte.getDateExpiration());
            stmt.setString(3, carte.getStatus());
            stmt.setString(4, carte.getClass().getSimpleName());

            // Set type-specific fields
            if (carte instanceof CarteDebit debit) {
                stmt.setBigDecimal(5, debit.getPlafondJournalier());
                stmt.setNull(6, Types.DECIMAL);
                stmt.setNull(7, Types.DECIMAL);
                stmt.setNull(8, Types.DECIMAL);
            } else if (carte instanceof CarteCredit credit) {
                stmt.setNull(5, Types.DECIMAL);
                stmt.setBigDecimal(6, credit.getPlafondMensuel());
                stmt.setBigDecimal(7, credit.getTauxInteret());
                stmt.setNull(8, Types.DECIMAL);
            } else if (carte instanceof CartePrepayee prepayee) {
                stmt.setNull(5, Types.DECIMAL);
                stmt.setNull(6, Types.DECIMAL);
                stmt.setNull(7, Types.DECIMAL);
                stmt.setBigDecimal(8, prepayee.getSoldeDisponible());
            }

            stmt.setLong(9, carte.getClientId());
            stmt.setLong(10, carte.getId());

            stmt.executeUpdate();
        }
    }

    @Override
    public void delete(Integer id) throws SQLException {
        String sql = "DELETE FROM carte WHERE id = ?";

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            stmt.executeUpdate();
        }
    }

    // Specific query methods (not CRUD)
    public List<Carte> findByClientId(Integer clientId) throws SQLException {
        String sql = "SELECT * FROM carte WHERE idclient = ?";
        List<Carte> cartes = new ArrayList<>();

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, clientId);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    cartes.add(mapResultSetToCarte(rs));
                }
            }
        }
        return cartes;
    }

    public List<Carte> findByStatut(String statut) throws SQLException {
        String sql = "SELECT * FROM carte WHERE statut = ?";
        List<Carte> cartes = new ArrayList<>();

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, statut);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    cartes.add(mapResultSetToCarte(rs));
                }
            }
        }
        return cartes;
    }

    public Optional<Carte> findByNumero(String numero) throws SQLException {
        String sql = "SELECT * FROM carte WHERE numero = ?";

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, numero);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToCarte(rs));
                }
            }
        }
        return Optional.empty();
    }

    // Helper method to map ResultSet to Carte
    private Carte mapResultSetToCarte(ResultSet rs) throws SQLException {
        String typeCarte = rs.getString("typecarte");

        return switch (typeCarte) {
            case "CarteDebit" -> {
                BigDecimal plafond = rs.getBigDecimal("plafondjournalier");
                CarteDebit carte = new CarteDebit(
                    rs.getInt("id"),
                    rs.getString("numero"),
                    rs.getDate("dateexpiration"),
                    rs.getString("statut"),
                    rs.getInt("idclient"),
                    plafond != null ? plafond : BigDecimal.valueOf(1000.0)
                );
                yield carte;
            }
            case "CarteCredit" -> {
                BigDecimal plafondMensuel = rs.getBigDecimal("plafondmensuel");
                BigDecimal tauxInteret = rs.getBigDecimal("tauxinteret");
                CarteCredit carte = new CarteCredit(
                    rs.getInt("id"),
                    rs.getString("numero"),
                    rs.getDate("dateexpiration"),
                    rs.getString("statut"),
                    rs.getInt("idclient"),
                    plafondMensuel != null ? plafondMensuel : BigDecimal.valueOf(5000.0),
                    tauxInteret != null ? tauxInteret : BigDecimal.valueOf(15.0)
                );
                yield carte;
            }
            case "CartePrepayee" -> {
                BigDecimal solde = rs.getBigDecimal("soldedisponible");
                CartePrepayee carte = new CartePrepayee(
                    rs.getInt("id"),
                    rs.getString("numero"),
                    rs.getDate("dateexpiration"),
                    rs.getString("statut"),
                    rs.getInt("idclient"),
                    solde != null ? solde : BigDecimal.valueOf(0.0)
                );
                yield carte;
            }
            default -> throw new SQLException("Unknown card type: " + typeCarte);
        };
    }
}
