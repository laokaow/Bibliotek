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
    private  MediaType mediaType;
    //Funderar ifall medaType skall göras final. Det ska aldrig ändras för ett objekt
    //Visserligen så är det så för all annan data också - Men folk kan göra misstag
    //Måste även ha Category

    public Media(int mediaId, String mediaName, MediaType mediaType){
        this.mediaId = mediaId;
        this.mediaName = mediaName;
        this.mediaType = mediaType;
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

    public void setMediaId(int mediaId) {
        this.mediaId = mediaId;
    }

    public void setMediaName(String mediaName) {
        this.mediaName = mediaName;
    }

    public void setMediaType(MediaType mediaType) {
        this.mediaType = mediaType;
    }
}
