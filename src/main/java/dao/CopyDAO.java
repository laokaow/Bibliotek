package dao;

import model.Copy;
import model.Media;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CopyDAO {
    private final Connection connection;
    private final MediaDAO mediaDAO;

    public CopyDAO(Connection connection) {
        this.connection = connection;
        this.mediaDAO = new MediaDAO(connection);
    }

    public int addNewCopy(Copy copy) throws SQLException {
        String sql = "INSERT INTO Copy (mediaId, referenceCopy, availability) VALUES (?,?,?)";
        try (PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, copy.getMediaId());
            ps.setBoolean(2, copy.isReferenceCopy());
            ps.setString(3, copy.getAvailability().name());

            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        }
        return -1;
    }

    public List<Copy> addCopies(int mediaId, int count, boolean referenceCopy) throws SQLException {
        List<Copy> addedCopies = new ArrayList<>();
        String sql = "INSERT INTO Copy (mediaId, referenceCopy, availability) VALUES (?, ?, ?)";
        try (PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            for (int i = 0; i < count; i++) {
                ps.setInt(1, mediaId);
                ps.setBoolean(2, referenceCopy);
                ps.setString(3, Copy.AvailabilityStatus.AVAILABLE.name());
                ps.addBatch();
            }
            ps.executeBatch();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                Media media = mediaDAO.getMediaById(mediaId);
                while (rs.next()) {
                    int copyId = rs.getInt(1);
                    Copy copy = new Copy(copyId, media, referenceCopy, Copy.AvailabilityStatus.AVAILABLE, null);
                    addedCopies.add(copy);
                }
            }
        }
        return addedCopies;
    }

    public List<Copy> searchCopiesByMediaName(String mediaName) throws SQLException {
        List<Copy> copies = new ArrayList<>();
        String sql = """
            SELECT c.copyId, c.mediaId, c.referenceCopy, c.availability 
            FROM Copy c
            JOIN Media m ON c.mediaId = m.mediaId
            WHERE LOWER(m.mediaName) LIKE ? AND m.mediaType != 'NOMEDIA'
            """;

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, "%" + mediaName.toLowerCase() + "%");

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    copies.add(mapResultSetToCopy(rs));
                }
            }
        }
        return copies;
    }

    public Copy getCopyById(int copyId) throws SQLException {
        String sql = "SELECT * FROM Copy WHERE copyId = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, copyId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToCopy(rs);
                }
            }
        }
        return null;
    }

    public List<Copy> getCopiesByMediaId(int mediaId) throws SQLException {
        String sql = "SELECT * FROM Copy WHERE mediaId = ?";
        List<Copy> copies = new ArrayList<>();
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, mediaId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    copies.add(mapResultSetToCopy(rs));
                }
            }
        }
        return copies;
    }
    public void updateAvailability(int copyId, Copy.AvailabilityStatus availability) throws SQLException {
        String sql = "UPDATE Copy SET availability = ? WHERE copyId = ? AND referenceCopy = FALSE";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, availability.name());
            ps.setInt(2, copyId);
            int updated = ps.executeUpdate();
            if (updated == 0) {
                throw new SQLException("Kopian kunde inte uppdateras – kontrollera att det inte är en referenskopia.");
            }
        }
    }

    private Copy mapResultSetToCopy(ResultSet rs) throws SQLException {
        int copyId = rs.getInt("copyId");
        int mediaId = rs.getInt("mediaId");
        boolean referenceCopy = rs.getBoolean("referenceCopy");
        Copy.AvailabilityStatus availability = Copy.AvailabilityStatus.valueOf(rs.getString("availability"));
        Media media = mediaDAO.getMediaById(mediaId);
        return new Copy(copyId, media, referenceCopy, availability, null);
    }
}
