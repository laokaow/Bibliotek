package model;

public class Dvd extends Media{
    private final int ageLimit;
    private final String productionCountry;
    private final String director;
    private final int duration;

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

}
