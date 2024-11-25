package com.spring.scheduler.repository;

import java.sql.Date;
import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.spring.scheduler.entities.Employee;

@Repository
public interface EmployeeRepository extends JpaRepository<Employee, Long> {

     // bdays on a particular date selected..
	@Query(value = "SELECT * FROM employees e WHERE to_char(e.birthday, 'MM-DD') = to_char(CAST(:today AS date), 'MM-DD')", nativeQuery = true)
	List<Employee> findByBirthday(@Param("today") Date today);
	
	@Query(value = "SELECT * FROM employees e WHERE EXTRACT(MONTH FROM e.birthday) = :month AND EXTRACT(DAY FROM e.birthday) = :day ORDER BY e.birthday ASC", nativeQuery = true)
	List<Employee> findUpcomingBirthdays(@Param("month") int month, @Param("day") int day);

    
	 //Get all bdays in a specified month....
    @Query(value = "SELECT * FROM employees e WHERE EXTRACT(MONTH FROM e.birthday) = :month ORDER BY EXTRACT(DAY FROM e.birthday) ASC", nativeQuery = true)
    List<Employee> findAllBirthdaysInMonth(@Param("month") int month);
     
	
}
