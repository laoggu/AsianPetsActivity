package org.example.asianpetssystem.repository;

import org.example.asianpetssystem.entity.ContactChangeLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ContactChangeLogRepository extends JpaRepository<ContactChangeLog, Long> {

    List<ContactChangeLog> findByMemberIdOrderByCreatedAtDesc(Long memberId);

    Page<ContactChangeLog> findByMemberId(Long memberId, Pageable pageable);

    List<ContactChangeLog> findByContactIdOrderByCreatedAtDesc(Long contactId);
}
