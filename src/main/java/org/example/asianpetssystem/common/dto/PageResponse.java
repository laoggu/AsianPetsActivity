package org.example.asianpetssystem.common.dto;

import lombok.Data;

import java.util.List;

@Data
public class PageResponse<T> {
    private List<T> content;
    private Long totalElements;
    private Integer totalPages;
    private Integer number;
    private Integer size;
    private Boolean first;
    private Boolean last;
}
