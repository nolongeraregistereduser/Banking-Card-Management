package ui;

import service.ClientService;
import entity.Client;
import java.util.Scanner;

public class MainMenu {
    private final ClientService clientService;
    private final Scanner scanner;

    public MainMenu() {
        this.clientService = new ClientService();
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
            // You can add more menu options here for logged-in users
        } catch (Exception e) {
            System.out.println("Erreur de connexion: " + e.getMessage());
        }
    }
}

