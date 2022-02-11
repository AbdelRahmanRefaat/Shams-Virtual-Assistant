package com.example.marcello.models;

public class ChatMessage {
    private String name;
    private int id;
    public ChatMessage(String name, int id) {
        this.name = name;
        this.id = id;
    }
    public int getId() {
        return id;
    }
    public String getName() {
        return name;
    }



}
