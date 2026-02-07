package org.example.asianpetssystem.repository;

import org.example.asianpetssystem.common.enums.SignupStatus;
import org.example.asianpetssystem.entity.ActivitySignup;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ActivitySignupRepository extends JpaRepository<ActivitySignup, Long> {

    Page<ActivitySignup> findByActivityId(Long activityId, Pageable pageable);

    Page<ActivitySignup> findByActivityIdAndStatus(Long activityId, SignupStatus status, Pageable pageable);

    Optional<ActivitySignup> findByActivityIdAndMemberId(Long activityId, Long memberId);

    long countByActivityId(Long activityId);

    long countByActivityIdAndStatus(Long activityId, SignupStatus status);
}
