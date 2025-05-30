package model;

import java.util.ArrayList;
import java.util.List;


public class Dvd extends Media{
    private final int ageLimit;
    private final String productionCountry;
    private final String director;
    private final int duration;
    List<Actor> actor = new ArrayList<>();
    List<Category> category = new ArrayList<>();

    public Dvd(int mediaId, String mediaName, MediaType mediaType, boolean partOfCourse, int ageLimit, String productionCountry, String director, int duration, List<Actor> actor, List<Category> category) {
        super(mediaId, mediaName, mediaType.DVD, partOfCourse);
        this.ageLimit = ageLimit;
        this.productionCountry = productionCountry;
        this.director = director;
        this.duration = duration;
        this.actor = actor;
        this.category = category;


    }

    public Dvd(int mediaId, String mediaName, MediaType mediaType, boolean partOfCourse, int ageLimit, String productionCountry, String director, int duration) {
        super(mediaId, mediaName, MediaType.DVD, partOfCourse);
        this.ageLimit = ageLimit;
        this.productionCountry = productionCountry;
        this.director = director;
        this.duration = duration;
        this.actor = new ArrayList<>();
        this.category = new ArrayList<>();
    }

    public int getAgeLimit(){
        return ageLimit;
    }

    public String getProductionCountry(){
        return productionCountry;
    }
    public String getDirector(){
        return director;
    }
    public int getDuration(){
        return duration;
    }
    public List<Actor> getActor(){
        return actor;
    }
    public List<Category> getCategory(){
        return category;
    }
    public void setActor(List<Actor> actor){
        this.actor = actor;
    }
    public void setCategory(List<Category> category){
        this.category = category;
    }

    // Lägger till unika skådespelare i en enskild dvd. Dvs inga dubletter av skådespelare i samma dvd
    public void addActor(Actor actor){
        if(this.getActor().stream().noneMatch(a -> a.getActorId() == actor.getActorId())) {
            this.getActor().add(actor);
        }
    }
    // Egentligen skulle denna metod kunna ligga i media.java, eftersom flera mediaTyper har kategorier..
    // Men Journal har inte några kategorier. Sedan känns det inte så farligt att ha samma metod i två klasser.
    //Speciellt när metoden är 2 rader lång.
    // Lägger till unika kategorier till en dvd, och ser till att det inte blir dubbletter.
    public void addCategory(Category category){
        if(this.getCategory().stream().noneMatch(c -> c.getCategoryId() == category.getCategoryId())) {
            this.getCategory().add(category);
        }
    }

}