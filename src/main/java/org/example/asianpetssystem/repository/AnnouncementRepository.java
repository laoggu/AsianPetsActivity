package org.example.asianpetssystem.repository;

import org.example.asianpetssystem.entity.Announcement;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AnnouncementRepository extends JpaRepository<Announcement, Long> {

    Page<Announcement> findByIsDeletedFalse(Pageable pageable);

    Page<Announcement> findByTypeAndIsDeletedFalse(String type, Pageable pageable);

    List<Announcement> findByIsTopTrueAndStatusAndIsDeletedFalseOrderByTopOrderDesc(String status);

    List<Announcement> findByStatusAndIsDeletedFalseOrderByIsTopDescTopOrderDescPublishTimeDesc(String status);
}
