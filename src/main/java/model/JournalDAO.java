package model;

import util.DatabaseConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class JournalDAO {


    public List<Journal> getAllJournals() {
        List<Journal> journals = new ArrayList<Journal>();
        String sql = "select * from Media where mediaType = 'journal' ";

        try(Connection con = DatabaseConnection.getConnection();
            PreparedStatement stmt = con.prepareStatement(sql);
        )
        {
            ResultSet rs = stmt.executeQuery();

            while(rs.next()) {
                int mediaId = rs.getInt("mediaId");
                String mediaName = rs.getString("mediaName");
                String mediaType = String.valueOf(Media.MediaType.valueOf(rs.getString("mediaType").toUpperCase()));
                boolean partOfCourse = rs.getBoolean("partOfCourse");
                String issueNumber = rs.getString("issueNumber");

            }

        }
        catch(SQLException e) {
            e.printStackTrace();
        }
        return journals;
    }
    public void addJournal(Journal journal) throws SQLException {
        String sql = "INSERT INTO Media (mediaName, mediaType, partOfCourse, issueNumber VALUES (?,?,?,?)";

        try(Connection con = DatabaseConnection.getConnection();
            PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, journal.getMediaName());
            ps.setString(2, journal.getMediaType().toString());
            ps.setBoolean(3, journal.getPartOfCourse());
            ps.setString(4, journal.getIssueNumber());

            ps.executeUpdate();
        }
        catch(SQLException e) {
            e.printStackTrace();

        }
    }
}
