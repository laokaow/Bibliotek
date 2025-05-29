package model;


public class Actor {
    private final int actorId;
    private final String firstName;
    private final String lastName;


    public Actor(int actorId, String firstName, String lastName){
        this.actorId = actorId;
        this.firstName = firstName;
        this.lastName = lastName;
    }

    public int getActorId(){
        return actorId;
    }
    public String getFirstName(){
        return firstName;
    }
    public String getLastName(){
        return lastName;
    }
    public String getName(){
        return firstName + " " + lastName;
    }

}
