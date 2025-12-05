package com.example.graphql.model;

import org.eclipse.microprofile.graphql.Name;
import org.eclipse.microprofile.graphql.Description;

@Description("A book in the library")
public class Book {
    
    @Name("id")
    private String id;
    
    @Name("title")
    private String title;
    
    @Name("author")
    private String author;
    
    @Name("year")
    private int year;
    
    public Book() {
    }
    
    public Book(String id, String title, String author, int year) {
        this.id = id;
        this.title = title;
        this.author = author;
        this.year = year;
    }
    
    public String getId() {
        return id;
    }
    
    public void setId(String id) {
        this.id = id;
    }
    
    public String getTitle() {
        return title;
    }
    
    public void setTitle(String title) {
        this.title = title;
    }
    
    public String getAuthor() {
        return author;
    }
    
    public void setAuthor(String author) {
        this.author = author;
    }
    
    public int getYear() {
        return year;
    }
    
    public void setYear(int year) {
        this.year = year;
    }
}