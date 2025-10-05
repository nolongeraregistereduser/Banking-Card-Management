package ui;

import service.ClientService;
import service.CarteService;
import entity.Client;
import entity.Carte;
import entity.CarteDebit;
import entity.CarteCredit;
import entity.CartePrepayee;
import java.math.BigDecimal;
import java.sql.Date;
import java.time.LocalDate;
import java.util.List;
import java.util.Scanner;

public class MainMenu {
    private final ClientService clientService;
    private final CarteService carteService;
    private final Scanner scanner;

    public MainMenu() {
        this.clientService = new ClientService();
        this.carteService = new CarteService();
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
            clientService.addClient(client);
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
            System.out.println("3. Gérer une carte");
            System.out.println("4. Voir les détails d'une carte");
            System.out.println("5. Mon profil");
            System.out.println("0. Se déconnecter");
            System.out.print("Votre choix: ");

            String choice = scanner.nextLine();

            try {
                switch (choice) {
                    case "1" -> showMyCards(client);
                    case "2" -> createNewCard(client);
                    case "3" -> manageCard(client);
                    case "4" -> showCardDetails(client);
                    case "5" -> showProfile(client);
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
            List<Carte> cartes = carteService.trouverCartesParClient((long) client.getId());

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
            String cardNumber = generateCardNumber();
            Date expirationDate = Date.valueOf(LocalDate.now().plusYears(3));

            Carte newCard = switch (typeChoice) {
                case "1" -> {
                    System.out.print("Plafond journalier (défaut 1000€): ");
                    String plafondInput = scanner.nextLine();
                    BigDecimal plafond = plafondInput.isEmpty() ?
                        BigDecimal.valueOf(1000) : new BigDecimal(plafondInput);
                    yield new CarteDebit(0, cardNumber, expirationDate,
                        CarteService.STATUS_ACTIVE, client.getId(), plafond);
                }
                case "2" -> {
                    System.out.print("Plafond mensuel (défaut 5000€): ");
                    String plafondInput = scanner.nextLine();
                    BigDecimal plafondMensuel = plafondInput.isEmpty() ?
                        BigDecimal.valueOf(5000) : new BigDecimal(plafondInput);

                    System.out.print("Taux d'intérêt annuel % (défaut 15%): ");
                    String tauxInput = scanner.nextLine();
                    BigDecimal taux = tauxInput.isEmpty() ?
                        BigDecimal.valueOf(15) : new BigDecimal(tauxInput);

                    yield new CarteCredit(0, cardNumber, expirationDate,
                        CarteService.STATUS_ACTIVE, client.getId(), plafondMensuel, taux);
                }
                case "3" -> {
                    System.out.print("Solde initial (défaut 0€): ");
                    String soldeInput = scanner.nextLine();
                    BigDecimal solde = soldeInput.isEmpty() ?
                        BigDecimal.valueOf(0) : new BigDecimal(soldeInput);

                    yield new CartePrepayee(0, cardNumber, expirationDate,
                        CarteService.STATUS_ACTIVE, client.getId(), solde);
                }
                default -> {
                    System.out.println("Type de carte invalide.");
                    yield null;
                }
            };

            if (newCard != null) {
                carteService.creerCarte(newCard);
                System.out.println("Carte créée avec succès!");
                System.out.println("Numéro de carte: " + cardNumber);
            }

        } catch (Exception e) {
            System.out.println("Erreur lors de la création de la carte: " + e.getMessage());
        }
    }

    private void manageCard(Client client) {
        showMyCards(client);

        if (hasNoCards(client)) return;

        System.out.print("\nEntrez l'ID de la carte à gérer: ");
        try {
            long cardId = Long.parseLong(scanner.nextLine());

            // Verify card belongs to client
            List<Carte> userCards = carteService.trouverCartesParClient((long) client.getId());
            boolean cardBelongsToUser = userCards.stream()
                .anyMatch(carte -> carte.getId() == cardId);

            if (!cardBelongsToUser) {
                System.out.println("Cette carte ne vous appartient pas.");
                return;
            }

            System.out.println("\n=== Gestion de la Carte ===");
            System.out.println("1. Activer la carte");
            System.out.println("2. Bloquer la carte");
            System.out.println("3. Suspendre la carte");
            System.out.println("4. Supprimer la carte");
            System.out.println("0. Retour");
            System.out.print("Votre choix: ");

            String action = scanner.nextLine();

            switch (action) {
                case "1" -> {
                    carteService.activerCarte(cardId);
                    System.out.println("Carte activée avec succès!");
                }
                case "2" -> {
                    carteService.bloquerCarte(cardId);
                    System.out.println("Carte bloquée avec succès!");
                }
                case "3" -> {
                    carteService.suspendreCarte(cardId);
                    System.out.println("Carte suspendue avec succès!");
                }
                case "4" -> {
                    System.out.print("Êtes-vous sûr de vouloir supprimer cette carte? (oui/non): ");
                    String confirmation = scanner.nextLine();
                    if ("oui".equalsIgnoreCase(confirmation)) {
                        carteService.supprimerCarte(cardId);
                        System.out.println("Carte supprimée avec succès!");
                    } else {
                        System.out.println("Suppression annulée.");
                    }
                }
                case "0" -> System.out.println("Retour au menu principal.");
                default -> System.out.println("Choix invalide.");
            }

        } catch (NumberFormatException e) {
            System.out.println("ID de carte invalide.");
        } catch (Exception e) {
            System.out.println("Erreur lors de la gestion de la carte: " + e.getMessage());
        }
    }

    private void showCardDetails(Client client) {
        showMyCards(client);

        if (hasNoCards(client)) return;

        System.out.print("\nEntrez l'ID de la carte pour voir les détails: ");
        try {
            long cardId = Long.parseLong(scanner.nextLine());

            var carteOpt = carteService.trouverCarteParId(cardId);
            if (carteOpt.isEmpty()) {
                System.out.println("Carte non trouvée.");
                return;
            }

            Carte carte = carteOpt.get();

            // Verify card belongs to client
            if (carte.getClientId() != client.getId()) {
                System.out.println("Cette carte ne vous appartient pas.");
                return;
            }

            System.out.println("\n=== Détails de la Carte ===");
            System.out.println("ID: " + carte.getId());
            System.out.println("Numéro: " + carte.getNumero());
            System.out.println("Type: " + carte.getClass().getSimpleName());
            System.out.println("Statut: " + carte.getStatus());
            System.out.println("Date d'expiration: " + carte.getDateExpiration());

            // Show type-specific details
            if (carte instanceof CarteDebit debit) {
                System.out.println("Plafond journalier: " + debit.getPlafondJournalier() + "€");
            } else if (carte instanceof CarteCredit credit) {
                System.out.println("Plafond mensuel: " + credit.getPlafondMensuel() + "€");
                System.out.println("Taux d'intérêt: " + credit.getTauxInteret() + "%");
            } else if (carte instanceof CartePrepayee prepayee) {
                System.out.println("Solde disponible: " + prepayee.getSoldeDisponible() + "€");
            }

        } catch (NumberFormatException e) {
            System.out.println("ID de carte invalide.");
        } catch (Exception e) {
            System.out.println("Erreur lors de l'affichage des détails: " + e.getMessage());
        }
    }

    private void showProfile(Client client) {
        System.out.println("\n=== Mon Profil ===");
        System.out.println("ID: " + client.getId());
        System.out.println("Nom: " + client.getNom());
        System.out.println("Email: " + client.getEmail());
        System.out.println("Téléphone: " + client.getTelephone());

        try {
            List<Carte> cartes = carteService.trouverCartesParClient((long) client.getId());
            System.out.println("Nombre de cartes: " + cartes.size());
        } catch (Exception e) {
            System.out.println("Erreur lors du calcul des cartes: " + e.getMessage());
        }
    }

    // Helper methods
    private String generateCardNumber() {
        return "4000" + String.format("%012d", System.currentTimeMillis() % 1000000000000L);
    }

    private String maskCardNumber(String cardNumber) {
        if (cardNumber.length() < 4) return cardNumber;
        return "**** **** **** " + cardNumber.substring(cardNumber.length() - 4);
    }

    private String getCardTypeInfo(Carte carte) {
        if (carte instanceof CarteDebit debit) {
            return " | Plafond: " + debit.getPlafondJournalier() + "€/jour";
        } else if (carte instanceof CarteCredit credit) {
            return " | Plafond: " + credit.getPlafondMensuel() + "€/mois";
        } else if (carte instanceof CartePrepayee prepayee) {
            return " | Solde: " + prepayee.getSoldeDisponible() + "€";
        }
        return "";
    }

    private boolean hasNoCards(Client client) {
        try {
            List<Carte> cartes = carteService.trouverCartesParClient((long) client.getId());
            if (cartes.isEmpty()) {
                System.out.println("Vous n'avez aucune carte pour le moment.");
                return true;
            }
            return false;
        } catch (Exception e) {
            System.out.println("Erreur lors de la vérification des cartes: " + e.getMessage());
            return true;
        }
    }
}
