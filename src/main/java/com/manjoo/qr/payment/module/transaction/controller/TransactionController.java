package com.manjoo.qr.payment.module.transaction.controller;

import com.manjoo.qr.payment.module.transaction.dto.GenerateQrRequestDto;
import com.manjoo.qr.payment.module.transaction.dto.PaymentCallbackRequestDto;
import com.manjoo.qr.payment.module.transaction.service.TransactionServiceImpl;
import com.manjoo.qr.payment.utils.signature.SignatureUtil;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/transactions")
public class TransactionController {
    @Autowired
    private TransactionServiceImpl transactionService;

    @Autowired
    private SignatureUtil signatureUtil;

    @PostMapping("/qr/generate")
    public ResponseEntity<?> generateQr(
            @RequestHeader("X-Signature") String signature,
            @RequestBody GenerateQrRequestDto request,
            HttpServletRequest servletRequest) {

        if (!signatureUtil.isValid(request.toString(), signature)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        if (Double.parseDouble(request.getAmount().getValue()) <= 0) {
            return ResponseEntity.badRequest().body("Amount must be greater than 0");
        }

        return ResponseEntity.ok(transactionService.generateQr(request));
    }

    @PostMapping("/qr/payment")
    public ResponseEntity<?> paymentCallback(
            @RequestHeader("X-Signature") String signature,
            @RequestBody PaymentCallbackRequestDto request) {

        if (!signatureUtil.isValid(request.toString(), signature)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        try {
            return ResponseEntity.ok(transactionService.paymentCallback(request));
        } catch (RuntimeException e) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("responseCode", "4044700");
            errorResponse.put("responseMessage", "Transaction not found");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
        }
    }

    @GetMapping("/qr/status")
    public ResponseEntity<?> getStatus(
            @RequestHeader("X-Signature") String signature,
            @RequestParam("referenceNumber") String referenceNumber
    ) {
        if (!signatureUtil.isValid(referenceNumber, signature)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        Map<String, Object> response = new HashMap<>();
        response.put("responseCode", "4044700");
        response.put("data", transactionService.findTransactionByReferenceNumber(referenceNumber));

        return ResponseEntity.ok(response);
    }
}
