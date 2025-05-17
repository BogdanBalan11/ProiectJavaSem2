package Proiect1.domain;

import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Entity
public class Goal {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String goalName;
    private BigDecimal targetAmount;
    private BigDecimal savedAmount;
    private LocalDate deadline;

    @ManyToOne
    @JoinColumn(name = "app_user_id")
    private User user;
}
