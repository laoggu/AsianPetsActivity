package org.example.asianpetssystem.dto.response;

import lombok.Data;

@Data
public class MessageStatsResponse {
    private Long totalSent;
    private Long totalReceived;
    private Double readRate;
    private Double deliveryRate;
}
