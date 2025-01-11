package com.spring.scheduler.service;

import java.io.IOException;

import org.springframework.stereotype.Service;

import com.sendgrid.Method;
import com.sendgrid.Request;
import com.sendgrid.Response;
import com.sendgrid.SendGrid;
import com.sendgrid.helpers.mail.Mail;
import com.sendgrid.helpers.mail.objects.Content;
import com.sendgrid.helpers.mail.objects.Email;

@Service
public class SendGridService {

	private static final String SENDGRID_API_KEY = System.getenv("SENDGRID_API_KEY");

    public boolean sendEmail(String to, String subject, String content, String imageUrl) {
        Email from = new Email("memanishdas@gmail.com");
        Email toEmail = new Email(to);
        Content emailContent = new Content("text/html", 
        	    "<div style='font-family: Arial, sans-serif; color: #333; max-width: 500px; margin: auto; border: 1px solid #e1e1e1; border-radius: 8px; padding: 20px; background-color: #f9f9f9;'>"
        	    + "<div style='font-size: 18px; text-align: center; margin-bottom: 20px; color: #4CAF50;'><strong>🎉 Happy Birthday! 🎉</strong></div>"
        	    + "<div style='font-size: 16px; text-align: center; line-height: 1.5;'>" 
        	    + content 
        	    + "</div>"
        	    + "<div style='text-align: center; margin: 20px 0;'>"
        	    + "<img src='" + imageUrl + "' alt='Birthday Image' style='width: 100%; max-width: 400px; height: auto; border-radius: 10px; box-shadow: 0px 4px 8px rgba(0, 0, 0, 0.1);' />"
        	    + "</div>"
        	    + "<div style='font-size: 14px; color: #555; text-align: center;'>Warmest wishes,<br><strong>Your Company Team</strong></div>"
        	    + "</div>");


        Mail mail = new Mail(from, subject, toEmail, emailContent);

        SendGrid sg = new SendGrid(SENDGRID_API_KEY); // Authentication
        Request request = new Request();
        try {
            request.setMethod(Method.POST);
            request.setEndpoint("mail/send");
            request.setBody(mail.build());
            Response response = sg.api(request);
            return response.getStatusCode() == 202; //  response code check
        } catch (IOException ex) {
            return false;
        }
    }
}