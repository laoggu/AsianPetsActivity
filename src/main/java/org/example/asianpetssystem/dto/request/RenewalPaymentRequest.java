package org.example.asianpetssystem.dto.request;

import lombok.Data;

@Data
public class RenewalPaymentRequest {
    private String paymentMethod;
    private String transactionNo;
}
