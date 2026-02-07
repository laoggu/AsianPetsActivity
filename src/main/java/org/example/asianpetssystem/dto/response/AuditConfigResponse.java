package org.example.asianpetssystem.dto.response;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class AuditConfigResponse {

    private Long id;
    private Boolean autoAudit;
    private List<String> requireMaterials;
    private List<FlowStepResponse> auditFlow;
    private LocalDateTime updatedAt;

    @Data
    public static class FlowStepResponse {
        private Integer step;
        private String name;
        private String description;
    }
}
