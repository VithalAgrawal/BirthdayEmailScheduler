package com.spring.scheduler.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class EmailServiceTest {

    @Autowired
    private EmailService emailService;

    @Test
    public void testScheduleBirthdayEmails() {
        // Call the method to see if it works as expected
        emailService.scheduleBirthdayEmails();
    }
}
