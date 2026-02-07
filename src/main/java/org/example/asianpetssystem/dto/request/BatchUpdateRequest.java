package org.example.asianpetssystem.dto.request;

import lombok.Data;

import java.util.List;

@Data
public class BatchUpdateRequest {
    private List<Long> ids;
    private String status;
    private String remark;
}
