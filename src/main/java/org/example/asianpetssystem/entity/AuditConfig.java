package org.example.asianpetssystem.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Table(name = "audit_config")
@Data
public class AuditConfig {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "auto_audit", nullable = false)
    private Boolean autoAudit = false;

    @Column(name = "require_materials", length = 500)
    private String requireMaterials;

    @Column(name = "audit_flow", length = 1000)
    private String auditFlow;

    @Column(name = "updated_by")
    private Long updatedBy;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}
