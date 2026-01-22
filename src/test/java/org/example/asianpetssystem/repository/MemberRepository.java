package org.example.asianpetssystem.repository;

import org.example.asianpetssystem.entity.Member;
import org.example.asianpetssystem.entity.MemberStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface MemberRepository extends JpaRepository<Member, Long> {
    Member findByCreditCode(String creditCode);

    @Query("SELECT m FROM Member m WHERE m.status = :status")
    Page<Member> findByStatus(MemberStatus status, Pageable pageable);

    boolean existsByCreditCode(String creditCode);
}
