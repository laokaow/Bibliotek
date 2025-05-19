package dao;

import model.User;

import java.sql.*;
import java.time.LocalDateTime;

public class ReceiptDAO {
    private final Connection connection;

    public ReceiptDAO(Connection connection) {
        this.connection = connection;
    }

    public int createReceipt(User user, String info, LocalDateTime dateTime) throws SQLException {
        String sql = "INSERT INTO Receipt(userId, info, receiptDate) VALUES (?, ?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setInt(1, user.getUserId());
            stmt.setString(2, info);
            stmt.setTimestamp(3, Timestamp.valueOf(dateTime));

            int affectedRows = stmt.executeUpdate();
            if (affectedRows == 0) throw new SQLException("Kvitto kunde inte skapas.");

            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    return generatedKeys.getInt(1);
                } else {
                    throw new SQLException("Ingen receiptId genererades.");
                }
            }
        }
    }
}