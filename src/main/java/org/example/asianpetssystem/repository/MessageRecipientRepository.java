package org.example.asianpetssystem.repository;

import org.example.asianpetssystem.entity.MessageRecipient;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MessageRecipientRepository extends JpaRepository<MessageRecipient, Long> {

    Page<MessageRecipient> findByMemberId(Long memberId, Pageable pageable);

    List<MessageRecipient> findByMemberId(Long memberId);

    List<MessageRecipient> findByMemberIdAndStatus(Long memberId, String status);

    Optional<MessageRecipient> findByMessageIdAndMemberId(Long messageId, Long memberId);

    @Query("SELECT COUNT(r) FROM MessageRecipient r WHERE r.memberId = ?1 AND r.status = 'UNREAD'")
    long countUnreadByMemberId(Long memberId);

    long countByMemberId(Long memberId);
}
