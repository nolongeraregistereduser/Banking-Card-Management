package ui;

import service.ClientService;
import service.CarteService;
import service.OperationService;
import service.FraudeService;
import service.RapportService;
import entity.*;
import java.math.BigDecimal;
import java.sql.Date;
import java.time.LocalDate;
import java.util.List;
import java.util.Scanner;

public class MainMenu {
    private final ClientService clientService;
    private final CarteService carteService;
    private final OperationService operationService;
    private final FraudeService fraudeService;
    private final RapportService rapportService;
    private final Scanner scanner;

    public MainMenu() {
        this.clientService = new ClientService();
        this.carteService = new CarteService();
        this.operationService = new OperationService();
        this.fraudeService = new FraudeService();
        this.rapportService = new RapportService();
        this.scanner = new Scanner(System.in);
    }

    public void start() {
        while (true) {
            System.out.println("\n=== Menu Principal ===");
            System.out.println("1. S'inscrire (Register)");
            System.out.println("2. Se connecter (Login)");
            System.out.println("0. Quitter");
            System.out.print("Votre choix: ");
            String choice = scanner.nextLine();
            switch (choice) {
                case "1" -> register();
                case "2" -> login();
                case "0" -> {
                    System.out.println("Au revoir!");
                    return;
                }
                default -> System.out.println("Choix invalide. Veuillez réessayer.");
            }
        }
    }

    private void register() {
        try {
            System.out.println("\n--- Inscription ---");
            System.out.print("Nom: ");
            String nom = scanner.nextLine();
            System.out.print("Email: ");
            String email = scanner.nextLine();
            System.out.print("Téléphone: ");
            String telephone = scanner.nextLine();
            System.out.print("Mot de passe: ");
            String password = scanner.nextLine();
            Client client = new Client(0, nom, email, telephone, password);
            clientService.add(client);
        } catch (Exception e) {
            System.out.println("Erreur lors de l'inscription: " + e.getMessage());
        }
    }

    private void login() {
        try {
            System.out.println("\n--- Connexion ---");
            System.out.print("Email: ");
            String email = scanner.nextLine();
            System.out.print("Mot de passe: ");
            String password = scanner.nextLine();
            Client client = clientService.login(email, password);
            System.out.println("Bienvenue, " + client.getNom() + "!");

            // Show user dashboard after successful login
            showUserDashboard(client);

        } catch (Exception e) {
            System.out.println("Erreur de connexion: " + e.getMessage());
        }
    }

    private void showUserDashboard(Client client) {
        while (true) {
            System.out.println("\n=== Dashboard Client - " + client.getNom() + " ===");
            System.out.println("1. Voir mes cartes");
            System.out.println("2. Créer une nouvelle carte");
            System.out.println("3. Gérer une carte (bloquer/activer)");
            System.out.println("4. Effectuer une opération");
            System.out.println("5. Historique des opérations");
            System.out.println("6. Voir les alertes de fraude");
            System.out.println("7. Mon profil");
            System.out.println("8. Voir mes rapports");
            System.out.println("0. Se déconnecter");
            System.out.print("Votre choix: ");

            String choice = scanner.nextLine();

            try {
                switch (choice) {
                    case "1" -> showMyCards(client);
                    case "2" -> createNewCard(client);
                    case "3" -> manageCard(client);
                    case "4" -> performOperation(client);
                    case "5" -> showOperationHistory(client);
                    case "6" -> showFraudAlerts(client);
                    case "7" -> showProfile(client);
                    case "8" -> showReports(client);
                    case "0" -> {
                        System.out.println("Déconnexion réussie. À bientôt!");
                        return;
                    }
                    default -> System.out.println("Choix invalide. Veuillez réessayer.");
                }
            } catch (Exception e) {
                System.out.println("Erreur: " + e.getMessage());
            }
        }
    }

    private void showMyCards(Client client) {
        try {
            List<Carte> cartes = carteService.findByClientId(client.getId());

            if (cartes.isEmpty()) {
                System.out.println("\nVous n'avez aucune carte pour le moment.");
                return;
            }

            System.out.println("\n=== Mes Cartes ===");
            for (Carte carte : cartes) {
                String typeInfo = getCardTypeInfo(carte);
                System.out.printf("ID: %d | Numéro: %s | Type: %s | Statut: %s | Expiration: %s%s%n",
                    carte.getId(),
                    maskCardNumber(carte.getNumero()),
                    carte.getClass().getSimpleName(),
                    carte.getStatus(),
                    carte.getDateExpiration(),
                    typeInfo
                );
            }
        } catch (Exception e) {
            System.out.println("Erreur lors de la récupération des cartes: " + e.getMessage());
        }
    }

