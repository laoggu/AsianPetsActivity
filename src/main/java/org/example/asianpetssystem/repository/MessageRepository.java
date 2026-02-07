package org.example.asianpetssystem.repository;

import org.example.asianpetssystem.entity.Message;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface MessageRepository extends JpaRepository<Message, Long> {

    Page<Message> findByTypeAndStatus(String type, String status, Pageable pageable);

    List<Message> findByStatusAndScheduledTimeBefore(String status, LocalDateTime time);
}
