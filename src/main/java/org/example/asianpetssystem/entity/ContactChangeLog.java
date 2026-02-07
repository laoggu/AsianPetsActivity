package org.example.asianpetssystem.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 联系人变更记录实体
 */
@Entity
@Table(name = "contact_change_log")
@Data
public class ContactChangeLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "member_id", nullable = false)
    private Long memberId;

    @Column(name = "contact_id")
    private Long contactId;

    @Column(name = "change_type", length = 20, nullable = false)
    private String changeType;  // CREATE, UPDATE, DELETE

    @Column(name = "field_name", length = 50)
    private String fieldName;  // 变更的字段名

    @Column(name = "old_value", length = 500)
    private String oldValue;

    @Column(name = "new_value", length = 500)
    private String newValue;

    @Column(name = "changed_by")
    private Long changedBy;  // 变更人ID

    @Column(name = "change_reason", length = 200)
    private String changeReason;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}
