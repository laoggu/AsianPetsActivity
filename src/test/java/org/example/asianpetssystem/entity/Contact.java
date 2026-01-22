package org.example.asianpetssystem.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "contact")
public class Contact {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    private String name;

    @Column(name = "mobile")
    private String mobile; // 脱敏处理

    private String email;

    @Column(name = "is_primary")
    private Boolean isPrimary;

    // 构造函数、getter、setter
}
