// src/main/java/org/example\asianpetssystem\service\impl\MemberServiceImpl.java
package org.example.asianpetssystem.service.impl;

import org.example.asianpetssystem.common.enums.BusinessErrorEnum;
import org.example.asianpetssystem.common.enums.MemberLevel;
import org.example.asianpetssystem.common.enums.MemberStatus;
import org.example.asianpetssystem.dto.request.MemberApplyRequest;
import org.example.asianpetssystem.dto.request.UpdateProfileRequest;
import org.example.asianpetssystem.dto.response.MemberStatusResponse;
import org.example.asianpetssystem.dto.response.ProfileResponse;
import org.example.asianpetssystem.entity.*;
import org.example.asianpetssystem.exception.BusinessException;
import org.example.asianpetssystem.repository.AttachmentRepository;
import org.example.asianpetssystem.repository.ContactRepository;
import org.example.asianpetssystem.repository.MemberRepository;
import org.example.asianpetssystem.service.MemberService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class MemberServiceImpl implements MemberService {

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private ContactRepository contactRepository;

    @Autowired
    private AttachmentRepository attachmentRepository;

    @Override
    public void applyForMember(MemberApplyRequest request) {
        // 检查信用代码是否已存在
        if (memberRepository.existsByCreditCode(request.getCreditCode())) {
            throw new BusinessException(BusinessErrorEnum.CREDIT_CODE_EXISTS);
        }

        // 创建会员
        Member member = new Member();
        member.setCompanyName(request.getCompanyName());
        member.setCreditCode(request.getCreditCode());
        member.setLevel(MemberLevel.REGULAR);
        member.setStatus(MemberStatus.PENDING);
        member.setCreatedAt(LocalDateTime.now());

        Member savedMember = memberRepository.save(member);

        // 保存联系人
        for (MemberApplyRequest.ContactRequest contactReq : request.getContacts()) {
            Contact contact = new Contact();
            contact.setMember(savedMember);
            contact.setName(contactReq.getName());
            contact.setMobile(maskPhone(contactReq.getMobile()));
            contact.setEmail(contactReq.getEmail());
            contact.setIsPrimary(contactReq.getIsPrimary() != null && contactReq.getIsPrimary());
            contactRepository.save(contact);
        }

        // 保存附件
        for (MemberApplyRequest.AttachmentRequest attachmentReq : request.getAttachments()) {
            Attachment attachment = new Attachment();
            attachment.setMember(savedMember);
            attachment.setType(attachmentReq.getType());
            attachment.setOssKey(attachmentReq.getOssKey());
            attachmentRepository.save(attachment);
        }
    }

    @Override
    public MemberStatusResponse getMemberStatus(Long id) {
        Member member = memberRepository.findById(id)
                .orElseThrow(() -> new BusinessException(BusinessErrorEnum.MEMBER_NOT_FOUND));

        return new MemberStatusResponse(
                member.getStatus(),
                member.getLevel(),
                member.getExpireAt(),
                member.getCompanyName(),
                member.getCreditCode()
        );
    }

    @Override
    public ProfileResponse getMemberProfile(String username) {
        // 修复：使用 findById 方法，它返回 Optional
        Member member = memberRepository.findByCreditCode(username)
                .orElseThrow(() -> new BusinessException(BusinessErrorEnum.MEMBER_NOT_FOUND));

        ProfileResponse profile = new ProfileResponse();
        profile.setId(member.getId());
        profile.setCompanyName(member.getCompanyName());
        profile.setCreditCode(member.getCreditCode());
        profile.setLevel(member.getLevel());
        profile.setStatus(member.getStatus());
        profile.setExpireAt(member.getExpireAt());
        profile.setCreatedAt(member.getCreatedAt());

        // 获取联系人信息
        List<Contact> contacts = contactRepository.findByMemberId(member.getId());
        List<ProfileResponse.ContactInfo> contactInfos = contacts.stream().map(c -> {
            ProfileResponse.ContactInfo info = new ProfileResponse.ContactInfo();
            info.setName(c.getName());
            info.setMobile(c.getMobile());
            info.setEmail(c.getEmail());
            info.setIsPrimary(c.getIsPrimary());
            return info;
        }).collect(Collectors.toList());

        profile.setContacts(contactInfos);

        return profile;
    }

    @Override
    public void updateMemberProfile(UpdateProfileRequest request, String username) {
        Member member = memberRepository.findByCreditCode(username)
                .orElseThrow(() -> new BusinessException(BusinessErrorEnum.MEMBER_NOT_FOUND));

        if (request.getCompanyName() != null) {
            member.setCompanyName(request.getCompanyName());
        }

        memberRepository.save(member);

        // 更新主要联系人信息
        List<Contact> contacts = contactRepository.findByMemberIdAndIsPrimaryTrue(member.getId());
        if (!contacts.isEmpty()) {
            Contact primaryContact = contacts.get(0);
            if (request.getContactName() != null) {
                primaryContact.setName(request.getContactName());
            }
            if (request.getMobile() != null) {
                primaryContact.setMobile(maskPhone(request.getMobile()));
            }
            if (request.getEmail() != null) {
                primaryContact.setEmail(request.getEmail());
            }
            contactRepository.save(primaryContact);
        }
    }

    @Override
    public String uploadFile(MultipartFile file, AttachmentType type) {
        // 检查文件大小限制（例如：2MB）
        if (file.getSize() > 2 * 1024 * 1024) {
            throw new BusinessException("文件大小不能超过2MB");
        }

        // 检查文件名是否为空
        if (file.getOriginalFilename() == null || file.getOriginalFilename().isEmpty()) {
            throw new BusinessException("文件名不能为空");
        }

        // 模拟上传到OSS并返回key（暂时使用本地路径模拟）
        String fileName = "attachments/" + System.currentTimeMillis() + "_" + file.getOriginalFilename();
        // 实际上传逻辑需要根据使用的OSS服务进行实现
        return fileName;
    }

    private String maskPhone(String phone) {
        if (phone == null || phone.length() != 11) {
            return phone;
        }
        return phone.replaceAll("(\\d{3})\\d{4}(\\d{4})", "$1****$2");
    }
}
