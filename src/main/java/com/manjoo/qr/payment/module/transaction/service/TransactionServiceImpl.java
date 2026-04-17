package com.manjoo.qr.payment.module.transaction.service;

import com.manjoo.qr.payment.module.transaction.dto.GenerateQrRequestDto;
import com.manjoo.qr.payment.module.transaction.dto.PaymentCallbackRequestDto;
import com.manjoo.qr.payment.module.transaction.entity.Transaction;
import com.manjoo.qr.payment.module.transaction.repository.TransactionRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Transactional
public class TransactionServiceImpl implements TransactionService {
    @Autowired
    private TransactionRepository transactionRepository;

    @Override
    public Map<String, Object> generateQr(GenerateQrRequestDto request) {
        String internalRef = "A" + String.format("%010d", System.currentTimeMillis() % 10000000000L);

        Transaction txn = new Transaction();
        txn.setMerchantId(request.getMerchantId());
        txn.setAmount(Double.parseDouble(request.getAmount().getValue()));
        txn.setPartnerReferenceNumber(request.getPartnerReferenceNo());
        txn.setReferenceNumber(internalRef);
        txn.setStatus("PENDING");
        txn.setTransactionDate(LocalDateTime.now());
        txn.setQrContent("00020101021226620015ID.CO.MANJO.WWW...");

        transactionRepository.save(txn);

        Map<String, Object> response = new HashMap<>();
        response.put("responseCode", "2004700");
        response.put("responseMessage", "Successful");
        response.put("referenceNo", internalRef);
        response.put("partnerReferenceNo", request.getPartnerReferenceNo());
        response.put("qrContent", txn.getQrContent());

        return response;
    }

    @Override
    public Map<String, Object> paymentCallback(PaymentCallbackRequestDto request) {
        Transaction transaction = transactionRepository.findByReferenceNumber(request.getOriginalReferenceNo())
                .orElseThrow(() -> new RuntimeException("Transaction not found"));

        transaction.setStatus(request.getTransactionStatusDesc());
        transaction.setPaidDate(OffsetDateTime.parse(request.getPaidTime()).toLocalDateTime());

        transactionRepository.save(transaction);

        Map<String, Object> response = new HashMap<>();
        response.put("responseCode", "2005100");
        response.put("responseMessage", "Successful");
        response.put("transactionStatusDesc", transaction.getStatus());

        return response;
    }

    @Override
    public List<Transaction> findAllTransactions() {
        return transactionRepository.findAll();
    }
}
