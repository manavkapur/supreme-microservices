package com.supremesolutions.notificationservice.dto;

import java.util.Map;

public class FcmMessage {
    private String to;
    private Map<String, Object> data;
    private Notification notification;

    public static record Notification(String title, String body) {}

    // getters/setters
    public String getTo() { return to; }
    public void setTo(String to) { this.to = to; }
    public Map<String, Object> getData() { return data; }
    public void setData(Map<String, Object> data) { this.data = data; }
    public Notification getNotification() { return notification; }
    public void setNotification(Notification notification) { this.notification = notification; }
}
