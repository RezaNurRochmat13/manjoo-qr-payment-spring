package com.manjoo.qr.payment.module.transaction.service;

import com.manjoo.qr.payment.module.transaction.dto.GenerateQrRequestDto;
import com.manjoo.qr.payment.module.transaction.dto.PaymentCallbackRequestDto;

import java.util.Map;

public interface TransactionService {
    Map<String, Object> generateQr(GenerateQrRequestDto request);
    Map<String, Object> paymentCallback(PaymentCallbackRequestDto request);
}
