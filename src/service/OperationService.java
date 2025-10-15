package service;

import dao.OperationDAO;
import dao.CarteDAO;
import entity.OperationCarte;
import entity.Carte;

import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public class OperationService {
    private final OperationDAO operationDAO;
    private final CarteDAO carteDAO;

    // Operation types constants
    public static final String TYPE_ACHAT = "ACHAT";
    public static final String TYPE_RETRAIT = "RETRAIT";
    public static final String TYPE_PAIEMENT_ENLIGNE = "PAIEMENTENLIGNE";

    public OperationService() {
        this.operationDAO = new OperationDAO();
        this.carteDAO = new CarteDAO();
    }

    public void enregistrerOperation(int carteId, double montant, String type, String lieu) throws SQLException {
        // Verify card exists and is active
        Optional<Carte> carteOpt = carteDAO.findById(carteId);
        if (carteOpt.isEmpty()) {
            throw new SQLException("Carte not found with ID: " + carteId);
        }

        Carte carte = carteOpt.get();
        if (!"ACTIVE".equals(carte.getStatus())) {
            throw new SQLException("Cannot perform operation on inactive card");
        }

        // Create operation
        OperationCarte operation = new OperationCarte(
                0, // ID will be auto-generated
                Timestamp.valueOf(LocalDateTime.now()),
                montant,
                type,
                lieu,
                carteId
        );

        operationDAO.save(operation);
        System.out.println("Operation enregistrée avec succès");
    }

    public List<OperationCarte> getOperationsByCard(int carteId) throws SQLException {
        return operationDAO.findByCarteId(carteId);
    }

    public List<OperationCarte> getOperationsByType(String type) throws SQLException {
        return operationDAO.findByType(type);
    }

    public List<OperationCarte> getAllOperations() throws SQLException {
        return operationDAO.findAll();
    }

    public void effectuerAchat(int carteId, double montant, String lieu) throws SQLException {
        enregistrerOperation(carteId, montant, TYPE_ACHAT, lieu);
    }

    public void effectuerRetrait(int carteId, double montant, String lieu) throws SQLException {
        enregistrerOperation(carteId, montant, TYPE_RETRAIT, lieu);
    }

    public void effectuerPaiementEnLigne(int carteId, double montant, String lieu) throws SQLException {
        enregistrerOperation(carteId, montant, TYPE_PAIEMENT_ENLIGNE, lieu);
    }
}
