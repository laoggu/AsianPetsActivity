package org.example.asianpetssystem.repository;

import org.example.asianpetssystem.entity.ActivityMaterial;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ActivityMaterialRepository extends JpaRepository<ActivityMaterial, Long> {

    List<ActivityMaterial> findByActivityIdOrderBySortOrderAsc(Long activityId);

    List<ActivityMaterial> findByActivityIdAndTypeOrderBySortOrderAsc(Long activityId, String type);

    long countByActivityId(Long activityId);

    long countByActivityIdAndType(Long activityId, String type);
}
