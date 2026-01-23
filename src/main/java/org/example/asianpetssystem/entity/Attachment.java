// src/main/java/org/example/asianpetssystem/entity/Attachment.java
package org.example.asianpetssystem.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "attachment")
@Data
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

    @Column(name = "created_at")
    private java.time.LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = java.time.LocalDateTime.now();
    }
}
