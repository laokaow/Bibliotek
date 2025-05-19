package model;

public class Media {
    public enum MediaType {
        BOOK,
        DVD,
        JOURNAL,
        NOMEDIA
    }
    private final int mediaId;
    private final String mediaName;
    private final MediaType mediaType;
    private boolean partOfCourse;
    //Funderar ifall medaType skall göras final. Det ska aldrig ändras för ett objekt
    //Visserligen så är det så för all annan data också - Men folk kan göra misstag
    //Måste även ha Category
    //När jag gör de final så kan man INTE använda setters. Makes sense då de aldrig ska uppdateras, enbart konstrueras.

    public Media(int mediaId, String mediaName, MediaType mediaType, boolean partOfCourse){
        this.mediaId = mediaId;
        this.mediaName = mediaName;
        this.mediaType = mediaType;
        this.partOfCourse = partOfCourse;
    }

    public int getMediaId() {
        return mediaId;
    }

    public String getMediaName() {
        return mediaName;
    }

    public MediaType getMediaType() {
        return mediaType;
    }

    public boolean getPartOfCourse(){
        return partOfCourse;
    }

    public void setPartOfCourse(boolean partOfCourse){
        this.partOfCourse = partOfCourse;
    }

}


/* Gamla setters som jag inte har tagit bort för att det är jobbigt att skriva igen.
    public void setMediaId(int mediaId) {
        this.mediaId = mediaId;
    }

    public void setMediaName(String mediaName) {
        this.mediaName = mediaName;
    }

    public void setMediaType(MediaType mediaType) {
        this.mediaType = mediaType;
    } **/


