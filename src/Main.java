import util.DBUtil;
import service.ClientService;
import entity.Client;
import java.sql.Connection;
import java.sql.SQLException;

public class Main {
    public static void main(String[] args) {
        // Test database connection
        try (Connection conn = DBUtil.getConnection()) {
            System.out.println("Connection successful!");
        } catch (SQLException e) {
            System.out.println("Connection failed: " + e.getMessage());
            return;
        }

        // Test Client functionality
        ClientService clientService = new ClientService();

        try {
            // Test 1: Add a new client
            Client newClient = new Client(0, "John Doe", "john@email.com", "123456789");
            clientService.addClient(newClient);

            // Test 2: Display all clients
            clientService.displayAllClients();

            // Test 3: Find client by ID (adjust ID based on your database)
            Client foundClient = clientService.findClientById(1);
            if (foundClient != null) {
                System.out.println("Found client: " + foundClient);
            }

        } catch (SQLException e) {
            System.out.println("Database error: " + e.getMessage());
        } catch (IllegalArgumentException e) {
            System.out.println("Validation error: " + e.getMessage());
        }
    }
}