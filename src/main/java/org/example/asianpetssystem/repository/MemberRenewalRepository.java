package org.example.asianpetssystem.repository;

import org.example.asianpetssystem.entity.MemberRenewal;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface MemberRenewalRepository extends JpaRepository<MemberRenewal, Long> {

    Page<MemberRenewal> findByMemberId(Long memberId, Pageable pageable);

    List<MemberRenewal> findByMemberId(Long memberId);

    Page<MemberRenewal> findByStatus(String status, Pageable pageable);

    Optional<MemberRenewal> findByRenewalNo(String renewalNo);

    List<MemberRenewal> findByStatusAndPaymentTimeBefore(String status, LocalDateTime time);

    long countByMemberIdAndStatus(Long memberId, String status);
}
