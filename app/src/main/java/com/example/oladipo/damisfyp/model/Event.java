package com.example.oladipo.damisfyp.model;

public class Event extends EventId  {

    private String genre;
    private String image;
    private String post;
    private long timestamp;
    private String description;
//    private FieldValue timestamp;

    public Event(){

    }
    //
    public Event(String genre, String image, String title,long timestamp, String description) {
        this.genre = genre;
        this.image = image;
        post = title;
        this.timestamp = timestamp;
        this.description = description;
    }

//    public Blog(String genre, String image, String title, FieldValue timestamp, String description) {
//        this.genre = genre;
//        this.image = image;
//        post = title;
//        this.timestamp = timestamp;
//        this.description = description;
//    }

    public String getGenre() {
        return genre;
    }

    public String getImage() {
        return image;
    }

    public String getPost() {
        return post;
    }

    public Long getTimestamp() {
        return timestamp;
    }

    public String getDescription() {
        return description;
    }
//    public FieldValue getTimestamp(){
//        return timestamp;
//    }
}
