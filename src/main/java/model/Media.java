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
