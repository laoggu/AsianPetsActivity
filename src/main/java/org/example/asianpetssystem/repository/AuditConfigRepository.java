package org.example.asianpetssystem.repository;

import org.example.asianpetssystem.entity.AuditConfig;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AuditConfigRepository extends JpaRepository<AuditConfig, Long> {

    /**
     * 获取最新的审核配置
     *
     * @return 审核配置对象
     */
    Optional<AuditConfig> findFirstByOrderByIdDesc();
}
