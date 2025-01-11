package com.spring.scheduler.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.spring.scheduler.entities.EmailLog;

@Repository
public interface EmailLogRepository extends JpaRepository<EmailLog, Long>{

	
	  @Query(value = "SELECT * FROM email_logs WHERE status = :status", nativeQuery = true)
	    List<EmailLog> findByStatus(@Param("status") String status);
}
