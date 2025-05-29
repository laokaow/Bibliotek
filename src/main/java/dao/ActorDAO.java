package dao;

import model.Actor;
import util.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ActorDAO {
    List<Actor> actorList;
    private Connection connection;

    public ActorDAO(Connection connection){
        this.connection = connection;
    }

    public void  SaveActors() {
        actorList = getActors();
    }
    //Nedan övervägas vilket connection som bör användas. Rimligast är nog den ovan deklarerade variabeln
    public List<Actor> getActors() {
        List<Actor> actors = new ArrayList<Actor>();
        String sql = "SELECT * FROM Actor";
        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()
        ) {
            while (rs.next()) {
                int actorId = rs.getInt("actorId");
                String firstName = rs.getString("firstName");
                String lastName = rs.getString("lastName");

                Actor actor = new Actor(
                        actorId,
                        firstName,
                        lastName
                );
                actors.add(actor);
            }


        } catch (Exception e) {
            e.printStackTrace();
        }


        return actors;
    }

    public List<Actor> getActorsByMediaId(int mediaId) throws SQLException {
        List<Actor> actorList = new ArrayList<Actor>();
        String sql = "SELECT a.actorId, a.firstName, a.lastName " +
                "FROM Actor a " +
                "JOIN MediaActor ma ON a.actorId = ma.actorId " +
                "WHERE ma.mediaId = ?";
        try (Connection con = DatabaseConnection.getConnection()) {
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setInt(1, mediaId);
            ResultSet rs = ps.executeQuery();

           while (rs.next()) {
               int actorId = rs.getInt("actorId");
               String firstName = rs.getString("firstName");
               String lastName = rs.getString("lastName");

               Actor actor = new Actor(
                       actorId,
                       firstName,
                       lastName
               );
               actorList.add(actor);
           }

        }
        catch(SQLException e) {
            e.printStackTrace();
        }
        return actorList;
    }

    public List<Actor> searchActors(String firstName, String lastName) throws SQLException {
        List<Actor> searchedAuthor = new ArrayList<>();
        for(Actor actor: actorList ) {
            if(actor.getFirstName().equals(firstName) && actor.getLastName().equals(lastName)) {
                searchedAuthor.add(actor);
            }
        }
        return searchedAuthor;
    }

    public Actor findByName(String firstName, String lastName) throws SQLException {
        String sql = "SELECT actorId, firstName, lastName FROM Actor WHERE firstName = ? AND lastName = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, capitalize(firstName));
            stmt.setString(2, capitalize(lastName));
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return new Actor(rs.getInt("actorId"), rs.getString("firstName"), rs.getString("lastName"));
                } else {
                    return null;
                }
            }
        }
    }

    // Hjälpmetod för att säkerställa stor första bokstav
    private String capitalize(String str) {
        if (str == null || str.isEmpty()) return str;
        return str.substring(0, 1).toUpperCase() + str.substring(1).toLowerCase();
    }
    public void addActorToMedia(int mediaId, String actorName) throws SQLException {
        String sql = "INSERT INTO MediaActor (mediaId, actorName) VALUES (?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, mediaId);
            stmt.setString(2, actorName);
            stmt.executeUpdate();
        }
    }
    public int addActor(Actor actor) throws SQLException {
        String sql = "INSERT INTO Actor (firstName, lastName) VALUES (?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, capitalize(actor.getFirstName()));
            stmt.setString(2, capitalize(actor.getLastName()));
            stmt.executeUpdate();

            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    return rs.getInt(1);
                } else {
                    throw new SQLException("Misslyckades hämta genererat actorId.");
                }
            }
        }
    }

    public void addActorToMedia(int mediaId, int actorId) throws SQLException {
        String sql = "INSERT INTO MediaActor (mediaId, actorId) VALUES (?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, mediaId);
            stmt.setInt(2, actorId);
            stmt.executeUpdate();
        }
    }

    //Nästan samma som addActor. Returnerar annat dock
    public Actor addActorAndReturn(Actor actor) throws SQLException {
        String sql = "INSERT INTO Actor (firstName, lastName) VALUES (?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, capitalize(actor.getFirstName()));
            stmt.setString(2, capitalize(actor.getLastName()));
            stmt.executeUpdate();

            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    int actorId = rs.getInt(1);
                    return new Actor(actorId, actor.getFirstName(), actor.getLastName());
                } else {
                    throw new SQLException("Misslyckades hämta genererat actorId.");
                }
            }
        }
    }
}