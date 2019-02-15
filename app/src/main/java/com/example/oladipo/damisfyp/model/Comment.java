package com.example.oladipo.damisfyp.model;

import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.ServerTimestamp;

import java.util.Date;

public class Comment {

    private String message, user_id;
    @ServerTimestamp
    private Date timestamp;
    private FieldValue stimestamp;


    public Comment(){}
    public Comment(String message, String user_id, Date timestamp) {
        this.message = message;
        this.user_id = user_id;
        this.timestamp = timestamp;
    }

    public Comment(String message, String user_id, FieldValue stimestamp) {
        this.message = message;
        this.user_id = user_id;
        this.stimestamp = stimestamp;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

    public FieldValue getStimestamp() {
        return stimestamp;
    }
}
