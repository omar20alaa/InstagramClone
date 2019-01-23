package com.instagram.instagramclone.Model;

public class Comment {

    // vars
    private String comment;
    private String publisher;

    public Comment() {
    } // empty constructor

    public Comment(String comment, String publisher) {
        this.comment = comment;
        this.publisher = publisher;
    } // constructor

    // getter and setter


    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getPublisher() {
        return publisher;
    }

    public void setPublisher(String publisher) {
        this.publisher = publisher;
    }
}
