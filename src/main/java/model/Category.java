package model;

//IDEn rekommenderar record class - värt att överväga
public class Category {
    private final int categoryId;
    private final String categoryName;

    public Category(int categoryId, String categoryName){
        this.categoryId = categoryId;
        this.categoryName = categoryName;
    }
    public int getCategoryId(){
        return categoryId;
    }
    public String getCategoryName(){
        return categoryName;
    }
}
