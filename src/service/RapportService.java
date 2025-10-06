package service;

import dao.CarteDAO;
import dao.OperationDAO;
import entity.Carte;
import entity.OperationCarte;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class RapportService {
    private final CarteDAO carteDAO;
    private final OperationDAO operationDAO;

    public RapportService() {
        this.carteDAO = new CarteDAO();
        this.operationDAO = new OperationDAO();
    }

    public void afficherTop5CartesUtilisees() throws SQLException {
        System.out.println("\n=== TOP 5 DES CARTES LES PLUS UTILISÉES ===");

        List<OperationCarte> operations = operationDAO.findAll();
        Map<Integer, Long> cartesUtilisation = operations.stream()
                .collect(Collectors.groupingBy(
                    OperationCarte::idCarte,
                    Collectors.counting()
                ));

        cartesUtilisation.entrySet().stream()
                .sorted(Map.Entry.<Integer, Long>comparingByValue().reversed())
                .limit(5)
                .forEach(entry -> {
                    try {
                        Carte carte = carteDAO.findById((long) entry.getKey()).orElse(null);
                        String numeroMasque = carte != null ?
                            carte.getNumero().substring(0, 4) + "****" +
                            carte.getNumero().substring(carte.getNumero().length() - 4) :
                            "Unknown";
                        System.out.println("Carte " + numeroMasque + ": " + entry.getValue() + " opérations");
                    } catch (SQLException e) {
                        System.out.println("Carte ID " + entry.getKey() + ": " + entry.getValue() + " opérations");
                    }
                });
    }

    public void afficherStatistiquesParType() throws SQLException {
        System.out.println("\n=== STATISTIQUES DES OPÉRATIONS PAR TYPE ===");

        List<OperationCarte> operations = operationDAO.findAll();
        Map<String, Double> montantsParType = operations.stream()
                .collect(Collectors.groupingBy(
                    OperationCarte::type,
                    Collectors.summingDouble(OperationCarte::montant)
                ));

        Map<String, Long> nombreParType = operations.stream()
                .collect(Collectors.groupingBy(
                    OperationCarte::type,
                    Collectors.counting()
                ));

        for (String type : montantsParType.keySet()) {
            System.out.printf("%s: %d opérations, Montant total: %.2f€%n",
                    type, nombreParType.get(type), montantsParType.get(type));
        }
    }

    public void afficherCartesBloquees() throws SQLException {
        System.out.println("\n=== CARTES BLOQUÉES OU SUSPENDUES ===");

        List<Carte> cartesBloquees = carteDAO.findByStatut("BLOQUEE");
        List<Carte> cartesSuspendues = carteDAO.findByStatut("SUSPENDUE");

        System.out.println("Cartes bloquées:");
        for (Carte carte : cartesBloquees) {
            String numeroMasque = carte.getNumero().substring(0, 4) + "****" +
                                carte.getNumero().substring(carte.getNumero().length() - 4);
            System.out.println("- " + numeroMasque + " (Client ID: " + carte.getClientId() + ")");
        }

        System.out.println("\nCartes suspendues:");
        for (Carte carte : cartesSuspendues) {
            String numeroMasque = carte.getNumero().substring(0, 4) + "****" +
                                carte.getNumero().substring(carte.getNumero().length() - 4);
            System.out.println("- " + numeroMasque + " (Client ID: " + carte.getClientId() + ")");
        }
    }

    public void afficherRapportComplet() throws SQLException {
        System.out.println("\n" + "=".repeat(50));
        System.out.println("           RAPPORT COMPLET DU SYSTÈME");
        System.out.println("=".repeat(50));

        // Total cards by type
        List<Carte> toutesCartes = carteDAO.findAll();
        Map<String, Long> cartesParType = toutesCartes.stream()
                .collect(Collectors.groupingBy(
                    carte -> carte.getClass().getSimpleName(),
                    Collectors.counting()
                ));

        System.out.println("\n=== RÉPARTITION DES CARTES PAR TYPE ===");
        cartesParType.forEach((type, count) ->
            System.out.println(type + ": " + count + " cartes"));

        // Total operations and amount
        List<OperationCarte> operations = operationDAO.findAll();
        double montantTotal = operations.stream()
                .mapToDouble(OperationCarte::montant)
                .sum();

        System.out.println("\n=== STATISTIQUES GLOBALES ===");
        System.out.println("Nombre total d'opérations: " + operations.size());
        System.out.printf("Montant total des opérations: %.2f€%n", montantTotal);
        System.out.println("Nombre total de cartes: " + toutesCartes.size());

        // Call other report methods
        afficherTop5CartesUtilisees();
        afficherStatistiquesParType();
        afficherCartesBloquees();
    }
}
