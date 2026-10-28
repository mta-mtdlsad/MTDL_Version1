package com.domain;

public class ValidationResult {
    String message;
    String severity;

    public ValidationResult(String message, String severity) {
        this.message = message;
        this.severity = severity;
    }

    public String getMessage() {
        return message;
    }

    public String getSeverity() {
        return severity;
    }
}
