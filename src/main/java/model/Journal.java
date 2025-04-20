package model;

public class Journal extends Media{
    private final String issueNumber;

    public Journal(int mediaId, String mediaName, MediaType mediaType, boolean partOfCourse, String issueNumber){
        super(mediaId, mediaName, mediaType.JOURNAL, partOfCourse);
        this.issueNumber = issueNumber;
    }
    public String getIssueNumber(){
        return issueNumber;
    }

}
