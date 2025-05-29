package dao;

import model.Journal;
import model.Media;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class JournalDAO {
    private final Connection connection;

    public JournalDAO(Connection connection) {
        this.connection = connection;
    }

    public List<Journal> getAllJournals() {
        List<Journal> journals = new ArrayList<>();
        String sql = "SELECT * FROM Media WHERE mediaType = 'journal'";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                int mediaId = rs.getInt("mediaId");
                String mediaName = rs.getString("mediaName");
                boolean partOfCourse = rs.getBoolean("partOfCourse");
                String issueNumber = rs.getString("issueNumber");

                Journal journal = new Journal(mediaId, mediaName, Media.MediaType.JOURNAL, partOfCourse, issueNumber);
                journals.add(journal);
            }
        } catch (SQLException e) {
            System.err.println("Fel vid hämtning av journals: " + e.getMessage());
        }
        return journals;
    }

    public void updateJournal(Journal journal) throws SQLException {
        String sql = "UPDATE Media SET partOfCourse = ? WHERE mediaId = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setBoolean(1, journal.getPartOfCourse());
            stmt.setInt(2, journal.getMediaId());
            stmt.executeUpdate();
        }
    }

    public void addJournal(Journal journal) {
        String sql = "INSERT INTO Media (mediaName, mediaType, partOfCourse, issueNumber) VALUES (?, ?, ?, ?)";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, journal.getMediaName());
            ps.setString(2, journal.getMediaType().toString());
            ps.setBoolean(3, journal.getPartOfCourse());
            ps.setString(4, journal.getIssueNumber());
            ps.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Fel vid tillägg av journal: " + e.getMessage());
        }
    }
}