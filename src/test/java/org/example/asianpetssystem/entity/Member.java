package org.example.asianpetssystem.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "member")
public class Member {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "company_name", nullable = false)
    private String companyName;

    @Column(name = "credit_code", unique = true, nullable = false)
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

    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL)
    private List<Contact> contacts;

    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL)
    private List<Attachment> attachments;

    // 构造函数、getter、setter
}

enum MemberLevel {
    REGULAR, GOLD, PLATINUM
}

enum MemberStatus {
    PENDING, APPROVED, REJECTED, SUSPENDED
}
