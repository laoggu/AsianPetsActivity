package org.example.asianpetssystem.service;

import org.example.asianpetssystem.dto.request.MemberRightCreateRequest;
import org.example.asianpetssystem.dto.request.MemberRightUpdateRequest;
import org.example.asianpetssystem.dto.response.LevelRightsResponse;
import org.example.asianpetssystem.dto.response.MemberRightResponse;

import java.util.List;

public interface MemberRightService {

    /**
     * 按等级分组获取权益
     *
     * @return 按等级分组的权益列表
     */
    List<LevelRightsResponse> getRightsByLevel();

    /**
     * 获取指定等级的权益
     *
     * @param level 会员等级
     * @return 权益列表
     */
    List<MemberRightResponse> getRightsByLevel(String level);

    /**
     * 创建权益
     *
     * @param request 创建请求
     * @return 创建的权益响应
     */
    MemberRightResponse createRight(MemberRightCreateRequest request);

    /**
     * 更新权益
     *
     * @param id      权益ID
     * @param request 更新请求
     * @return 更新后的权益响应
     */
    MemberRightResponse updateRight(Long id, MemberRightUpdateRequest request);

    /**
     * 删除权益
     *
     * @param id 权益ID
     */
    void deleteRight(Long id);

    /**
     * 更新等级权益配置
     *
     * @param level    会员等级
     * @param rightIds 权益ID列表
     */
    void updateLevelRights(String level, List<Long> rightIds);
}
