package org.example.asianpetssystem.service;

import org.example.asianpetssystem.common.dto.PageRequest;
import org.example.asianpetssystem.common.dto.PageResponse;
import org.example.asianpetssystem.dto.request.RenewalCreateRequest;
import org.example.asianpetssystem.dto.request.RenewalPaymentRequest;
import org.example.asianpetssystem.dto.response.RenewalResponse;

import java.util.List;

public interface MemberRenewalService {

    /**
     * 创建续费记录
     */
    RenewalResponse createRenewal(RenewalCreateRequest request);

    /**
     * 获取续费列表
     */
    PageResponse<RenewalResponse> getRenewalList(Long memberId, String status, PageRequest pageRequest);

    /**
     * 获取续费详情
     */
    RenewalResponse getRenewalById(Long id);

    /**
     * 处理续费支付
     */
    RenewalResponse processPayment(Long id, RenewalPaymentRequest request);

    /**
     * 取消续费
     */
    void cancelRenewal(Long id);

    /**
     * 获取会员的续费统计
     */
    List<RenewalResponse> getMemberRenewals(Long memberId);

    /**
     * 获取需要发送续费提醒的会员
     */
    List<Long> getMembersNeedRenewalReminder(int daysBeforeExpire);
}
