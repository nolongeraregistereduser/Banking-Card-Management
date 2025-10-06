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

    public void afficherMesCartesUtilisation(int clientId) throws SQLException {
        System.out.println("\n=== UTILISATION DE MES CARTES ===");

        List<Carte> mesCartes = carteDAO.findByClientId((long) clientId);
        List<OperationCarte> operations = operationDAO.findAll();

        Map<Integer, Long> utilisationMesCartes = operations.stream()
                .filter(op -> mesCartes.stream().anyMatch(carte -> carte.getId() == op.idCarte()))
                .collect(Collectors.groupingBy(
                    OperationCarte::idCarte,
                    Collectors.counting()
                ));

        if (utilisationMesCartes.isEmpty()) {
            System.out.println("Aucune opération trouvée pour vos cartes.");
            return;
        }

        utilisationMesCartes.entrySet().stream()
                .sorted(Map.Entry.<Integer, Long>comparingByValue().reversed())
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

    public void afficherMesStatistiquesParType(int clientId) throws SQLException {
        System.out.println("\n=== MES STATISTIQUES PAR TYPE D'OPÉRATION ===");

        List<Carte> mesCartes = carteDAO.findByClientId((long) clientId);
        List<OperationCarte> mesOperations = operationDAO.findAll().stream()
                .filter(op -> mesCartes.stream().anyMatch(carte -> carte.getId() == op.idCarte()))
                .collect(Collectors.toList());

        if (mesOperations.isEmpty()) {
            System.out.println("Aucune opération trouvée pour vos cartes.");
            return;
        }

        Map<String, Double> montantsParType = mesOperations.stream()
                .collect(Collectors.groupingBy(
                    OperationCarte::type,
                    Collectors.summingDouble(OperationCarte::montant)
                ));

        Map<String, Long> nombreParType = mesOperations.stream()
                .collect(Collectors.groupingBy(
                    OperationCarte::type,
                    Collectors.counting()
                ));

        for (String type : montantsParType.keySet()) {
            System.out.printf("%s: %d opérations, Montant total: %.2f€%n",
                    type, nombreParType.get(type), montantsParType.get(type));
        }
    }

    public void afficherMesCartesStatut(int clientId) throws SQLException {
        System.out.println("\n=== STATUT DE MES CARTES ===");

        List<Carte> mesCartes = carteDAO.findByClientId((long) clientId);

        if (mesCartes.isEmpty()) {
            System.out.println("Vous n'avez aucune carte.");
            return;
        }

        Map<String, Long> cartesParStatut = mesCartes.stream()
                .collect(Collectors.groupingBy(
                    Carte::getStatus,
                    Collectors.counting()
                ));

        System.out.println("Répartition de mes cartes par statut:");
        cartesParStatut.forEach((statut, count) ->
            System.out.println(statut + ": " + count + " carte(s)"));

        System.out.println("\nDétail de mes cartes:");
        for (Carte carte : mesCartes) {
            String numeroMasque = carte.getNumero().substring(0, 4) + "****" +
                                carte.getNumero().substring(carte.getNumero().length() - 4);
            System.out.println("- " + numeroMasque + " (" + carte.getClass().getSimpleName() + ") - " + carte.getStatus());
        }
    }

    public void afficherMonRapportComplet(int clientId) throws SQLException {
        System.out.println("\n" + "=".repeat(50));
        System.out.println("           MON RAPPORT PERSONNEL");
        System.out.println("=".repeat(50));

        // Mes cartes par type
        List<Carte> mesCartes = carteDAO.findByClientId((long) clientId);

        if (mesCartes.isEmpty()) {
            System.out.println("Vous n'avez aucune carte.");
            return;
        }

        Map<String, Long> cartesParType = mesCartes.stream()
                .collect(Collectors.groupingBy(
                    carte -> carte.getClass().getSimpleName(),
                    Collectors.counting()
                ));

        System.out.println("\n=== MES CARTES PAR TYPE ===");
        cartesParType.forEach((type, count) ->
            System.out.println(type + ": " + count + " carte(s)"));

        // Mes opérations et montant total
        List<OperationCarte> mesOperations = operationDAO.findAll().stream()
                .filter(op -> mesCartes.stream().anyMatch(carte -> carte.getId() == op.idCarte()))
                .collect(Collectors.toList());

        double monMontantTotal = mesOperations.stream()
                .mapToDouble(OperationCarte::montant)
                .sum();

        System.out.println("\n=== MES STATISTIQUES GLOBALES ===");
        System.out.println("Nombre de mes opérations: " + mesOperations.size());
        System.out.printf("Montant total de mes opérations: %.2f€%n", monMontantTotal);
        System.out.println("Nombre de mes cartes: " + mesCartes.size());

        // Appeler les autres méthodes personnalisées
        afficherMesCartesUtilisation(clientId);
        afficherMesStatistiquesParType(clientId);
        afficherMesCartesStatut(clientId);
    }
}
