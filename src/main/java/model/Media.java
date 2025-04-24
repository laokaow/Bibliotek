package model;

public class Media {
    public enum MediaType {
        BOOK,
        DVD,
        JOURNAL,
        NOMEDIA
    }
    private int mediaId;
    private String mediaName;
    private MediaType mediaType;
    private boolean partOfCourse;
    //Funderar ifall medaType skall göras final. Det ska aldrig ändras för ett objekt
    //Visserligen så är det så för all annan data också - Men folk kan göra misstag
    //Måste även ha Category

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

    public MediaType getMediaType(MediaType mediaType) {
        return mediaType;
    }
    public boolean getPartOfCourse(boolean partOfCourse){
        return partOfCourse;
    }

    public void setMediaId(int mediaId) {
        this.mediaId = mediaId;
    }

    public void setMediaName(String mediaName) {
        this.mediaName = mediaName;
    }

    public void setMediaType(MediaType mediaType) {
        this.mediaType = mediaType;
    }
    public void setPartOfCourse(boolean partOfCourse){
        this.partOfCourse = partOfCourse;
    }
}
