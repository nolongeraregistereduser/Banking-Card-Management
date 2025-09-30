import util.DBUtil;
import service.ClientService;
import entity.Client;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        // Test database connection
        try (Connection conn = DBUtil.getConnection()) {
            System.out.println("Connection successful!");
        } catch (SQLException e) {
            System.out.println(" Connection failed: " + e.getMessage());
            return;
        }

        // Test Client functionality
        ClientService clientService = new ClientService();
        System.out.println("\n=== TESTING CLIENT SERVICE & DAO ===\n");

        try {
            // Test 1: CREATE - Add new clients
            System.out.println("1. Testing CREATE operations:");
            Client client1 = new Client(0, "Alice Martin", "alice@email.com", "123456789");
            Client client2 = new Client(0, "Bob Dupont", "bob@email.com", "987654321");
            Client client3 = new Client(0, "Claire Bernard", "claire@email.com", "555666777");

            clientService.addClient(client1);
            clientService.addClient(client2);
            clientService.addClient(client3);
            System.out.println("✓ Successfully added 3 clients\n");

            // Test 2: READ - Display all clients
            System.out.println("2. Testing READ ALL operations:");
            clientService.displayAllClients();
            System.out.println();

            // Test 3: READ - Find client by ID
            System.out.println("3. Testing READ BY ID operations:");
            List<Client> allClients = clientService.getAllClients();
            if (!allClients.isEmpty()) {
                int firstClientId = allClients.get(0).getId();
                Client foundClient = clientService.findClientById(firstClientId);
                if (foundClient != null) {
                    System.out.println("✓ Found client by ID " + firstClientId + ": " + foundClient);
                } else {
                    System.out.println("✗ Client not found by ID " + firstClientId);
                }
            }
            System.out.println();

            // Test 4: UPDATE - Modify a client
            System.out.println("4. Testing UPDATE operations:");
            if (!allClients.isEmpty()) {
                Client clientToUpdate = allClients.get(0);
                Client updatedClient = new Client(
                    clientToUpdate.getId(),
                    "Alice Martin-Updated",
                    "alice.updated@email.com",
                    "111222333"
                );
                clientService.updateClient(updatedClient);

                // Verify the update
                Client verifyClient = clientService.findClientById(clientToUpdate.getId());
                if (verifyClient != null) {
                    System.out.println("✓ Client updated successfully: " + verifyClient);
                }
            }
            System.out.println();

            // Test 5: READ - Display all clients after update
            System.out.println("5. Testing READ ALL after UPDATE:");
            clientService.displayAllClients();
            System.out.println();

            // Test 6: DELETE - Remove a client
            System.out.println("6. Testing DELETE operations:");
            allClients = clientService.getAllClients();
            if (allClients.size() > 1) {
                int clientIdToDelete = allClients.get(allClients.size() - 1).getId();
                clientService.removeClient(clientIdToDelete);

                // Verify deletion
                Client deletedClient = clientService.findClientById(clientIdToDelete);
                if (deletedClient == null) {
                    System.out.println("✓ Client deleted successfully (ID: " + clientIdToDelete + ")");
                } else {
                    System.out.println("✗ Client deletion failed");
                }
            }
            System.out.println();

            // Test 7: Final state - Display remaining clients
            System.out.println("7. Final state - All remaining clients:");
            clientService.displayAllClients();
            System.out.println();

            // Test 8: Error handling - Invalid operations
            System.out.println("8. Testing error handling:");

            // Test invalid client ID
            try {
                clientService.findClientById(-1);
                System.out.println("✗ Should have thrown exception for negative ID");
            } catch (IllegalArgumentException e) {
                System.out.println("✓ Correctly handled negative ID: " + e.getMessage());
            }

            // Test null client
            try {
                clientService.addClient(null);
                System.out.println("✗ Should have thrown exception for null client");
            } catch (IllegalArgumentException e) {
                System.out.println("✓ Correctly handled null client: " + e.getMessage());
            }

            // Test empty name
            try {
                Client invalidClient = new Client(0, "", "test@email.com", "123456789");
                clientService.addClient(invalidClient);
                System.out.println("✗ Should have thrown exception for empty name");
            } catch (IllegalArgumentException e) {
                System.out.println("✓ Correctly handled empty name: " + e.getMessage());
            }

            System.out.println("\n=== ALL CLIENT TESTS COMPLETED SUCCESSFULLY ===");

        } catch (SQLException e) {
            System.out.println("✗ Database error: " + e.getMessage());
            e.printStackTrace();
        } catch (Exception e) {
            System.out.println("✗ Unexpected error: " + e.getMessage());
            e.printStackTrace();
        }
    }
}