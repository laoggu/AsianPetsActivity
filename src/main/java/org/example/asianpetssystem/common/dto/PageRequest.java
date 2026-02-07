package org.example.asianpetssystem.common.dto;

import lombok.Data;

@Data
public class PageRequest {
    private Integer page = 0;
    private Integer size = 20;
    private String sortBy;
    private String sortDirection;
}
