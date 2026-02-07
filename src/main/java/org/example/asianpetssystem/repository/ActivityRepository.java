package org.example.asianpetssystem.repository;

import org.example.asianpetssystem.common.enums.ActivityStatus;
import org.example.asianpetssystem.entity.Activity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface ActivityRepository extends JpaRepository<Activity, Long> {

    Page<Activity> findByIsDeletedFalse(Pageable pageable);

    Page<Activity> findByStatusAndIsDeletedFalse(ActivityStatus status, Pageable pageable);

    Page<Activity> findByTitleContainingAndIsDeletedFalse(String title, Pageable pageable);

    List<Activity> findByStatusAndEndTimeAfterAndIsDeletedFalse(ActivityStatus status, LocalDateTime now);
}
