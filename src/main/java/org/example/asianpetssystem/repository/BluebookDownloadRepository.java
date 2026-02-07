package org.example.asianpetssystem.repository;

import org.example.asianpetssystem.entity.BluebookDownload;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface BluebookDownloadRepository extends JpaRepository<BluebookDownload, Long> {

    long countByBluebookIdAndMemberId(Long bluebookId, Long memberId);

    long countByBluebookId(Long bluebookId);

    List<BluebookDownload> findByMemberId(Long memberId);

    Page<BluebookDownload> findByBluebookId(Long bluebookId, Pageable pageable);

    @Query("SELECT COUNT(d) FROM BluebookDownload d WHERE d.bluebookId = ?1 AND d.downloadTime >= ?2")
    long countRecentDownloads(Long bluebookId, LocalDateTime since);

    @Query("SELECT d.bluebookId, COUNT(d) FROM BluebookDownload d GROUP BY d.bluebookId")
    List<Object[]> getDownloadCountsGroupByBluebook();
}
