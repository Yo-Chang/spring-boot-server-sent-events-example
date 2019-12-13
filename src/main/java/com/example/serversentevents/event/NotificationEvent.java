package com.example.serversentevents.event;

public class NotificationEvent {

    private String sessionId;

    private Object content;

    public NotificationEvent(String sessionId, Object content) {
        this.sessionId = sessionId;
        this.content = content;
    }

    public String getSessionId() {
        return sessionId;
    }

    public Object getContent() {
        return content;
    }
}
