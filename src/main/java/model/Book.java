package model;

import java.util.ArrayList;
import java.util.List;

public class Book extends Media{
    private final String author;
    private final String isbn;
    private final int pageCount;
    List<Category> category = new ArrayList<Category>();

    public Book(int mediaId, String mediaName, MediaType mediaType, boolean partOfCourse, String author, String isbn, int pageCount, List<Category> category){
        super(mediaId, mediaName, mediaType.BOOK, partOfCourse);
        this.author = author;
        this.isbn = isbn;
        this.pageCount = pageCount;
        this.category = category;
    }

    public void setCategories(List<Category> category) {
        this.category = category;
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
    public List<Category> getCategory(){
        return category;
    }
    public void addCategory(Category category){
        if(category != null && this.category.stream().noneMatch(c -> c.getCategoryId() == category.getCategoryId())) {
            this.category.add(category);
        }
    } //Borde dokumentera tydligare, minns inte varför denna addCategory gjordes. Kan det vara för att det är en ArrayList?
}