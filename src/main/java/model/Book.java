package model;

public class Book extends Media{
    private String author;
    private String isbn;
    private int pageCount;
    private boolean partOfCourse;

    public Book(int mediaId, String mediaName, MediaType mediaType, String author, String isbn, int pageCount, boolean partOfCourse){
        super(mediaId, mediaName, mediaType.BOOK);
        this.author = author;
        this.isbn = isbn;
        this.pageCount = pageCount;
        this.partOfCourse = partOfCourse;
    }

    public String getAuthor() {
        return author;
    }
    public String getIsbn(){
        return isbn;
    }
    public int getPageCount(){
        return pageCount;
    }
    public boolean getPartOfCourse(){
        return partOfCourse;
    }
}
