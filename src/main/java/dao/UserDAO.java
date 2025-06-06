package dao;

import model.User;
import model.User.UserType;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import util.PasswordUtil;

public class UserDAO {
    private final Connection connection;

    public UserDAO(Connection connection) throws SQLException {
        this.connection = connection;
    }

    public int getMaxLoanCount(String userType) throws SQLException { //Färdig funktion i MySQL som räknar ut lånmängden beroende på användartyp
        String sql = "SELECT getMaxLoanCount(?)";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, userType);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) return rs.getInt(1);
            else throw new SQLException("getMaxLoanCount returnerade inget.");
        }
    }
    public User authenticateUser(String email, String password) {
        String sql = "SELECT * FROM Users WHERE email = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, email);

            try (ResultSet rs = stmt.executeQuery()) {
                if(rs.next()){
                    String storedHashedPassword = rs.getString("pinCode");

                    if(PasswordUtil.checkPassword(password, storedHashedPassword)){
                        return new User(
                                rs.getInt("userId"),
                                rs.getString("firstName"),
                                rs.getString("lastName"),
                                rs.getString("email"),
                                rs.getString("phoneNumber"),
                                rs.getString("pinCode"),
                                User.UserType.valueOf(rs.getString("userType").toUpperCase())
                        );
                    }
                }

            }
            } catch(SQLException e){
            e.printStackTrace(); //Bättre loggning hade kunnat användas. Men vi kikar på det när vi börjar bli klara med projektet.
        }return null;
    }
    public User createUser(User user){

        String hashedPinCode = PasswordUtil.hashPassword(user.getPinCode()); //Kallar på metoden som hashar pinCode. Lagrar i variabel

        String sql = "INSERT INTO Users (firstName, lastName, email, phoneNumber, pinCode, userType) VALUES (?, ?, ?, ?, ?, ?)";

        try(PreparedStatement stmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)){
            stmt.setString(1, user.getFirstName());
            stmt.setString(2, user.getLastName());
            stmt.setString(3, user.getEmail());
            stmt.setString(4, user.getPhoneNumber());
            stmt.setString(5, hashedPinCode); //Lagrar det hashade lösenordet i databasen
            stmt.setString(6, user.getUserType().name());

            int rowsAffected = stmt.executeUpdate();

            if(rowsAffected > 0 ) {
                try (ResultSet generatedKeys = stmt.getGeneratedKeys()){
                    if(generatedKeys.next()){
                        int generatedId = generatedKeys.getInt(1);
                        return new User(
                                generatedId,
                                user.getFirstName(),
                                user.getLastName(),
                                user.getEmail(),
                                user.getPhoneNumber(),
                                hashedPinCode, //Det nya User-objektet får det hashade lösenordet
                                user.getUserType()
                        );
                    }
                }
            }

        } catch(SQLException e){
            e.printStackTrace();
        }
        return null;
    }

    public boolean deleteUser(int userId){
        String sql = "DELETE FROM Users WHERE userId = ?";
        try(PreparedStatement stmt = connection.prepareStatement(sql)){
            stmt.setInt(1, userId);

            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        }
        catch (SQLException e){
            e.printStackTrace();
            return false;
        }
    }
    public boolean deleteUser(User user) { // En overloaded metod med samma namn. Detta för att kunna ta bort en användare både med ID och som objekt
        if (user == null) {
            throw new IllegalArgumentException("User cannot be null");
        }
        return deleteUser(user.getUserId());
    }

    public User getUserById(int userId) {
        User user = null;
        String sql = "SELECT * FROM Users WHERE userId = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, userId);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    user = new User(
                            rs.getInt("userId"),
                            rs.getString("firstName"),
                            rs.getString("lastName"),
                            rs.getString("email"),
                            rs.getString("phoneNumber"),
                            rs.getString("pinCode"), //Viktigt här att inte visa eller skriva ut då det här är ett hashat värde.
                            User.UserType.valueOf(rs.getString("userType").toUpperCase())
                    );
                }
            }
            } catch (SQLException e){
            e.printStackTrace();
        }
        return user;
        }

        public boolean updateEmail(String email, int userId) {

            String sql = "UPDATE Users SET email = ? WHERE userId = ?";
            try (PreparedStatement stmt = connection.prepareStatement(sql)){
                stmt.setString(1, email);
                stmt.setInt(2, userId);

                int rowsAffected = stmt.executeUpdate(); //Metoden som kör uppdateringen
                return rowsAffected > 0;

            } catch(SQLException e){
                e.printStackTrace();
                return false;
            }
        }

        public boolean updateUserType(UserType userType, int userId) {

        String sql = "UPDATE Users SET userType = ? WHERE userId = ?";
        try(PreparedStatement stmt = connection.prepareStatement(sql)){
            stmt.setString(1, userType.name()); //Konverterar enum till en String men byter inte versaler
            stmt.setInt(2, userId);

            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        }
        catch(SQLException e){
            e.printStackTrace();
            return false;
        }
    }

        public boolean updatePhoneNumber(String phoneNumber, int userId){

            String sql = "UPDATE Users SET phoneNumber = ? WHERE userId = ?";

                try(PreparedStatement stmt = connection.prepareStatement(sql)){
                    stmt.setString(1, phoneNumber);
                    stmt.setInt(2, userId);

                    int rowsAffected = stmt.executeUpdate();
                    return rowsAffected > 0;
                }
                catch(SQLException e){
                    e.printStackTrace();
                    return false;
        }
    }
    public List<User> getAllUsers() {
        List<User> users = new ArrayList<>();
        String sql = "SELECT userId, firstName, lastName, email, phoneNumber, userType FROM Users";

        try (PreparedStatement stmt = connection.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                User user = new User(
                        rs.getInt("userId"),
                        rs.getString("firstName"),
                        rs.getString("lastName"),
                        rs.getString("email"),
                        rs.getString("phoneNumber"),
                        null, // pinCode ska inte visas
                        UserType.valueOf(rs.getString("userType"))
                );
                users.add(user);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return users;
    }

    public boolean updatePinCode(String pinCode, int userId){
        String hashedPinCode = PasswordUtil.hashPassword(pinCode); //Hämtar
        String sql = "UPDATE Users SET pinCode = ? WHERE userId = ?";

        try(PreparedStatement stmt = connection.prepareStatement(sql)){
            stmt.setString(1, hashedPinCode);
            stmt.setInt(2, userId);

            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        }
        catch(SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}




//En tanke var att hantera all update-logik i en generell update-metod, men detta kändes väldigt komplext att hantera så separata update-metoder valdes istället.
//Try-with-resources ser till att statement stängs automatiskt efteråt, både vid rätt och fel. Minskar risken för memory leaks eller låsningar
//En tanke som kom upp var hur programmet ska hantera userID vid ex. uppdatering. Efter lite efterforskning så spelar det ingen roll. userId lagras som en global variabel e.g. i MainController.
//int userId = LoggedInUser.getId();