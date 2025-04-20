package model;

public class Book extends Media{
    private final String author;
    private final String isbn;
    private final int pageCount;

    public Book(int mediaId, String mediaName, MediaType mediaType, boolean partOfCourse, String author, String isbn, int pageCount){
        super(mediaId, mediaName, mediaType.BOOK, partOfCourse);
        this.author = author;
        this.isbn = isbn;
        this.pageCount = pageCount;
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
}
