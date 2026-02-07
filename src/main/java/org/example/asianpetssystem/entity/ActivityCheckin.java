package org.example.asianpetssystem.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Table(name = "activity_checkin")
@Data
public class ActivityCheckin {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "activity_id", nullable = false)
    private Long activityId;

    @Column(name = "signup_id", nullable = false)
    private Long signupId;

    @Column(name = "member_id")
    private Long memberId;

    @Column(name = "checkin_time")
    private LocalDateTime checkinTime;

    @Column(name = "checkin_type", length = 20)
    private String checkinType;

    @Column(name = "checkin_code", length = 50)
    private String checkinCode;

    @PrePersist
    protected void onCreate() {
        checkinTime = LocalDateTime.now();
    }
}
