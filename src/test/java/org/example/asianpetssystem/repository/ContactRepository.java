package org.example.asianpetssystem.repository;

import org.example.asianpetssystem.entity.Contact;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ContactRepository extends JpaRepository<Contact, Long> {
    List<Contact> findByMemberId(Long memberId);

    List<Contact> findByMemberIdAndIsPrimaryTrue(Long memberId);
}
