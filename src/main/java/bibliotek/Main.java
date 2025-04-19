package bibliotek;

import java.sql.*;
import io.github.cdimascio.dotenv.Dotenv;

public class Main {
    public static void main(String[] args) {

        Dotenv dotenv = Dotenv.load();

       String url = dotenv.get("DB_URL");
       String user = dotenv.get("DB_USER");
       String password = dotenv.get("DB_PASSWORD");

        System.out.println("URL: " + url);
        System.out.println("USERNAME: " + user);
        System.out.println("PASSWORD: " + password);

       try (Connection conn = DriverManager.getConnection(url, user, password)){
           System.out.println("Connected to Aiven!");

           String sql = "SELECT * FROM Media";
           try(Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(sql)){
    while (rs.next()){
        System.out.println(rs.getInt("mediaId") + " | " + rs.getString("mediaName") + " | " + rs.getString("mediaType"));

    }
           }
       } catch(SQLException e) {
           System.err.println("Connection Failed!");
           e.printStackTrace();
       }
    }
}