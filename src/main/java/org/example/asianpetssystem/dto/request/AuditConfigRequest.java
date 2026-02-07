package org.example.asianpetssystem.dto.request;

import lombok.Data;

import java.util.List;

@Data
public class AuditConfigRequest {

    private Boolean autoAudit;

    private List<String> requireMaterials;

    private List<FlowStep> auditFlow;

    @Data
    public static class FlowStep {
        private Integer step;
        private String name;
        private String description;
    }
}
