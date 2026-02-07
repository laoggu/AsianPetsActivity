package org.example.asianpetssystem.repository;

import org.example.asianpetssystem.entity.Bluebook;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BluebookRepository extends JpaRepository<Bluebook, Long> {

    Page<Bluebook> findByIsDeletedFalse(Pageable pageable);

    Page<Bluebook> findByYearAndIsDeletedFalse(Integer year, Pageable pageable);

    Page<Bluebook> findByTitleContainingAndIsDeletedFalse(String title, Pageable pageable);

    List<Bluebook> findByStatusAndIsDeletedFalseOrderByYearDesc(String status);

    Page<Bluebook> findByYearAndTitleContainingAndIsDeletedFalse(Integer year, String title, Pageable pageable);
}
