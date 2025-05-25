package Proiect1.dtos;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class TransactionDTO {
    private Long id;
    private BigDecimal amount;
    private LocalDate transactionDate;
    private String description;
    private String transactionType; // INCOME/EXPENSE
    private Long userId;
    private Long categoryId;
    private String categoryName;


}
