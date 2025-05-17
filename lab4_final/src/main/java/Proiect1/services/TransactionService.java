package Proiect1.services;

import Proiect1.dtos.TransactionDTO;
import org.springframework.stereotype.Service;

@Service
public interface TransactionService {
    TransactionDTO addTransaction(TransactionDTO transactionDTO);
}
