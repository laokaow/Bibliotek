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

    public Dvd(int mediaId, String mediaName, MediaType mediaType, boolean partOfCourse, int ageLimit, String productionCountry, String director, int duration){
        super(mediaId, mediaName, mediaType.DVD, partOfCourse);
        this.ageLimit = ageLimit;
        this.productionCountry = productionCountry;
        this.director = director;
        this.duration = duration;


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

}
