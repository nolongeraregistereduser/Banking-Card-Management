import util.DBUtil;
import service.ClientService;
import entity.Client;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import ui.MainMenu;

public class Main {
    public static void main(String[] args) {
        MainMenu menu = new MainMenu();
        menu.start();
    }
}

