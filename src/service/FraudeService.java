package service;

import dao.AlerteDAO;
import dao.OperationDAO;
import dao.CarteDAO;
import entity.AlerteFraude;
import entity.OperationCarte;
import entity.Carte;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class FraudeService {
    private final AlerteDAO alerteDAO;
    private final OperationDAO operationDAO;
    private final CarteDAO carteDAO;

    // Alert levels
    public static final String NIVEAU_INFO = "INFO";
    public static final String NIVEAU_AVERTISSEMENT = "AVERTISSEMENT";
    public static final String NIVEAU_CRITIQUE = "CRITIQUE";

    // Fraud detection thresholds
    private static final double MONTANT_SUSPECT = 1000.0;
    private static final int OPERATIONS_RAPIDES_LIMITE = 3; // 3 operations in short time

    public FraudeService() {
        this.alerteDAO = new AlerteDAO();
        this.operationDAO = new OperationDAO();
        this.carteDAO = new CarteDAO();
    }

    public void analyserFraude(int carteId) throws SQLException {
        List<OperationCarte> operations = operationDAO.findByCarteId(carteId);

        if (operations.isEmpty()) {
            return;
        }

        // Check for high amount operations
        for (OperationCarte operation : operations) {
            if (operation.montant() > MONTANT_SUSPECT) {
                creerAlerte(carteId,
                    "Montant élevé détecté: " + operation.montant() + "€",
                    NIVEAU_CRITIQUE);
            }
        }

        // Check for rapid operations in different locations
        detecterOperationsRapides(carteId, operations);
    }

    private void detecterOperationsRapides(int carteId, List<OperationCarte> operations) throws SQLException {
        if (operations.size() < 2) return;

        // Group operations by location and check for rapid changes
        for (int i = 0; i < operations.size() - 1; i++) {
            OperationCarte op1 = operations.get(i);
            OperationCarte op2 = operations.get(i + 1);

            // Check if operations are in different locations within short time
            if (!op1.lieu().equals(op2.lieu())) {
                long timeDiff = Math.abs(op1.date().getTime() - op2.date().getTime());
                long minutesDiff = timeDiff / (1000 * 60);

                if (minutesDiff < 30) { // Less than 30 minutes apart
                    creerAlerte(carteId,
                        "Opérations rapprochées dans des lieux différents: " +
                        op1.lieu() + " et " + op2.lieu(),
                        NIVEAU_AVERTISSEMENT);
                }
            }
        }
    }

    public void creerAlerte(int carteId, String description, String niveau) throws SQLException {
        AlerteFraude alerte = new AlerteFraude(0, description, niveau, carteId);
        alerteDAO.save(alerte);

        // If critical, auto-suspend the card
        if (NIVEAU_CRITIQUE.equals(niveau)) {
            suspendreCarteAutomatiquement(carteId);
        }

        System.out.println("Alerte de fraude créée: " + description);
    }

    private void suspendreCarteAutomatiquement(int carteId) throws SQLException {
        CarteService carteService = new CarteService();
        carteService.suspendreCarte(carteId);
        System.out.println("Carte automatiquement suspendue pour suspicion de fraude");
    }

    public List<AlerteFraude> getAlertesByCard(int carteId) throws SQLException {
        return alerteDAO.findByCarteId(carteId);
    }

    public List<AlerteFraude> getAlertesByNiveau(String niveau) throws SQLException {
        return alerteDAO.findByNiveau(niveau);
    }

    public List<AlerteFraude> getAllAlertes() throws SQLException {
        return alerteDAO.findAll();
    }

    public void analyserToutesLesCartes() throws SQLException {
        List<Carte> cartes = carteDAO.findAll();
        for (Carte carte : cartes) {
            analyserFraude(carte.getId());
        }
        System.out.println("Analyse de fraude terminée pour toutes les cartes");
    }
}
