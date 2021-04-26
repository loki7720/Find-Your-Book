package com.example.booksharing;

public class BooksModel {
    private String BookID,BookTitle,Author,Edition,ISBN,PostedBy,PostedDate,Category,Condition,Price,BookImage;

    public BooksModel() {
    }

    public BooksModel(String bookID, String bookTitle, String author, String edition, String ISBN, String postedBy, String postedDate, String category, String condition, String price, String bookImage) {
        BookID = bookID;
        BookTitle = bookTitle;
        Author = author;
        Edition = edition;
        this.ISBN = ISBN;
        PostedBy = postedBy;
        PostedDate = postedDate;
        Category = category;
        Condition = condition;
        Price = price;
        BookImage = bookImage;
    }

    public String getBookID() {
        return BookID;
    }

    public void setBookID(String bookID) {
        BookID = bookID;
    }

    public String getBookTitle() {
        return BookTitle;
    }

    public void setBookTitle(String bookTitle) {
        BookTitle = bookTitle;
    }

    public String getAuthor() {
        return Author;
    }

    public void setAuthor(String author) {
        Author = author;
    }

    public String getEdition() {
        return Edition;
    }

    public void setEdition(String edition) {
        Edition = edition;
    }

    public String getISBN() {
        return ISBN;
    }

    public void setISBN(String ISBN) {
        this.ISBN = ISBN;
    }

    public String getPostedBy() {
        return PostedBy;
    }

    public void setPostedBy(String postedBy) {
        PostedBy = postedBy;
    }

    public String getPostedDate() {
        return PostedDate;
    }

    public void setPostedDate(String postedDate) {
        PostedDate = postedDate;
    }

    public String getCategory() {
        return Category;
    }

    public void setCategory(String category) {
        Category = category;
    }

    public String getCondition() {
        return Condition;
    }

    public void setCondition(String condition) {
        Condition = condition;
    }

    public String getPrice() {
        return Price;
    }

    public void setPrice(String price) {
        Price = price;
    }

    public String getBookImage() {
        return BookImage;
    }

    public void setBookImage(String bookImage) {
        BookImage = bookImage;
    }
}
