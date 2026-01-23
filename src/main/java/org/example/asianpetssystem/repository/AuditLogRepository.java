package org.example.asianpetssystem.repository;

import org.example.asianpetssystem.entity.AuditLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AuditLogRepository extends JpaRepository<AuditLog, Long> {
    List<AuditLog> findByMemberId(Long memberId);

    List<AuditLog> findByOperatorId(Long operatorId);
}
