package org.example.asianpetssystem.repository;

import org.example.asianpetssystem.entity.MemberRight;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MemberRightRepository extends JpaRepository<MemberRight, Long> {

    /**
     * 根据等级和启用状态查询权益，按排序值升序
     *
     * @param level 会员等级
     * @return 权益列表
     */
    List<MemberRight> findByLevelAndIsActiveTrueOrderBySortOrderAsc(String level);

    /**
     * 查询所有启用的权益，按等级和排序值升序
     *
     * @return 权益列表
     */
    List<MemberRight> findByIsActiveTrueOrderByLevelAscSortOrderAsc();

    /**
     * 根据等级查询权益
     *
     * @param level 会员等级
     * @return 权益列表
     */
    List<MemberRight> findByLevel(String level);
}
