package com.spring.scheduler.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.spring.scheduler.entities.TempUsedTemplateId;

public interface TempUsedTemplateIdRepository extends JpaRepository<TempUsedTemplateId, Long> {
}