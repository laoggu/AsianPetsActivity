package org.example.asianpetssystem.repository;

import org.example.asianpetssystem.entity.ActivityEvaluation;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ActivityEvaluationRepository extends JpaRepository<ActivityEvaluation, Long> {

    Page<ActivityEvaluation> findByActivityId(Long activityId, Pageable pageable);

    List<ActivityEvaluation> findByActivityId(Long activityId);

    Optional<ActivityEvaluation> findByActivityIdAndMemberId(Long activityId, Long memberId);

    boolean existsByActivityIdAndMemberId(Long activityId, Long memberId);

    @Query("SELECT AVG(e.overallRating) FROM ActivityEvaluation e WHERE e.activityId = ?1")
    Double calculateAverageRating(Long activityId);

    @Query("SELECT COUNT(e) FROM ActivityEvaluation e WHERE e.activityId = ?1")
    Long countByActivityId(Long activityId);

    @Query("SELECT e.overallRating, COUNT(e) FROM ActivityEvaluation e WHERE e.activityId = ?1 GROUP BY e.overallRating")
    List<Object[]> getRatingDistribution(Long activityId);
}
