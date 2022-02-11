package com.example.myapplication;

public class ChatMessageModel {
    private String name;
    private int id;
    public ChatMessageModel(String name, int id) {
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
