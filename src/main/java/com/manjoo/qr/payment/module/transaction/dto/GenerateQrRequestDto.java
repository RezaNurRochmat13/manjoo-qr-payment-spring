package com.manjoo.qr.payment.module.transaction.dto;

import lombok.Data;

@Data
public class GenerateQrRequestDto {
    private String partnerReferenceNo;
    private AmountDto amount;
    private String merchantId;
}
