package com.manjoo.qr.payment.module.transaction.dto;

import lombok.Data;

@Data
public class PaymentCallbackRequestDto {
    private String originalReferenceNo;
    private String originalPartnerReferenceNo;
    private String transactionStatusDesc;
    private String paidTime;
    private AmountDto amount;
}
