package org.example.asianpetssystem.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "attachment")
public class Attachment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @Enumerated(EnumType.STRING)
    private AttachmentType type;

    @Column(name = "oss_key")
    private String ossKey;

    // 构造函数、getter、setter
}

enum AttachmentType {
    LICENSE, BROCHURE, OTHER
}
