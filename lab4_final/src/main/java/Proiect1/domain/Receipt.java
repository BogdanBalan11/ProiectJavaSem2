package Proiect1.domain;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
public class Receipt {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String details;

    @OneToOne
    @JoinColumn(name = "transaction_id")
    private Transaction transaction;

}
