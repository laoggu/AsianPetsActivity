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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

    private static final Logger logger = LoggerFactory.getLogger(MemberServiceImpl.class);

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private ContactRepository contactRepository;

    @Autowired
    private AttachmentRepository attachmentRepository;

    @Override
    public void applyForMember(MemberApplyRequest request) {
        logger.info("开始处理会员申请 - 公司名称: {}, 统一信用代码: {}", 
                   request.getCompanyName(), request.getCreditCode());
        long startTime = System.currentTimeMillis();
        
        try {
            // 检查信用代码是否已存在
            if (memberRepository.existsByCreditCode(request.getCreditCode())) {
                logger.warn("会员申请失败 - 统一信用代码已存在: {}", request.getCreditCode());
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
            logger.debug("会员基本信息保存成功 - ID: {}, 公司名称: {}", savedMember.getId(), savedMember.getCompanyName());

            // 保存联系人
            for (MemberApplyRequest.ContactRequest contactReq : request.getContacts()) {
                Contact contact = new Contact();
                contact.setMember(savedMember);
                contact.setName(contactReq.getName());
                contact.setMobile(maskPhone(contactReq.getMobile()));
                contact.setEmail(contactReq.getEmail());
                contact.setIsPrimary(contactReq.getIsPrimary() != null && contactReq.getIsPrimary());
                contactRepository.save(contact);
                logger.debug("联系人信息保存成功 - 姓名: {}, 手机号: {}", 
                           contactReq.getName(), maskPhone(contactReq.getMobile()));
            }

            // 保存附件
            for (MemberApplyRequest.AttachmentRequest attachmentReq : request.getAttachments()) {
                Attachment attachment = new Attachment();
                attachment.setMember(savedMember);
                attachment.setType(attachmentReq.getType());
                attachment.setOssKey(attachmentReq.getOssKey());
                attachmentRepository.save(attachment);
                logger.debug("附件信息保存成功 - 类型: {}, 存储路径: {}", 
                           attachmentReq.getType(), attachmentReq.getOssKey());
            }
            
            long duration = System.currentTimeMillis() - startTime;
            logger.info("会员申请处理成功 - 公司名称: {}, 耗时: {}ms", request.getCompanyName(), duration);
        } catch (Exception e) {
            long duration = System.currentTimeMillis() - startTime;
            logger.error("会员申请处理失败 - 公司名称: {}, 耗时: {}ms, 错误: {}", 
                        request.getCompanyName(), duration, e.getMessage(), e);
            throw e;
        }
    }

    @Override
    public MemberStatusResponse getMemberStatus(Long id) {
        logger.info("开始查询会员申请状态 - ID={}", id);
        long startTime = System.currentTimeMillis();
        
        try {
            Member member = memberRepository.findById(id)
                    .orElseThrow(() -> new BusinessException(BusinessErrorEnum.MEMBER_NOT_FOUND));

            MemberStatusResponse response = new MemberStatusResponse(
                    member.getStatus(),
                    member.getLevel(),
                    member.getExpireAt(),
                    member.getCompanyName(),
                    member.getCreditCode()
            );
            
            long duration = System.currentTimeMillis() - startTime;
            logger.info("会员申请状态查询成功 - ID={}, 状态: {}, 耗时: {}ms", 
                       id, member.getStatus(), duration);
            return response;
        } catch (Exception e) {
            long duration = System.currentTimeMillis() - startTime;
            logger.error("会员申请状态查询失败 - ID={}, 耗时: {}ms, 错误: {}", id, duration, e.getMessage(), e);
            throw e;
        }
    }

    @Override
    public ProfileResponse getMemberProfile(String username) {
        logger.info("开始获取会员个人信息 - 用户名: {}", username);
        long startTime = System.currentTimeMillis();
        
        try {
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
            
            long duration = System.currentTimeMillis() - startTime;
            logger.info("会员个人信息获取成功 - 用户名: {}, 公司: {}, 联系人数量: {}, 耗时: {}ms", 
                       username, member.getCompanyName(), contactInfos.size(), duration);
            return profile;
        } catch (Exception e) {
            long duration = System.currentTimeMillis() - startTime;
            logger.error("会员个人信息获取失败 - 用户名: {}, 耗时: {}ms, 错误: {}", 
                        username, duration, e.getMessage(), e);
            throw e;
        }
    }

    @Override
    public void updateMemberProfile(UpdateProfileRequest request, String username) {
        logger.info("开始更新会员个人信息 - 用户名: {}", username);
        long startTime = System.currentTimeMillis();
        
        try {
            Member member = memberRepository.findByCreditCode(username)
                    .orElseThrow(() -> new BusinessException(BusinessErrorEnum.MEMBER_NOT_FOUND));

            if (request.getCompanyName() != null) {
                member.setCompanyName(request.getCompanyName());
                logger.debug("更新公司名称: {}", request.getCompanyName());
            }

            memberRepository.save(member);

            // 更新主要联系人信息
            List<Contact> contacts = contactRepository.findByMemberIdAndIsPrimaryTrue(member.getId());
            if (!contacts.isEmpty()) {
                Contact primaryContact = contacts.get(0);
                boolean contactUpdated = false;
                
                if (request.getContactName() != null) {
                    primaryContact.setName(request.getContactName());
                    contactUpdated = true;
                    logger.debug("更新联系人姓名: {}", request.getContactName());
                }
                if (request.getMobile() != null) {
                    primaryContact.setMobile(maskPhone(request.getMobile()));
                    contactUpdated = true;
                    logger.debug("更新联系人手机: {}", maskPhone(request.getMobile()));
                }
                if (request.getEmail() != null) {
                    primaryContact.setEmail(request.getEmail());
                    contactUpdated = true;
                    logger.debug("更新联系人邮箱: {}", request.getEmail());
                }
                
                if (contactUpdated) {
                    contactRepository.save(primaryContact);
                }
            }
            
            long duration = System.currentTimeMillis() - startTime;
            logger.info("会员个人信息更新成功 - 用户名: {}, 耗时: {}ms", username, duration);
        } catch (Exception e) {
            long duration = System.currentTimeMillis() - startTime;
            logger.error("会员个人信息更新失败 - 用户名: {}, 耗时: {}ms, 错误: {}", 
                        username, duration, e.getMessage(), e);
            throw e;
        }
    }

    @Override
    public String uploadFile(MultipartFile file, AttachmentType type) {
        logger.info("开始上传附件 - 文件名: {}, 类型: {}, 大小: {} bytes", 
                   file.getOriginalFilename(), type, file.getSize());
        long startTime = System.currentTimeMillis();
        
        try {
            // 检查文件大小限制（例如：2MB）
            if (file.getSize() > 2 * 1024 * 1024) {
                logger.warn("文件上传失败 - 文件过大: {} bytes", file.getSize());
                throw new BusinessException("文件大小不能超过2MB");
            }

            // 检查文件名是否为空
            if (file.getOriginalFilename() == null || file.getOriginalFilename().isEmpty()) {
                logger.warn("文件上传失败 - 文件名为空");
                throw new BusinessException("文件名不能为空");
            }

            // 模拟上传到OSS并返回key（暂时使用本地路径模拟）
            String fileName = "attachments/" + System.currentTimeMillis() + "_" + file.getOriginalFilename();
            // 实际上传逻辑需要根据使用的OSS服务进行实现
            
            long duration = System.currentTimeMillis() - startTime;
            logger.info("附件上传成功 - 文件名: {}, 存储路径: {}, 耗时: {}ms", 
                       file.getOriginalFilename(), fileName, duration);
            return fileName;
        } catch (Exception e) {
            long duration = System.currentTimeMillis() - startTime;
            logger.error("附件上传失败 - 文件名: {}, 类型: {}, 耗时: {}ms, 错误: {}", 
                        file.getOriginalFilename(), type, duration, e.getMessage(), e);
            throw e;
        }
    }

    private String maskPhone(String phone) {
        if (phone == null || phone.length() != 11) {
            return phone;
        }
        return phone.replaceAll("(\\d{3})\\d{4}(\\d{4})", "$1****$2");
    }
}
