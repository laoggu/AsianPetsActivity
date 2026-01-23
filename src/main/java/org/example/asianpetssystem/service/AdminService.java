package org.example.asianpetssystem.service;

import org.example.asianpetssystem.dto.request.AuditRequest;
import org.example.asianpetssystem.dto.response.ApplyListResponse;
import org.springframework.core.io.Resource;

import java.util.List;

public interface AdminService {

    /**
     * 获取待审核列表
     */
    List<ApplyListResponse> getPendingApplications(int page, int size, String status);

    /**
     * 审核会员申请
     */
    void auditApplication(Long id, AuditRequest request);

    /**
     * 导出会员信息到Excel
     */
    Resource exportMembersToExcel();

    /**
     * 删除会员
     */
    void deleteMember(Long id);

    /**
     * 暂停会员资格
     */
    void suspendMember(Long id);

    /**
     * 激活会员资格
     */
    void activateMember(Long id);
}