    private void createNewCard(Client client) {
        System.out.println("\n=== Créer une Nouvelle Carte ===");
        System.out.println("Types de cartes disponibles:");
        System.out.println("1. Carte de Débit");
        System.out.println("2. Carte de Crédit");
        System.out.println("3. Carte Prépayée");
        System.out.print("Choisissez le type de carte: ");

        String typeChoice = scanner.nextLine();

        try {
            String numeroGenere = generateCardNumber();
            Date dateExpiration = Date.valueOf(LocalDate.now().plusYears(3));

            Carte nouvelleCarte;

            switch (typeChoice) {
                case "1" -> {
                    System.out.print("Plafond journalier (défaut 1000€): ");
                    String plafondStr = scanner.nextLine();
                    BigDecimal plafond = plafondStr.isEmpty() ?
                        BigDecimal.valueOf(1000) :
                        new BigDecimal(plafondStr);

                    nouvelleCarte = new CarteDebit(0, numeroGenere, dateExpiration,
                        CarteService.STATUS_ACTIVE, client.getId(), plafond);
                }
                case "2" -> {
                    System.out.print("Plafond mensuel (défaut 5000€): ");
                    String plafondStr = scanner.nextLine();
                    BigDecimal plafondMensuel = plafondStr.isEmpty() ?
                        BigDecimal.valueOf(5000) :
                        new BigDecimal(plafondStr);

                    System.out.print("Taux d'intérêt % (défaut 15%): ");
                    String tauxStr = scanner.nextLine();
                    BigDecimal taux = tauxStr.isEmpty() ?
                        BigDecimal.valueOf(15) :
                        new BigDecimal(tauxStr);

                    nouvelleCarte = new CarteCredit(0, numeroGenere, dateExpiration,
                        CarteService.STATUS_ACTIVE, client.getId(), plafondMensuel, taux);
                }
                case "3" -> {
                    System.out.print("Solde initial: ");
                    String soldeStr = scanner.nextLine();
                    BigDecimal solde = new BigDecimal(soldeStr);

                    nouvelleCarte = new CartePrepayee(0, numeroGenere, dateExpiration,
                        CarteService.STATUS_ACTIVE, client.getId(), solde);
                }
                default -> {
                    System.out.println("Type de carte invalide.");
                    return;
                }
            }

            carteService.add(nouvelleCarte);
            System.out.println("Carte créée avec succès! Numéro: " + maskCardNumber(numeroGenere));

        } catch (Exception e) {
            System.out.println("Erreur lors de la création de la carte: " + e.getMessage());
        }
    }

    private void manageCard(Client client) {
        try {
            List<Carte> cartes = carteService.findByClientId(client.getId());

            if (cartes.isEmpty()) {
                System.out.println("Vous n'avez aucune carte à gérer.");
                return;
            }

            System.out.println("\n=== Gestion des Cartes ===");
            showMyCards(client);

            System.out.print("Entrez l'ID de la carte à gérer: ");
            int carteId = Integer.parseInt(scanner.nextLine());

            // Verify card belongs to client
            boolean carteExists = cartes.stream().anyMatch(c -> c.getId() == carteId);
            if (!carteExists) {
                System.out.println("Cette carte ne vous appartient pas.");
                return;
            }

            System.out.println("\nActions disponibles:");
            System.out.println("1. Activer la carte");
            System.out.println("2. Suspendre la carte");
            System.out.println("3. Bloquer la carte");
            System.out.print("Votre choix: ");

            String action = scanner.nextLine();

            switch (action) {
                case "1" -> {
                    carteService.activerCarte(carteId);
                    System.out.println("Carte activée avec succès!");
                }
                case "2" -> {
                    carteService.suspendreCarte(carteId);
                    System.out.println("Carte suspendue avec succès!");
                }
                case "3" -> {
                    carteService.bloquerCarte(carteId);
                    System.out.println("Carte bloquée avec succès!");
                }
                default -> System.out.println("Action invalide.");
            }

        } catch (Exception e) {
            System.out.println("Erreur lors de la gestion de la carte: " + e.getMessage());
        }
    }

    private void performOperation(Client client) {
        try {
            List<Carte> cartes = carteService.findByClientId(client.getId());

            if (cartes.isEmpty()) {
                System.out.println("Vous n'avez aucune carte pour effectuer des opérations.");
                return;
            }

            System.out.println("\n=== Effectuer une Opération ===");
            showMyCards(client);

            System.out.print("Entrez l'ID de la carte: ");
            int carteId = Integer.parseInt(scanner.nextLine());

            // Verify card belongs to client and is active
            Carte carte = cartes.stream()
                .filter(c -> c.getId() == carteId)
                .findFirst()
                .orElse(null);

            if (carte == null) {
                System.out.println("Cette carte ne vous appartient pas.");
                return;
            }

            if (!"ACTIVE".equals(carte.getStatus())) {
                System.out.println("Cette carte n'est pas active.");
                return;
            }

            System.out.println("\nTypes d'opérations:");
            System.out.println("1. Achat");
            System.out.println("2. Retrait");
            System.out.println("3. Paiement en ligne");
            System.out.print("Type d'opération: ");

            String typeOp = scanner.nextLine();

            System.out.print("Montant: ");
            double montant = Double.parseDouble(scanner.nextLine());

            System.out.print("Lieu: ");
            String lieu = scanner.nextLine();

            switch (typeOp) {
                case "1" -> operationService.effectuerAchat(carteId, montant, lieu);
                case "2" -> operationService.effectuerRetrait(carteId, montant, lieu);
                case "3" -> operationService.effectuerPaiementEnLigne(carteId, montant, lieu);
                default -> {
                    System.out.println("Type d'opération invalide.");
                    return;
                }
            }

            // Analyze for fraud after operation
            fraudeService.analyserFraude(carteId);

        } catch (Exception e) {
            System.out.println("Erreur lors de l'opération: " + e.getMessage());
        }
    }

