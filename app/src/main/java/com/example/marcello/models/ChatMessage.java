package com.example.marcello.models;

public class ChatMessage {
    private String message;
    private int id;
    public ChatMessage(String message, int id) {
        this.message = message;
        this.id = id;
    }
    public int getId() {
        return id;
    }
    public String getMessage() {
        return message;
    }



}
