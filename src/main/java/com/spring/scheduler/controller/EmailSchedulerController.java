package com.spring.scheduler.controller;

import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
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
    
    
    //  end point to get upcoming birthdays
    @GetMapping("/getUpcomingBirthdays")
    public ResponseEntity<List<Employee>> getUpcomingBirthdays() {
        LocalDate today = LocalDate.now();
        List<Employee> upcomingBirthdays = employeeRepository.findUpcomingBirthdays(today.getMonthValue(), today.getDayOfMonth());
        return ResponseEntity.ok(upcomingBirthdays);
    }
    
    
    // end point to get all birthdays within a specified month
    @GetMapping("/getBirthdaysByMonth/{month}")
    public ResponseEntity<List<Employee>> getBirthdaysByMonth(@PathVariable int month) {
        List<Employee> monthlyBirthdays = employeeRepository.findAllBirthdaysInMonth(month);
        return ResponseEntity.ok(monthlyBirthdays);
    }
    
    
 // New endpoint to save an email template
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
    
    
    // Endpoint to fetch all email logs
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

    
    
}

	