    private void showOperationHistory(Client client) {
        try {
            List<Carte> cartes = carteService.findByClientId(client.getId());

            if (cartes.isEmpty()) {
                System.out.println("Vous n'avez aucune carte.");
                return;
            }

            System.out.println("\n=== Historique des Opérations ===");

            for (Carte carte : cartes) {
                List<OperationCarte> operations = operationService.getOperationsByCard(carte.getId());

                if (!operations.isEmpty()) {
                    System.out.println("\nCarte " + maskCardNumber(carte.getNumero()) + ":");
                    for (OperationCarte op : operations) {
                        System.out.printf("  %s | %.2f€ | %s | %s%n",
                            op.date(), op.montant(), op.type(), op.lieu());
                    }
                }
            }

        } catch (Exception e) {
            System.out.println("Erreur lors de la récupération de l'historique: " + e.getMessage());
        }
    }

    private void showFraudAlerts(Client client) {
        try {
            List<Carte> cartes = carteService.findByClientId(client.getId());

            if (cartes.isEmpty()) {
                System.out.println("Vous n'avez aucune carte.");
                return;
            }

            System.out.println("\n=== Alertes de Fraude ===");
            boolean hasAlerts = false;

            for (Carte carte : cartes) {
                List<AlerteFraude> alertes = fraudeService.getAlertesByCard(carte.getId());

                if (!alertes.isEmpty()) {
                    hasAlerts = true;
                    System.out.println("\nCarte " + maskCardNumber(carte.getNumero()) + ":");
                    for (AlerteFraude alerte : alertes) {
                        System.out.printf("  [%s] %s%n", alerte.niveau(), alerte.description());
                    }
                }
            }

            if (!hasAlerts) {
                System.out.println("Aucune alerte de fraude pour vos cartes.");
            }

        } catch (Exception e) {
            System.out.println("Erreur lors de la récupération des alertes: " + e.getMessage());
        }
    }

    private void showProfile(Client client) {
        System.out.println("\n=== Mon Profil ===");
        System.out.println("Nom: " + client.getNom());
        System.out.println("Email: " + client.getEmail());
        System.out.println("Téléphone: " + client.getTelephone());
        System.out.println("ID Client: " + client.getId());
    }

    private void showReports(Client client) {
        while (true) {
            System.out.println("\n=== Mes Rapports Personnels ===");
            System.out.println("1. Utilisation de mes cartes");
            System.out.println("2. Mes statistiques par type d'opération");
            System.out.println("3. Statut de mes cartes");
            System.out.println("4. Mon rapport complet");
            System.out.println("0. Retour au menu principal");
            System.out.print("Votre choix: ");

            String choice = scanner.nextLine();

            try {
                switch (choice) {
                    case "1" -> rapportService.afficherMesCartesUtilisation(client.getId());
                    case "2" -> rapportService.afficherMesStatistiquesParType(client.getId());
                    case "3" -> rapportService.afficherMesCartesStatut(client.getId());
                    case "4" -> rapportService.afficherMonRapportComplet(client.getId());
                    case "0" -> {
                        return;
                    }
                    default -> System.out.println("Choix invalide. Veuillez réessayer.");
                }

                // Pause to let user read the report
                System.out.println("\nAppuyez sur Entrée pour continuer...");
                scanner.nextLine();

            } catch (Exception e) {
                System.out.println("Erreur lors de la génération du rapport: " + e.getMessage());
            }
        }
    }

    private String getCardTypeInfo(Carte carte) {
        if (carte instanceof CarteDebit debit) {
            return " | Plafond: " + debit.getPlafondJournalier() + "€/jour";
        } else if (carte instanceof CarteCredit credit) {
            return " | Plafond: " + credit.getPlafondMensuel() + "€/mois | Taux: " + credit.getTauxInteret() + "%";
        } else if (carte instanceof CartePrepayee prepayee) {
            return " | Solde: " + prepayee.getSoldeDisponible() + "€";
        }
        return "";
    }

    private String maskCardNumber(String numero) {
        if (numero.length() >= 8) {
            return numero.substring(0, 4) + "****" + numero.substring(numero.length() - 4);
        }
        return numero;
    }

    private String generateCardNumber() {
        // Simple card number generation (in real app, this would be more sophisticated)
        return "4000" + String.format("%012d", (long) (Math.random() * 1000000000000L));
    }
}
