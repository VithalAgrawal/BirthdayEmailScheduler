package com.spring.scheduler.service;

import java.sql.Date;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.spring.scheduler.entities.EmailLog;
import com.spring.scheduler.entities.EmailTemplate;
import com.spring.scheduler.entities.Employee;
import com.spring.scheduler.repository.EmailLogRepository;
import com.spring.scheduler.repository.EmailTemplateRepository;
import com.spring.scheduler.repository.EmployeeRepository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;



@Service
public class EmailService {

    private static final Logger logger = LoggerFactory.getLogger(EmailService.class);

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private EmailTemplateRepository templateRepository;

    @Autowired
    private EmailLogRepository logRepository;

    @Autowired
    private SendGridService sendGridService;

    @Scheduled(cron = "0 26 10 * * ?") // Runs daily 
    public void scheduleBirthdayEmails() {
        Date today = Date.valueOf(LocalDate.now());
        List<Employee> employees = employeeRepository.findByBirthday(today);

        logger.info("Scheduled task started: Sending birthday emails for {} employees", employees.size());

        employees.forEach(employee -> {
            logger.info("Processing birthday email for employee: {}", employee.getEmail());
            sendBirthdayEmail(employee);
        });

        logger.info("Scheduled task complete: All birthday emails sent for the day.");
    }

    public void sendBirthdayEmail(Employee employee) {
        logger.info("Fetching all predefined templates...");
        List<EmailTemplate> allTemplates = templateRepository.findAllTemplates();

        if (allTemplates.size() >= 2) {
        	
            logger.info("Selecting random message body and image URL from different templates..");
            
            
            // Selecting a random message body from one template.
            EmailTemplate bodyTemplate = allTemplates.get(new Random().nextInt(allTemplates.size()));

            // Selecting a random image URL from another template.
            EmailTemplate imageTemplate;
            
            do {
                imageTemplate = allTemplates.get(new Random().nextInt(allTemplates.size()));
            } while (bodyTemplate == imageTemplate); // Checkpoint ensuring different templates..

            // Combining randomly to make unique template.
            String subject = "Happy Birthday " + employee.getName();
            String content = bodyTemplate.getMessageBody() + ", " + employee.getName() + "!";
            String imageUrl = imageTemplate.getImageUrl();

            try {
            	// Custom made exception to check resend failed email fnxtionalty...
                if (employee.getEmail().contains("test-failure")) {      
                    throw new RuntimeException("Failure email Testing");
                }

                boolean isSuccess = sendGridService.sendEmail(employee.getEmail(), subject, content, imageUrl);
                if (isSuccess) {
                    logger.info("Birthday email successfully sent to (ID: {}): {}", employee.getId(), employee.getEmail());
                    saveEmailLog(employee, "success", content, null); // Success status
                } else {
                    logger.error("Failed to send email to (ID: {}): {}", employee.getId(), employee.getEmail());
                    saveEmailLog(employee, "failure", content, "Failed to send email.");
                }
            } catch (Exception e) {
                logger.error("Exception while sending email to (ID: {}): {}", employee.getId(), employee.getEmail(), e.getMessage());
                saveEmailLog(employee, "failure", content, e.getMessage()); 
            }
        } else {
            logger.warn("Not enough templates to create combinations. Skipping email for employee: {}", employee.getEmail());
        }
    }

    
    // fnx to resend the failed bday emails.. 
    public boolean resendFailedEmail(Employee employee) {
        logger.info("Resending failed email for employee: {}", employee.getEmail());
        
        // Fetching all the templates for random combination.
        List<EmailTemplate> allTemplates = templateRepository.findAllTemplates();
 
        if (allTemplates.size() >= 2) { 
            logger.info("Selecting random message body and image URL from different templates..");
            
            // Selecting a random message body from one template
            EmailTemplate bodyTemplate = allTemplates.get(new Random().nextInt(allTemplates.size()));
            
            // Selecting a random image URL from another template
            EmailTemplate imageTemplate;
            do {
                imageTemplate = allTemplates.get(new Random().nextInt(allTemplates.size()));
            } while (bodyTemplate == imageTemplate); // checkpoint for ensuring both templates are different.

            // Combining randomly to make unique templates.
            String subject = "Happy Birthday " + employee.getName();
            String content = bodyTemplate.getMessageBody() + ", " + employee.getName() + "!";
            String imageUrl = imageTemplate.getImageUrl();

            try {
              
                boolean isSuccess = sendGridService.sendEmail(employee.getEmail(), subject, content, imageUrl);

                
                saveEmailLog(employee, isSuccess ? "success" : "failure", 
                             content, isSuccess ? null : "SendGrid failed to resend email.");
                return isSuccess;

            } catch (Exception e) {
                // excption handle...
                logger.error("Error while resending email to {}: {}", employee.getEmail(), e.getMessage());
                saveEmailLog(employee, "failure", content, e.getMessage());
                return false;
            }
        } else {
            logger.warn("Not enough templates to create combinations. Skipping email for employee: {}", employee.getEmail());
            saveEmailLog(employee, "failure", null, "No email template found for resend.");
            return false;
        }
    }

    private void saveEmailLog(Employee employee, String status, String content, String error) {
        EmailLog log = new EmailLog();
        log.setEmployeeId(employee.getId());
        log.setStatus(status);
        log.setTimestamp(LocalDateTime.now());
        log.setEmailId(employee.getEmail());
        log.setError(error);

        logRepository.save(log);
    }
}




