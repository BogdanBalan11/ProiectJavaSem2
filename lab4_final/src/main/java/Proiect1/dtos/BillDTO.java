package Proiect1.dtos;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class BillDTO {
    private Long id;
    private String billName;
    private BigDecimal amount;
    private LocalDate nextDueDate;
    private String description;
    private Long userId;

}
