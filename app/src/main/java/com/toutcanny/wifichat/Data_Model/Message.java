package com.toutcanny.wifichat.Data_Model;

/**
 * Created by Farhan on 14-03-2016.
 */
public class Message {
    public static int CURRENT_USER=1;
    public static int OTHER_USER=1;
    private String message=null;
    private int sender;

    public Message(String message, int sender) {
        this.message = message;
        this.sender = sender;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getSender() {
        return sender;
    }

    public void setSender(int sender) {
        this.sender = sender;
    }
}
