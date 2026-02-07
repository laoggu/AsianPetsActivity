package org.example.asianpetssystem.service.impl;

import org.example.asianpetssystem.common.dto.PageRequest;
import org.example.asianpetssystem.common.dto.PageResponse;
import org.example.asianpetssystem.common.enums.BusinessErrorEnum;
import org.example.asianpetssystem.common.enums.RenewalStatus;
import org.example.asianpetssystem.dto.request.RenewalCreateRequest;
import org.example.asianpetssystem.dto.request.RenewalPaymentRequest;
import org.example.asianpetssystem.dto.response.RenewalResponse;
import org.example.asianpetssystem.entity.Member;
import org.example.asianpetssystem.entity.MemberRenewal;
import org.example.asianpetssystem.exception.BusinessException;
import org.example.asianpetssystem.repository.MemberRenewalRepository;
import org.example.asianpetssystem.repository.MemberRepository;
import org.example.asianpetssystem.service.MemberRenewalService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class MemberRenewalServiceImpl implements MemberRenewalService {

    private static final Logger logger = LoggerFactory.getLogger(MemberRenewalServiceImpl.class);

    @Autowired
    private MemberRenewalRepository renewalRepository;

    @Autowired
    private MemberRepository memberRepository;

    @Override
    public RenewalResponse createRenewal(RenewalCreateRequest request) {
        logger.info("创建续费记录 - memberId={}", request.getMemberId());

        Member member = memberRepository.findById(request.getMemberId())
                .orElseThrow(() -> new BusinessException(BusinessErrorEnum.MEMBER_NOT_FOUND));

        MemberRenewal renewal = new MemberRenewal();
        renewal.setMemberId(request.getMemberId());
        renewal.setRenewalNo(generateRenewalNo());
        renewal.setOriginalExpireDate(member.getExpireAt());
        renewal.setNewExpireDate(request.getNewExpireDate());
        renewal.setAmount(request.getAmount());
        renewal.setLevel(request.getLevel());
        renewal.setStatus(RenewalStatus.PENDING_PAYMENT);
        renewal.setRemark(request.getRemark());
        renewal.setCreatedAt(LocalDateTime.now());

        renewalRepository.save(renewal);

        logger.info("续费记录创建成功 - renewalNo={}", renewal.getRenewalNo());
        return convertToResponse(renewal, member);
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<RenewalResponse> getRenewalList(Long memberId, String status, PageRequest pageRequest) {
        Pageable pageable = org.springframework.data.domain.PageRequest.of(
                pageRequest.getPage(), pageRequest.getSize());
        
        Page<MemberRenewal> page;
        if (memberId != null) {
            page = renewalRepository.findByMemberId(memberId, pageable);
        } else if (status != null) {
            page = renewalRepository.findByStatus(status, pageable);
        } else {
            page = renewalRepository.findAll(pageable);
        }

        List<RenewalResponse> content = page.getContent().stream()
                .map(r -> {
                    Member member = memberRepository.findById(r.getMemberId()).orElse(null);
                    return convertToResponse(r, member);
                })
                .collect(Collectors.toList());

        PageResponse<RenewalResponse> response = new PageResponse<>();
        response.setContent(content);
        response.setTotalElements(page.getTotalElements());
        response.setTotalPages(page.getTotalPages());
        response.setNumber(page.getNumber());
        response.setSize(page.getSize());
        return response;
    }

    @Override
    @Transactional(readOnly = true)
    public RenewalResponse getRenewalById(Long id) {
        MemberRenewal renewal = renewalRepository.findById(id)
                .orElseThrow(() -> new BusinessException("续费记录不存在"));
        Member member = memberRepository.findById(renewal.getMemberId()).orElse(null);
        return convertToResponse(renewal, member);
    }

    @Override
    public RenewalResponse processPayment(Long id, RenewalPaymentRequest request) {
        logger.info("处理续费支付 - renewalId={}", id);

        MemberRenewal renewal = renewalRepository.findById(id)
                .orElseThrow(() -> new BusinessException("续费记录不存在"));

        if (renewal.getStatus() != RenewalStatus.PENDING_PAYMENT) {
            throw new BusinessException("续费记录状态不正确");
        }

        renewal.setStatus(RenewalStatus.COMPLETED);
        renewal.setPaymentMethod(request.getPaymentMethod());
        renewal.setPaymentTime(LocalDateTime.now());
        renewal.setTransactionNo(request.getTransactionNo());

        renewalRepository.save(renewal);

        // 更新会员到期时间
        Member member = memberRepository.findById(renewal.getMemberId())
                .orElseThrow(() -> new BusinessException(BusinessErrorEnum.MEMBER_NOT_FOUND));
        member.setExpireAt(renewal.getNewExpireDate());
        memberRepository.save(member);

        logger.info("续费支付完成 - renewalNo={}, memberId={}", renewal.getRenewalNo(), renewal.getMemberId());
        return convertToResponse(renewal, member);
    }

    @Override
    public void cancelRenewal(Long id) {
        MemberRenewal renewal = renewalRepository.findById(id)
                .orElseThrow(() -> new BusinessException("续费记录不存在"));

        if (renewal.getStatus() == RenewalStatus.COMPLETED) {
            throw new BusinessException("已完成的续费不能取消");
        }

        renewal.setStatus(RenewalStatus.CANCELLED);
        renewalRepository.save(renewal);

        logger.info("续费记录已取消 - renewalId={}", id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<RenewalResponse> getMemberRenewals(Long memberId) {
        return renewalRepository.findByMemberId(memberId).stream()
                .map(r -> {
                    Member member = memberRepository.findById(r.getMemberId()).orElse(null);
                    return convertToResponse(r, member);
                })
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<Long> getMembersNeedRenewalReminder(int daysBeforeExpire) {
        LocalDateTime targetDate = LocalDateTime.now().plusDays(daysBeforeExpire);
        // 查询即将到期的会员
        return memberRepository.findByExpireAtBetween(
                targetDate.minusDays(1), targetDate)
                .stream()
                .map(Member::getId)
                .collect(Collectors.toList());
    }

    private String generateRenewalNo() {
        return "RN" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss")) 
                + String.format("%04d", (int)(Math.random() * 10000));
    }

    private RenewalResponse convertToResponse(MemberRenewal renewal, Member member) {
        RenewalResponse response = new RenewalResponse();
        response.setId(renewal.getId());
        response.setMemberId(renewal.getMemberId());
        response.setMemberName(member != null ? member.getCompanyName() : "");
        response.setRenewalNo(renewal.getRenewalNo());
        response.setOriginalExpireDate(renewal.getOriginalExpireDate());
        response.setNewExpireDate(renewal.getNewExpireDate());
        response.setAmount(renewal.getAmount());
        response.setLevel(renewal.getLevel());
        response.setStatus(renewal.getStatus().name());
        response.setStatusName(renewal.getStatus().getDescription());
        response.setPaymentMethod(renewal.getPaymentMethod());
        response.setPaymentTime(renewal.getPaymentTime());
        response.setTransactionNo(renewal.getTransactionNo());
        response.setRemark(renewal.getRemark());
        response.setCreatedAt(renewal.getCreatedAt());
        return response;
    }
}
