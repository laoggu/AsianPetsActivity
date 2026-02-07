package org.example.asianpetssystem.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 蓝皮书下载记录实体
 */
@Entity
@Table(name = "bluebook_download")
@Data
public class BluebookDownload {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "bluebook_id", nullable = false)
    private Long bluebookId;

    @Column(name = "member_id", nullable = false)
    private Long memberId;

    @Column(name = "download_time")
    private LocalDateTime downloadTime;

    @Column(name = "ip_address", length = 50)
    private String ipAddress;

    @Column(name = "user_agent", length = 500)
    private String userAgent;

    @Column(name = "file_name", length = 200)
    private String fileName;

    @Column(name = "file_size")
    private Long fileSize;

    @Column(name = "has_watermark")
    private Boolean hasWatermark = true;

    @Column(name = "watermark_content", length = 100)
    private String watermarkContent;

    @PrePersist
    protected void onCreate() {
        downloadTime = LocalDateTime.now();
    }
}
