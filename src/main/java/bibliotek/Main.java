package bibliotek;

import model.User;
import dao.UserDAO;
import util.DatabaseConnection;

import java.sql.*;
import io.github.cdimascio.dotenv.Dotenv;

public class Main {
    public static void main(String[] args) {

        Dotenv dotenv = Dotenv.load();
        String url = dotenv.get("DB_URL");
        String user = dotenv.get("DB_USER");
        String password = dotenv.get("DB_PASSWORD");

        try (Connection connection = DatabaseConnection.getConnection()){
            System.out.println("Connected to Aiven!");

            UserDAO userDAO = new UserDAO(connection);
            User retrievedUser = userDAO.getUserById(1);

            if (user != null){
                System.out.println("User ID: " + retrievedUser.getUserId());
                System.out.println("User Email: " + retrievedUser.getEmail());
                }
            else{
                System.out.println("User not found!");
            }
        } catch(SQLException e) {
            System.err.println("Connection Failed!");
            e.printStackTrace();
        }
    }
}