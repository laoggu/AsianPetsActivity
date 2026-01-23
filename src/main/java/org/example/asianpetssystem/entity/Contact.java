package org.example.asianpetssystem.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "contact")
@Data
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

    // Lombok 的 @Data 注解会自动生成 getter/setter 方法
}
