package org.acme.resources;

import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class MessageService {

    private String message;
    private String messageType;

    public void sendMessage(String message, String messageType) {
        this.message = message;
        this.messageType = messageType;
    }

    public String getMessage() {
        return message;
    }

    public String getMessageType() {
        return messageType;
    }
}
