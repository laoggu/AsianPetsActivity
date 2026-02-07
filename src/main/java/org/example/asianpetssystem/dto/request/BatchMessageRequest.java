package org.example.asianpetssystem.dto.request;

import lombok.Data;

import java.util.List;

@Data
public class BatchMessageRequest {
    private List<Long> memberIds;
    private String title;
    private String content;
    private String type;
}
