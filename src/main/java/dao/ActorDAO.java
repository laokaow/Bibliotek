package dao;

import model.Actor;
import util.DatabaseConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ActorDAO {
    List<Actor> actorList;

    public void  SaveActors() {
        actorList = getActors();
    }
    public List<Actor> getActors() {
        List<Actor> actors = new ArrayList<Actor>();
        String sql = "SELECT * FROM Actor";
        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()
        ) {
            while (rs.next()) {
                int actorId = rs.getInt("id");
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
    public List<Actor> searchAuthor(String firstName, String lastName) throws SQLException {
        List<Actor> searchedAuthor = new ArrayList<>();
        String sql = "SELECT * FROM Actor WHERE firstName = ? AND lastName = ?";
        try (Connection con = DatabaseConnection.getConnection()) {
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setString(1, firstName);
            ps.setString(2, lastName);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                int actorId = rs.getInt("actorId");
                String fName = rs.getString("firstName");
                String lName = rs.getString("lastName");

                Actor actor = new Actor(
                        actorId,
                        fName,
                        lName
                );
                searchedAuthor.add(actor);

            }
        }
        return searchedAuthor;

    }

    public Actor addActor(Actor actor) throws SQLException {

        String sql = "INSERT INTO Actor (firstName, lastName) VALUES (?, ?)";
        try(Connection con = DatabaseConnection.getConnection();
        PreparedStatement ps = con.prepareStatement(sql)
        )
        {
            ps.setString(1, actor.getFirstName());
            ps.setString(2, actor.getLastName());
            ps.executeUpdate();
        }
        catch(SQLException e) {
            e.printStackTrace();
            System.out.println("Error adding actor, try again");
        }
        return actor;
    }
}
