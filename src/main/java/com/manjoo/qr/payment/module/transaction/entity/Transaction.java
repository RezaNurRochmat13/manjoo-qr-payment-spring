package com.manjoo.qr.payment.module.transaction.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Table(name = "transactions")
@Data
public class Transaction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String merchantId;
    private Double amount;
    private String trxId;
    private String partnerReferenceNumber;

    @Column(unique = true)
    private String referenceNumber;

    private String status;
    private LocalDateTime transactionDate;
    private LocalDateTime paidDate;
    private String qrContent;
}
