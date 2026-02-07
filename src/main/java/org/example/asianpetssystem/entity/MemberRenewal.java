package org.example.asianpetssystem.entity;

import jakarta.persistence.*;
import lombok.Data;
import org.example.asianpetssystem.common.enums.RenewalStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 会员续费记录实体
 */
@Entity
@Table(name = "member_renewal")
@Data
public class MemberRenewal {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "member_id", nullable = false)
    private Long memberId;

    @Column(name = "renewal_no", length = 50, unique = true)
    private String renewalNo;

    @Column(name = "original_expire_date", nullable = false)
    private LocalDateTime originalExpireDate;

    @Column(name = "new_expire_date", nullable = false)
    private LocalDateTime newExpireDate;

    @Column(name = "amount", precision = 10, scale = 2)
    private BigDecimal amount;

    @Column(name = "level", length = 20)
    private String level;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private RenewalStatus status;

    @Column(name = "payment_method", length = 20)
    private String paymentMethod;

    @Column(name = "payment_time")
    private LocalDateTime paymentTime;

    @Column(name = "transaction_no", length = 100)
    private String transactionNo;

    @Column(name = "remark", length = 500)
    private String remark;

    @Column(name = "created_by")
    private Long createdBy;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

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
