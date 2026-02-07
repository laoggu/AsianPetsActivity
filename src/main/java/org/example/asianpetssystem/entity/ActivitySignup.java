package org.example.asianpetssystem.entity;

import jakarta.persistence.*;
import lombok.Data;
import org.example.asianpetssystem.common.enums.SignupStatus;

import java.time.LocalDateTime;

@Entity
@Table(name = "activity_signup")
@Data
public class ActivitySignup {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "activity_id", nullable = false)
    private Long activityId;

    @Column(name = "member_id", nullable = false)
    private Long memberId;

    @Column(name = "contact_name", length = 100)
    private String contactName;

    @Column(name = "contact_mobile", length = 20)
    private String contactMobile;

    @Column(name = "contact_email", length = 100)
    private String contactEmail;

    @Column(name = "company_name", length = 200)
    private String companyName;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private SignupStatus status;

    @Column(name = "signup_time")
    private LocalDateTime signupTime;

    @Column(name = "audit_time")
    private LocalDateTime auditTime;

    @Column(name = "audit_remark", length = 500)
    private String auditRemark;

    @Column(name = "remark", length = 500)
    private String remark;

    @PrePersist
    protected void onCreate() {
        signupTime = LocalDateTime.now();
    }
}
