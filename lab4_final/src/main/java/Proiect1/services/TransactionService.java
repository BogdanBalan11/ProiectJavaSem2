package Proiect1.services;

import Proiect1.dtos.TransactionDTO;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface TransactionService {
    TransactionDTO addTransaction(TransactionDTO transactionDTO);
    List<TransactionDTO> getUserTransactions(Long userId);
    TransactionDTO getTransactionById(Long id);
    void updateTransaction(Long id, TransactionDTO dto);
    void deleteTransaction(Long id);

}
