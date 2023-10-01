package com.lazypanda.models;
import com.google.firebase.database.PropertyName;

public class ModelChat {

    String message, receiver, sender, timeStamp;
    boolean isSeen;
    
    public ModelChat() {}

    public ModelChat(
            String message, String receiver, String sender, String timeStamp, boolean isSeen) {
        this.message = message;
        this.receiver = receiver;
        this.sender = sender;
        this.timeStamp = timeStamp;
        this.isSeen = isSeen;
    }

    public String getMessage() {
        return this.message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getReceiver() {
        return this.receiver;
    }

    public void setReceiver(String receiver) {
        this.receiver = receiver;
    }

    public String getSender() {
        return this.sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    @PropertyName("timestamp")
    public String getTimeStamp() {
        return this.timeStamp;
    }
    
    @PropertyName("timestamp")
    public void setTimeStamp(String timeStamp) {
        this.timeStamp = timeStamp;
    }
    
    @PropertyName("isSeen")
    public boolean getIsSeen() {
        return this.isSeen;
    }
    
    @PropertyName("isSeen")
    public void setIsSeen(boolean isSeen) {
        this.isSeen = isSeen;
    }
}
