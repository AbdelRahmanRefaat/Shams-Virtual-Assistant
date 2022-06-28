package com.example.marcello.models;

public class Message {

    // message sender
    public static final int MESSAGE_SENDER_USER = 1;
    public static final int MESSAGE_SENDER_BOT = 2;


    private MessageType messageType;
    private int messageSender;
    private String messageText;

    public MessageType getMessageType() {
        return messageType;
    }

    public void setMessageType(MessageType messageType) {
        this.messageType = messageType;
    }

    public int getMessageSender() {
        return messageSender;
    }

    public void setMessageSender(int messageSender) {
        this.messageSender = messageSender;
    }

    public String getMessageText() {
        return messageText;
    }

    public void setMessageText(String messageText) {
        this.messageText = messageText;
    }
}
