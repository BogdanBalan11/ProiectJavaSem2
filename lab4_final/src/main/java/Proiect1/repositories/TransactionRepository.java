package Proiect1.repositories;


import Proiect1.domain.Transaction;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface TransactionRepository extends CrudRepository<Transaction, Long> {
    List<Transaction> findByUserId(Long userId);
    Page<Transaction> findByUserId(Long userId, Pageable pageable);
}
