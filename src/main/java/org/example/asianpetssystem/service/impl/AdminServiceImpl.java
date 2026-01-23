// src/main/java/org/example/asianpetssystem/service/impl/AdminServiceImpl.java
package org.example.asianpetssystem.service.impl;

import org.example.asianpetssystem.common.enums.BusinessErrorEnum;
import org.example.asianpetssystem.common.enums.MemberLevel;
import org.example.asianpetssystem.common.enums.MemberStatus;
import org.example.asianpetssystem.dto.request.AuditRequest;
import org.example.asianpetssystem.dto.response.ApplyListResponse;
import org.example.asianpetssystem.entity.*;
import org.example.asianpetssystem.exception.BusinessException;
import org.example.asianpetssystem.repository.AdminUserRepository;
import org.example.asianpetssystem.repository.AuditLogRepository;
import org.example.asianpetssystem.repository.ContactRepository;
import org.example.asianpetssystem.repository.MemberRepository;
import org.example.asianpetssystem.security.AuthenticationFacade; // 新增依赖
import org.example.asianpetssystem.service.AdminService;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class AdminServiceImpl implements AdminService {

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private ContactRepository contactRepository;

    @Autowired
    private AuditLogRepository auditLogRepository;

    @Autowired
    private AdminUserRepository adminUserRepository;

    @Autowired
    private AuthenticationFacade authenticationFacade; // 注入认证门面

    @Override
    public List<ApplyListResponse> getPendingApplications(int page, int size, String status) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Member> members;

        if (status != null && !status.isEmpty()) {
            MemberStatus memberStatus = MemberStatus.valueOf(status.toUpperCase());
            members = memberRepository.findByStatus(memberStatus, pageable);
        } else {
            members = memberRepository.findByStatus(MemberStatus.PENDING, pageable);
        }

        return members.getContent().stream()
                .map(this::convertToApplyListResponse)
                .collect(Collectors.toList());
    }

    @Override
    public void auditApplication(Long id, AuditRequest request) {
        Member member = memberRepository.findById(id)
                .orElseThrow(() -> new BusinessException(BusinessErrorEnum.MEMBER_NOT_FOUND));

        // 获取当前操作员ID
        Long operatorId = getCurrentOperatorId();

        // 根据审核操作更新会员状态
        switch (request.getAction()) {
            case APPROVE:
                member.setStatus(MemberStatus.APPROVED);
                member.setLevel(MemberLevel.REGULAR); // 新会员默认普通会员
                member.setExpireAt(LocalDateTime.now().plusYears(1)); // 默认一年有效期
                break;
            case REJECT:
                member.setStatus(MemberStatus.REJECTED);
                break;
            case SUPPLEMENT:
                member.setStatus(MemberStatus.PENDING); // 保持待审核状态，等待补充材料
                break;
        }

        memberRepository.save(member);

        // 记录审核日志
        AuditLog auditLog = new AuditLog();
        auditLog.setMemberId(member.getId());
        auditLog.setOperatorId(operatorId); // 使用从安全上下文获取的操作员ID
        auditLog.setAction(request.getAction());
        auditLog.setRemark(request.getRemark());
        auditLog.setCreatedAt(LocalDateTime.now());
        auditLogRepository.save(auditLog);
    }

    @Override
    public Resource exportMembersToExcel() {
        List<Member> members = memberRepository.findAll();

        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Members");

            // 创建标题行
            Row headerRow = sheet.createRow(0);
            String[] headers = {"ID", "公司名称", "信用代码", "会员等级", "状态", "到期时间", "创建时间", "更新时间"};
            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
            }

            // 填充数据
            int rowNum = 1;
            for (Member member : members) {
                Row row = sheet.createRow(rowNum++);

                row.createCell(0).setCellValue(member.getId());
                row.createCell(1).setCellValue(member.getCompanyName());
                row.createCell(2).setCellValue(member.getCreditCode());
                row.createCell(3).setCellValue(member.getLevel() != null ? member.getLevel().getCode() : "");
                row.createCell(4).setCellValue(member.getStatus() != null ? member.getStatus().getCode() : "");
                row.createCell(5).setCellValue(member.getExpireAt() != null ? member.getExpireAt().toString() : "");
                row.createCell(6).setCellValue(member.getCreatedAt() != null ? member.getCreatedAt().toString() : "");

                // 添加更新时间列
                row.createCell(7).setCellValue(member.getUpdatedAt() != null ? member.getUpdatedAt().toString() : "");
            }

            // 自动调整列宽
            for (int i = 0; i < headers.length; i++) {
                sheet.autoSizeColumn(i);
            }

            // 将工作簿转换为字节数组
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            workbook.write(outputStream);

            return new ByteArrayResource(outputStream.toByteArray());
        } catch (IOException e) {
            throw new RuntimeException("导出会员数据失败", e);
        }
    }

    @Override
    public void deleteMember(Long id) {
        Member member = memberRepository.findById(id)
                .orElseThrow(() -> new BusinessException(BusinessErrorEnum.MEMBER_NOT_FOUND));

        // 逻辑删除：更新状态为已暂停（实际业务中可能需要专门的删除状态）
        member.setStatus(MemberStatus.SUSPENDED);
        memberRepository.save(member);
    }

    @Override
    public void suspendMember(Long id) {
        Member member = memberRepository.findById(id)
                .orElseThrow(() -> new BusinessException(BusinessErrorEnum.MEMBER_NOT_FOUND));

        member.setStatus(MemberStatus.SUSPENDED);
        memberRepository.save(member);
    }

    @Override
    public void activateMember(Long id) {
        Member member = memberRepository.findById(id)
                .orElseThrow(() -> new BusinessException(BusinessErrorEnum.MEMBER_NOT_FOUND));

        member.setStatus(MemberStatus.APPROVED);
        memberRepository.save(member);
    }

    /**
     * 获取当前操作员ID
     */
    private Long getCurrentOperatorId() {
        String currentUsername = authenticationFacade.getCurrentUsername();
        if (currentUsername == null) {
            throw new BusinessException("无法获取当前操作员信息");
        }

        AdminUser currentUser = adminUserRepository.findByUsername(currentUsername)
            .orElseThrow(() -> new BusinessException("当前操作员不存在"));

        return currentUser.getId();
    }

    private ApplyListResponse convertToApplyListResponse(Member member) {
        ApplyListResponse response = new ApplyListResponse();
        response.setId(member.getId());
        response.setCompanyName(member.getCompanyName());
        response.setCreditCode(member.getCreditCode());
        response.setStatus(member.getStatus());
        response.setApplyTime(member.getCreatedAt());

        // 获取主要联系人信息
        List<Contact> contacts = contactRepository.findByMemberIdAndIsPrimaryTrue(member.getId());
        if (!contacts.isEmpty()) {
            Contact primaryContact = contacts.get(0);
            response.setPrimaryContactName(primaryContact.getName());
            response.setPrimaryContactMobile(primaryContact.getMobile());
        }

        return response;
    }
}
