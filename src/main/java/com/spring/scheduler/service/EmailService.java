package com.spring.scheduler.service;

import java.sql.Date;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
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
@Transactional // Ensures all methods in this class are transactional
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

    @PersistenceContext
    private EntityManager entityManager;

    private Set<Long> usedTemplateIds = new HashSet<>();

    @Scheduled(cron = "0 0 9 * * ?") // Runs daily at 9 am
    public void scheduleBirthdayEmails() {
        Date today = Date.valueOf(LocalDate.now());
        List<Employee> employees = employeeRepository.findByBirthday(today);

        logger.info("Scheduled task started: Sending birthday emails for {} employees", employees.size());

        // Send emails for each employee
        employees.forEach(employee -> {
            logger.info("Processing birthday email for employee: {}", employee.getEmail());
            sendBirthdayEmail(employee);
        });

        // clearing the used template ids to use it next day..
        usedTemplateIds.clear();
        logger.info("Cleared usedTemplateIds for the next day.");
        logger.info("Scheduled task completed: All birthday emails sent for the day.");
    }

    public void sendBirthdayEmail(Employee employee) {
        logger.info("Fetching template excluding IDs: {}", usedTemplateIds != null && !usedTemplateIds.isEmpty() ? "No exclusions" : usedTemplateIds);

        // Inserting usedTemplateIds into the temp_used_template_ids 
        if (usedTemplateIds != null && !usedTemplateIds.isEmpty()) {
            usedTemplateIds.forEach(id -> {
                entityManager.createNativeQuery("INSERT INTO temp_used_template_ids (id) VALUES (:id) ON CONFLICT DO NOTHING")
                    .setParameter("id", id)
                    .executeUpdate();
            });
        }

        // Fetching a random template excluding already used IDs
        EmailTemplate template = null;
        try {
            template = templateRepository.findRandomTemplateExcludingTempIds();
            logger.info("Selected Template ID: {}", template != null ? template.getId() : "No template found");
        } catch (Exception e) {
            logger.error("Error fetching random template: {}", e.getMessage(), e);
        }

        //  Sending email if a template is found..
        if (template != null) {
            usedTemplateIds.add(template.getId());
            logger.info("Selected Template ID: {}", template.getId());

            String content = String.format(template.getMessageBody(), employee.getName());
            String subject = String.format(template.getSubject(), employee.getName());
            EmailLog log = new EmailLog();
            log.setEmployeeId(employee.getId());
            log.setEmailId(employee.getEmail());
            log.setTimestamp(LocalDateTime.now());

            try {
                boolean isSuccess = sendGridService.sendEmail(employee.getEmail(), subject, content, template.getImageUrl());
                log.setStatus(isSuccess ? "success" : "failure");
                log.setError(isSuccess ? null : "Unknown failure occurred while sending email.");
                if (isSuccess) {
                    logger.info("Birthday email successfully sent to {} (ID: {})", employee.getEmail(), employee.getId());
                } else {
                    logger.error("Failed to send email to {} (ID: {})", employee.getEmail(), employee.getId());
                }
            } catch (Exception e) {
                log.setStatus("failure");
                log.setError(e.getMessage()); // Log the error message
                logger.error("Exception while sending email to {} (ID: {}): {}", employee.getEmail(), employee.getId(), e.getMessage(), e);
            }

            // Save log to the database
            logRepository.save(log);

        } else {
            logger.warn("No email template found or all templates used. Skipping email for employee: {}", employee.getEmail());
        }

        // Clearing the temporary table after using itt...
        entityManager.createNativeQuery("TRUNCATE TABLE temp_used_template_ids").executeUpdate();
    }

}




