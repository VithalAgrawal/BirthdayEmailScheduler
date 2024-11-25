package com.spring.scheduler.controller;

import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.spring.scheduler.entities.EmailLog;
import com.spring.scheduler.entities.EmailTemplate;
import com.spring.scheduler.entities.Employee;
import com.spring.scheduler.repository.EmailLogRepository;
import com.spring.scheduler.repository.EmailTemplateRepository;
import com.spring.scheduler.repository.EmployeeRepository;
import com.spring.scheduler.service.EmailService;



@RestController
@RequestMapping("/api/admin")
public class EmailSchedulerController {

    @Autowired
    private EmailService emailService;
    
    @Autowired
    private EmployeeRepository employeeRepository;
    
    @Autowired
    private EmailTemplateRepository emailtemplaterepository;
    
    @Autowired
    private EmailLogRepository emailLogRepository;

    @GetMapping("/send-birthday-emails")
    public ResponseEntity<String> triggerEmail() {
        emailService.scheduleBirthdayEmails();
        return ResponseEntity.ok("Birthday emails sent for today.");
    }
    
    
    // End point to get upcoming birthdays
    @GetMapping("/getUpcomingBirthdays")
    public ResponseEntity<List<Employee>> getUpcomingBirthdays() {
        LocalDate today = LocalDate.now();
        List<Employee> upcomingBirthdays = employeeRepository.findUpcomingBirthdays(today.getMonthValue(), today.getDayOfMonth());
        return ResponseEntity.ok(upcomingBirthdays);
    }
    
    
    // End point to get all birthdays within a specified month
    @GetMapping("/getBirthdaysByMonth/{month}")
    public ResponseEntity<List<Employee>> getBirthdaysByMonth(@PathVariable int month) {
        List<Employee> monthlyBirthdays = employeeRepository.findAllBirthdaysInMonth(month);
        return ResponseEntity.ok(monthlyBirthdays);
    }
    
    
 // End point to save an email template
    @PostMapping("/saveTemplate")
    public ResponseEntity<String> saveEmailTemplate(@RequestBody EmailTemplate emailTemplate) {
        try {
            // Log incoming payload for debugging
            System.out.println("Received Email Template: " + emailTemplate);

            // Save the email template to the database
            emailtemplaterepository.save(emailTemplate);
            return ResponseEntity.ok("Template saved successfully!");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error saving template: " + e.getMessage());
        }
    }
    
    
    // End point to fetch emailLogs..
    @GetMapping("/getEmailLogs")
    public ResponseEntity<List<EmailLog>> getEmailLogs(@RequestParam(required = false) String status) {
        try {
            List<EmailLog> emailLogs;

            // Check if a status filter is provided
            if (status != null && !status.isEmpty()) {
                emailLogs = emailLogRepository.findByStatus(status);
            } else {
                emailLogs = emailLogRepository.findAll();
            }

            return ResponseEntity.ok(emailLogs);
        } catch (Exception e) {
            return ResponseEntity.status(500).body(null); // Return an appropriate error response
        }
    }
    
    // End point for resending the failed emails....
    @PostMapping("/resendFailedEmail/{employeeId}")
    public ResponseEntity<String> resendFailedEmail(@PathVariable Long employeeId) {
        try {
            // Fetching the employee details
            Employee employee = employeeRepository.findById(employeeId)
                    .orElseThrow(() -> new RuntimeException("Employee not found with ID: " + employeeId));

            // Manually triggering re sending of email
            boolean isResent = emailService.resendFailedEmail(employee);

            if (isResent) {
                return ResponseEntity.ok("Email successfully resent to employee with ID: " + employeeId);
            } else {
                return ResponseEntity.status(500).body("Failed to resend email to employee with ID: " + employeeId);
            }
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error occurred while resending email: " + e.getMessage());
        }
    }
    
    
 // End point to fetch all email templates.....
    @GetMapping("/getAllTemplates")
    public ResponseEntity<List<EmailTemplate>> getAllTemplates() {
        try {
            List<EmailTemplate> templates = emailtemplaterepository.findAll();
            return ResponseEntity.ok(templates);
        } catch (Exception e) {
            return ResponseEntity.status(500).body(null); // Return 500 status in case of errors
        }
    }

    // End point to update an existing email template giving its id.
    @PutMapping("/updateTemplate/{id}")
    public ResponseEntity<String> updateTemplate(@PathVariable Long id, @RequestBody EmailTemplate updatedTemplate) {
        try {
            // Fetching the existing template by ID
            EmailTemplate existingTemplate = emailtemplaterepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Template not found with ID: " + id));

            // Updating the fields with the new data
            existingTemplate.setMessageBody(updatedTemplate.getMessageBody());
            existingTemplate.setImageUrl(updatedTemplate.getImageUrl());

            // Saving the updated template
            emailtemplaterepository.save(existingTemplate);

            return ResponseEntity.ok("Template updated successfully!");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error updating template: " + e.getMessage());
        }
    }


    
    
}

	