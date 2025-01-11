package com.spring.scheduler.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.spring.scheduler.entities.EmailTemplate;
@Repository
public interface EmailTemplateRepository extends JpaRepository<EmailTemplate, Long> {

    @Query(value = """
        SELECT * 
        FROM email_templates
        WHERE NOT EXISTS (
            SELECT 1 
            FROM temp_used_template_ids 
            WHERE email_templates.id = temp_used_template_ids.id
        )
        ORDER BY RANDOM()
        LIMIT 1
        """, nativeQuery = true)
    EmailTemplate findRandomTemplateExcludingTempIds();
}
