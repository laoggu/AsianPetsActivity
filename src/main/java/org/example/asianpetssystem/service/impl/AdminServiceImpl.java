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
import org.example.asianpetssystem.security.AuthenticationFacade;
import org.example.asianpetssystem.service.AdminService;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

    private static final Logger logger = LoggerFactory.getLogger(AdminServiceImpl.class);

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private ContactRepository contactRepository;

    @Autowired
    private AuditLogRepository auditLogRepository;

    @Autowired
    private AdminUserRepository adminUserRepository;

    @Autowired
    private AuthenticationFacade authenticationFacade;

    @Override
    public List<ApplyListResponse> getPendingApplications(int page, int size, String status) {
        logger.info("开始获取待审核申请列表 - page={}, size={}, status={}", page, size, status);
        long startTime = System.currentTimeMillis();
        
        try {
            Pageable pageable = PageRequest.of(page, size);
            Page<Member> members;

            if (status != null && !status.isEmpty()) {
                MemberStatus memberStatus = MemberStatus.valueOf(status.toUpperCase());
                members = memberRepository.findByStatus(memberStatus, pageable);
            } else {
                members = memberRepository.findByStatus(MemberStatus.PENDING, pageable);
            }

            List<ApplyListResponse> result = members.getContent().stream()
                    .map(this::convertToApplyListResponse)
                    .collect(Collectors.toList());
            
            long duration = System.currentTimeMillis() - startTime;
            logger.info("获取待审核申请列表成功 - 返回 {} 条记录, 耗时: {}ms", result.size(), duration);
            return result;
        } catch (Exception e) {
            long duration = System.currentTimeMillis() - startTime;
            logger.error("获取待审核申请列表失败 - 耗时: {}ms, 错误: {}", duration, e.getMessage(), e);
            throw e;
        }
    }

    @Override
    public void auditApplication(Long id, AuditRequest request) {
        logger.info("开始审核会员申请 - ID={}, 操作={}", id, request.getAction());
        long startTime = System.currentTimeMillis();
        
        try {
            Member member = memberRepository.findById(id)
                    .orElseThrow(() -> new BusinessException(BusinessErrorEnum.MEMBER_NOT_FOUND));

            // 获取当前操作员ID
            Long operatorId = getCurrentOperatorId();

            // 根据审核操作更新会员状态
            switch (request.getAction()) {
                case APPROVE:
                    member.setStatus(MemberStatus.APPROVED);
                    member.setLevel(MemberLevel.REGULAR);
                    member.setExpireAt(LocalDateTime.now().plusYears(1));
                    logger.info("会员申请审批通过 - ID={}, 操作员ID={}", id, operatorId);
                    break;
                case REJECT:
                    member.setStatus(MemberStatus.REJECTED);
                    logger.info("会员申请被拒绝 - ID={}, 操作员ID={}", id, operatorId);
                    break;
                case SUPPLEMENT:
                    member.setStatus(MemberStatus.PENDING);
                    logger.info("要求会员补充材料 - ID={}, 操作员ID={}", id, operatorId);
                    break;
            }

            memberRepository.save(member);

            // 记录审核日志
            AuditLog auditLog = new AuditLog();
            auditLog.setMemberId(member.getId());
            auditLog.setOperatorId(operatorId);
            auditLog.setAction(request.getAction());
            auditLog.setRemark(request.getRemark());
            auditLog.setCreatedAt(LocalDateTime.now());
            auditLogRepository.save(auditLog);
            
            long duration = System.currentTimeMillis() - startTime;
            logger.info("会员申请审核完成 - ID={}, 操作={}, 耗时: {}ms", id, request.getAction(), duration);
        } catch (Exception e) {
            long duration = System.currentTimeMillis() - startTime;
            logger.error("会员申请审核失败 - ID={}, 操作={}, 耗时: {}ms, 错误: {}", 
                        id, request.getAction(), duration, e.getMessage(), e);
            throw e;
        }
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
        logger.info("开始删除会员 - ID={}", id);
        long startTime = System.currentTimeMillis();
        
        try {
            Member member = memberRepository.findById(id)
                    .orElseThrow(() -> new BusinessException(BusinessErrorEnum.MEMBER_NOT_FOUND));

            member.setStatus(MemberStatus.SUSPENDED);
            memberRepository.save(member);
            
            long duration = System.currentTimeMillis() - startTime;
            logger.info("会员删除成功 - ID={}, 公司名称: {}, 耗时: {}ms", id, member.getCompanyName(), duration);
        } catch (Exception e) {
            long duration = System.currentTimeMillis() - startTime;
            logger.error("会员删除失败 - ID={}, 耗时: {}ms, 错误: {}", id, duration, e.getMessage(), e);
            throw e;
        }
    }

    @Override
    public void suspendMember(Long id) {
        logger.info("开始暂停会员资格 - ID={}", id);
        long startTime = System.currentTimeMillis();
        
        try {
            Member member = memberRepository.findById(id)
                    .orElseThrow(() -> new BusinessException(BusinessErrorEnum.MEMBER_NOT_FOUND));

            member.setStatus(MemberStatus.SUSPENDED);
            memberRepository.save(member);
            
            long duration = System.currentTimeMillis() - startTime;
            logger.info("会员资格暂停成功 - ID={}, 公司名称: {}, 耗时: {}ms", id, member.getCompanyName(), duration);
        } catch (Exception e) {
            long duration = System.currentTimeMillis() - startTime;
            logger.error("会员资格暂停失败 - ID={}, 耗时: {}ms, 错误: {}", id, duration, e.getMessage(), e);
            throw e;
        }
    }

    @Override
    public void activateMember(Long id) {
        logger.info("开始激活会员资格 - ID={}", id);
        long startTime = System.currentTimeMillis();
        
        try {
            Member member = memberRepository.findById(id)
                    .orElseThrow(() -> new BusinessException(BusinessErrorEnum.MEMBER_NOT_FOUND));

            member.setStatus(MemberStatus.APPROVED);
            memberRepository.save(member);
            
            long duration = System.currentTimeMillis() - startTime;
            logger.info("会员资格激活成功 - ID={}, 公司名称: {}, 耗时: {}ms", id, member.getCompanyName(), duration);
        } catch (Exception e) {
            long duration = System.currentTimeMillis() - startTime;
            logger.error("会员资格激活失败 - ID={}, 耗时: {}ms, 错误: {}", id, duration, e.getMessage(), e);
            throw e;
        }
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