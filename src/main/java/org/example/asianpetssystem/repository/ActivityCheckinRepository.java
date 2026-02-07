package org.example.asianpetssystem.repository;

import org.example.asianpetssystem.entity.ActivityCheckin;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ActivityCheckinRepository extends JpaRepository<ActivityCheckin, Long> {

    List<ActivityCheckin> findByActivityId(Long activityId);

    Optional<ActivityCheckin> findByActivityIdAndSignupId(Long activityId, Long signupId);

    long countByActivityId(Long activityId);
}
