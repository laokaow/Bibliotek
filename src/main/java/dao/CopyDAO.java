package dao;

import model.Copy;
import model.Location;
import model.Media;
import util.DatabaseConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class CopyDAO {
    private MediaDAO mediaDAO;
    private final Connection connection;

    public CopyDAO(Connection connection) {
        this.connection = connection;
    }

    public int addNewCopy(Copy copy) throws SQLException {
        String sql = "INSERT INTO Copy (mediaId, referenceCopy, availability) VALUES (?,?,?)";
        try(Connection conn = DatabaseConnection.getConnection();) {
            PreparedStatement ps = conn.prepareStatement(sql);
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
        return -1; // Returnera -1 om inget ID genererades
    }

    public List<Copy> addCopy(Copy copy) throws SQLException {
        String sql = "INSERT INTO Copy (mediaId, referenceCopy, availability) VALUES (?,?,?)";
        return List.of();
    }
    public List<Copy> searchCopiesByMediaName(String mediaName) throws SQLException {
        List<Copy> copies = new ArrayList<>();
        String sql = """
            SELECT c.copyId, c.mediaId, c.referenceCopy, c.availability 
            FROM Copy c
            JOIN Media m ON c.Media_mediaId = m.mediaId
            WHERE LOWER(m.mediaName) LIKE ? AND m.mediaType != 'NOMEDIA'
            """;

        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, "%" + mediaName.toLowerCase() + "%");

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    int copyId = rs.getInt("copyId");
                    int mediaId = rs.getInt("mediaId");
                    boolean referenceCopy = rs.getBoolean("referenceCopy");
                    Copy.AvailabilityStatus availability = Copy.AvailabilityStatus.valueOf(rs.getString("availability"));
                    Media media = mediaDAO.getMediaById(mediaId);
                    Copy copy = new Copy(copyId, media, referenceCopy, availability, null);
                    copies.add(copy);
                }
            }
        }
        return copies;
    }

    public Copy getCopyById(int copyId) throws SQLException {
        String sql = "SELECT * FROM Copy WHERE copyId = ?";
        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, copyId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    int mediaId = rs.getInt("mediaId");
                    boolean referenceCopy = rs.getBoolean("referenceCopy");
                    Copy.AvailabilityStatus availability = Copy.AvailabilityStatus.valueOf(rs.getString("availability"));

                    Media media = mediaDAO.getMediaById(mediaId);
                    return new Copy(copyId, media, referenceCopy, availability, null);
                }
            }
        }
        return null;
    }

    public List<Copy> getCopiesByMediaId(int mediaId) throws SQLException {
        String sql = "SELECT * FROM Copy WHERE mediaId = ?";
        List<Copy> copies = new ArrayList<>();
        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, mediaId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Media media = mediaDAO.getMediaById(mediaId);
                    Copy copy = new Copy(
                            rs.getInt("copyId"),
                            media,
                            rs.getBoolean("referenceCopy"),
                            Copy.AvailabilityStatus.valueOf(rs.getString("availability")),
                            null
                    );
                    copies.add(copy);
                }
            }
        }
        return copies;
    }

}
