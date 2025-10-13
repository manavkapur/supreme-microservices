package com.supremesolutions.contact.dto;

public class ContactEvent {

    private String event;
    private String contactId;
    private String name;
    private String email;
    private String message;

    public ContactEvent() {
        // default constructor (needed for JSON)
    }

    public ContactEvent(String event, String contactId, String name, String email, String message) {
        this.event = event;
        this.contactId = contactId;
        this.name = name;
        this.email = email;
        this.message = message;
    }

    // Getters
    public String getEvent() { return event; }
    public String getContactId() { return contactId; }
    public String getName() { return name; }
    public String getEmail() { return email; }
    public String getMessage() { return message; }

    // Setters
    public void setEvent(String event) { this.event = event; }
    public void setContactId(String contactId) { this.contactId = contactId; }
    public void setName(String name) { this.name = name; }
    public void setEmail(String email) { this.email = email; }
    public void setMessage(String message) { this.message = message; }
}
