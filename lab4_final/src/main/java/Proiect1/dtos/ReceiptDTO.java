package Proiect1.dtos;

import lombok.Data;

@Data
public class ReceiptDTO {
    private Long id;
    private String details;
    private Long transactionId;
}

