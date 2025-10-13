package com.supremesolutions.notificationservice.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class NotificationEvent {
    private String event;
    private String userId;      // required to find FCM token
    private String title;
    private String body;
    private Object payload;     // optional details

    // getters/setters
    public String getEvent() { return event; }
    public void setEvent(String event) { this.event = event; }
    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getBody() { return body; }
    public void setBody(String body) { this.body = body; }
    public Object getPayload() { return payload; }
    public void setPayload(Object payload) { this.payload = payload; }
}
