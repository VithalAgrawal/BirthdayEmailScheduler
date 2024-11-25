package com.spring.scheduler.entities;

import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "email_templates")
public class EmailTemplate {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
  
    @Column(name = "message_body")
    @JsonProperty("message_body")
    private String messageBody;

    @Column(name = "image_url")
    @JsonProperty("image_url")
    private String imageUrl;
    public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	
	public String getMessageBody() {
		return messageBody;
	}
	public void setMessageBody(String messageBody) {
		this.messageBody = messageBody;
	}
	public String getImageUrl() {
		return imageUrl;
	}
	public void setImageUrl(String imageUrl) {
		this.imageUrl = imageUrl;
	}
	
	public EmailTemplate(Long id, String messageBody, String imageUrl) {
		super();
		this.id = id;
		this.messageBody = messageBody;
		this.imageUrl = imageUrl;
	}
	public EmailTemplate() {
		super();
		// TODO Auto-generated constructor stub
	}


	
    // Getters and Setters
}
