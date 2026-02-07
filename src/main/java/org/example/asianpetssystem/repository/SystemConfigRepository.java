package org.example.asianpetssystem.repository;

import org.example.asianpetssystem.entity.SystemConfig;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SystemConfigRepository extends JpaRepository<SystemConfig, Long> {

    /**
     * 根据配置键查询配置
     *
     * @param configKey 配置键
     * @return 配置对象
     */
    Optional<SystemConfig> findByConfigKey(String configKey);

    /**
     * 根据配置键前缀查询配置列表
     *
     * @param prefix 配置键前缀
     * @return 配置列表
     */
    List<SystemConfig> findByConfigKeyStartingWith(String prefix);
}
