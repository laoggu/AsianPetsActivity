package org.example.asianpetssystem.entity;

import jakarta.persistence.*;
import lombok.Data;
import org.example.asianpetssystem.common.enums.MemberLevel;
import org.example.asianpetssystem.common.enums.MemberStatus;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "member")
@Data
public class Member {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "company_name", nullable = false, length = 200)
    private String companyName;

    @Column(name = "credit_code", unique = true, nullable = false, length = 18)
    private String creditCode;

    @Enumerated(EnumType.STRING)
    @Column(name = "level", nullable = false)
    private MemberLevel level;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private MemberStatus status;

    @Column(name = "expire_at")
    private LocalDateTime expireAt;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Contact> contacts;

    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Attachment> attachments;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
