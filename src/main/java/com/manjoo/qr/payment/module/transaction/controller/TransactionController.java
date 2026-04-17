package com.manjoo.qr.payment.module.transaction.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
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
    
    private final ObjectMapper objectMapper = new ObjectMapper();

    @PostMapping("/qr/generate")
    public ResponseEntity<?> generateQr(
            @RequestHeader("X-Signature") String signature,
            @RequestBody GenerateQrRequestDto request,
            HttpServletRequest servletRequest) {

        try {
            String jsonPayload = objectMapper.writeValueAsString(request);

            if (!signatureUtil.isValid(jsonPayload, signature)) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }
        } catch (JsonProcessingException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
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

        try {
            String jsonPayload = objectMapper.writeValueAsString(request);

            if (!signatureUtil.isValid(jsonPayload, signature)) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }
        } catch (JsonProcessingException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
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

    @GetMapping("/qr/transactions")
    @CrossOrigin(origins = "*")
    public ResponseEntity<?> getQrTransactions(
            @RequestHeader("X-Signature") String signature
    ) {
        if (!signatureUtil.isValid("GET_ALL", signature)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        Map<String, Object> response = new HashMap<>();
        response.put("responseCode", "2004700");
        response.put("data", transactionService.findAllTransactions());

        return ResponseEntity.ok(response);
    }
}
