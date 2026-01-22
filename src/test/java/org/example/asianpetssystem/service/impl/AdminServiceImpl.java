package org.example.asianpetssystem.service.impl;

import org.example.asianpetssystem.common.enums.BusinessErrorEnum;
import org.example.asianpetssystem.dto.request.AuditRequest;
import org.example.asianpetssystem.dto.response.ApplyListResponse;
import org.example.asianpetssystem.entity.*;
import org.example.asianpetssystem.exception.BusinessException;
import org.example.asianpetssystem.repository.*;
import org.example.asianpetssystem.service.AdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

        return members.getContent().stream().map(this::convertToApplyListResponse).collect(Collectors.toList());
    }

    @Override
    public void auditApplication(Long id, AuditRequest request) {
        Member member = memberRepository.findById(id)
            .orElseThrow(() -> new BusinessException(BusinessErrorEnum.MEMBER_NOT_FOUND));

        // 根据审核操作更新会员状态
        switch (request.getAction()) {
            case APPROVE:
                member.setStatus(MemberStatus.APPROVED);
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
        auditLog.setOperatorId(1L); // 这里应该是当前操作员ID
        auditLog.setAction(request.getAction());
        auditLog.setRemark(request.getRemark());
        auditLog.setCreatedAt(LocalDateTime.now());
        auditLogRepository.save(auditLog);
    }

    @Override
    public Resource exportMembersToExcel() {
        // 这里应该实现Excel导出逻辑
        // 为了演示，返回一个占位资源
        return new ClassPathResource("templates/members_template.xlsx");
    }

    @Override
    public void deleteMember(Long id) {
        Member member = memberRepository.findById(id)
            .orElseThrow(() -> new BusinessException(BusinessErrorEnum.MEMBER_NOT_FOUND));

        // 逻辑删除：更新状态为已删除
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
