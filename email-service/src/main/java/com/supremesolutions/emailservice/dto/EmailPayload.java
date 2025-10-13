package com.supremesolutions.emailservice.dto;

import java.util.Map;

public class EmailPayload {
    private String to;
    private String subject;
    private String body;
    private Map<String, Object> variables; // optional (for templates)

    public String getTo() { return to; }
    public void setTo(String to) { this.to = to; }

    public String getSubject() { return subject; }
    public void setSubject(String subject) { this.subject = subject; }

    public String getBody() { return body; }
    public void setBody(String body) { this.body = body; }

    public Map<String, Object> getVariables() { return variables; }
    public void setVariables(Map<String, Object> variables) { this.variables = variables; }
}
