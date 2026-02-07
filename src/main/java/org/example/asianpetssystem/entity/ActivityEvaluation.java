package org.example.asianpetssystem.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 活动评价实体
 */
@Entity
@Table(name = "activity_evaluation")
@Data
public class ActivityEvaluation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "activity_id", nullable = false)
    private Long activityId;

    @Column(name = "member_id", nullable = false)
    private Long memberId;

    @Column(name = "signup_id", nullable = false)
    private Long signupId;

    // 总体评分（1-5星）
    @Column(name = "overall_rating", nullable = false)
    private Integer overallRating;

    // 内容评分
    @Column(name = "content_rating")
    private Integer contentRating;

    // 组织评分
    @Column(name = "organization_rating")
    private Integer organizationRating;

    // 讲师/嘉宾评分
    @Column(name = "speaker_rating")
    private Integer speakerRating;

    // 场地评分
    @Column(name = "venue_rating")
    private Integer venueRating;

    // 评价内容
    @Column(name = "comment", length = 2000)
    private String comment;

    // 是否匿名
    @Column(name = "is_anonymous")
    private Boolean isAnonymous = false;

    // 是否有改进建议
    @Column(name = "has_suggestion")
    private Boolean hasSuggestion = false;

    // 改进建议内容
    @Column(name = "suggestion", length = 1000)
    private String suggestion;

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
