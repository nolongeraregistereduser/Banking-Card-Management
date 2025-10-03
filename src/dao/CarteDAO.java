package dao;

import entity.*;
import util.DBUtil;

import java.math.BigDecimal;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class CarteDAO {

    public void save(Carte carte) throws SQLException {
        String sql = """
            INSERT INTO Carte (numero, dateExpiration, statut, typeCarte, 
                             plafondJournalier, plafondMensuel, tauxInteret, 
                             soldeDisponible, idClient) 
            VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)
            """;

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, carte.getNumero());
            stmt.setDate(2, Date.valueOf(carte.getDateExpiration()));
            stmt.setString(3, carte.getStatut().name());
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

            stmt.setLong(9, carte.getIdClient());

            stmt.executeUpdate();

            try (ResultSet keys = stmt.getGeneratedKeys()) {
                if (keys.next()) {
                    carte.setId(keys.getLong(1));
                }
            }
        }
    }

    public Optional<Carte> findById(Long id) throws SQLException {
        String sql = "SELECT * FROM Carte WHERE id = ?";

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, id);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToCarte(rs));
                }
            }
        }
        return Optional.empty();
    }

    public List<Carte> findByClientId(Long clientId) throws SQLException {
        String sql = "SELECT * FROM Carte WHERE idClient = ?";
        List<Carte> cartes = new ArrayList<>();

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, clientId);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    cartes.add(mapResultSetToCarte(rs));
                }
            }
        }
        return cartes;
    }

    public List<Carte> findAll() throws SQLException {
        String sql = "SELECT * FROM Carte";
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

    public void update(Carte carte) throws SQLException {
        String sql = """
            UPDATE Carte SET numero = ?, dateExpiration = ?, statut = ?, 
                           typeCarte = ?, plafondJournalier = ?, plafondMensuel = ?, 
                           tauxInteret = ?, soldeDisponible = ?, idClient = ? 
            WHERE id = ?
            """;

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, carte.getNumero());
            stmt.setDate(2, Date.valueOf(carte.getDateExpiration()));
            stmt.setString(3, carte.getStatut().name());
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

            stmt.setLong(9, carte.getIdClient());
            stmt.setLong(10, carte.getId());

            stmt.executeUpdate();
        }
    }

    public void delete(Long id) throws SQLException {
        String sql = "DELETE FROM Carte WHERE id = ?";

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, id);
            stmt.executeUpdate();
        }
    }

    public List<Carte> findByStatut(StatutCarte statut) throws SQLException {
        String sql = "SELECT * FROM Carte WHERE statut = ?";
        List<Carte> cartes = new ArrayList<>();

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, statut.name());

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    cartes.add(mapResultSetToCarte(rs));
                }
            }
        }
        return cartes;
    }

    public Optional<Carte> findByNumero(String numero) throws SQLException {
        String sql = "SELECT * FROM Carte WHERE numero = ?";

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

    private Carte mapResultSetToCarte(ResultSet rs) throws SQLException {
        String typeCarte = rs.getString("typeCarte");

        return switch (typeCarte) {
            case "CarteDebit" -> {
                CarteDebit carte = new CarteDebit();
                setCommonFields(carte, rs);
                carte.setPlafondJournalier(rs.getBigDecimal("plafondJournalier"));
                yield carte;
            }
            case "CarteCredit" -> {
                CarteCredit carte = new CarteCredit();
                setCommonFields(carte, rs);
                carte.setPlafondMensuel(rs.getBigDecimal("plafondMensuel"));
                carte.setTauxInteret(rs.getBigDecimal("tauxInteret"));
                yield carte;
            }
            case "CartePrepayee" -> {
                CartePrepayee carte = new CartePrepayee();
                setCommonFields(carte, rs);
                carte.setSoldeDisponible(rs.getBigDecimal("soldeDisponible"));
                yield carte;
            }
            default -> throw new SQLException("Unknown card type: " + typeCarte);
        };
    }

    private void setCommonFields(Carte carte, ResultSet rs) throws SQLException {
        carte.setId(rs.getLong("id"));
        carte.setNumero(rs.getString("numero"));
        carte.setDateExpiration(rs.getDate("dateExpiration").toLocalDate());
        carte.setStatut(StatutCarte.valueOf(rs.getString("statut")));
        carte.setIdClient(rs.getLong("idClient"));
    }
}
