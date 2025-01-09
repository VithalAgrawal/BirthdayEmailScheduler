package com.spring.scheduler.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;

@Entity
@Table(name = "email_logs")
public class EmailLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long employeeId; // Employee ID associated with the email

    private String status; // Success or failure

    private LocalDateTime timestamp; // Timestamp of the email log

    @Column(name = "email_id")
    private String emailId; // Email ID of the recipient

    @Column(name = "error", length = 255)
    private String error; // Error message if any

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(Long employeeId) {
        this.employeeId = employeeId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public String getEmailId() {
        return emailId;
    }

    public void setEmailId(String emailId) {
        this.emailId = emailId;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    // Constructor with error
    public EmailLog(Long id, Long employeeId, String status, LocalDateTime timestamp, String emailId, String error) {
        this.id = id;
        this.employeeId = employeeId;
        this.status = status;
        this.timestamp = timestamp;
        this.emailId = emailId;
        this.error = error;
    }

    public EmailLog() {
        super();
    }
}
