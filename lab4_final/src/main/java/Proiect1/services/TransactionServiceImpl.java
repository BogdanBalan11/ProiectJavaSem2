package Proiect1.services;

import Proiect1.domain.Category;
import Proiect1.domain.Transaction;
import Proiect1.domain.User;
import Proiect1.dtos.TransactionDTO;
import Proiect1.exceptions.ItemNotFound;
import Proiect1.repositories.CategoryRepository;
import Proiect1.repositories.TransactionRepository;
import Proiect1.repositories.UserRepository;
import org.springframework.stereotype.Service;

@Service
public class TransactionServiceImpl implements TransactionService{

    private final UserRepository userRepository;
    private final TransactionRepository transactionRepository;
    private final CategoryRepository categoryRepository;

    TransactionServiceImpl(UserRepository userRepository, TransactionRepository transactionRepository, CategoryRepository categoryRepository){
        this.userRepository = userRepository;
        this.transactionRepository = transactionRepository;
        this.categoryRepository = categoryRepository;
    }

    @Override
    public TransactionDTO addTransaction(TransactionDTO transactionDTO) {
        User user = userRepository.findById(transactionDTO.getUserId())
                .orElseThrow(() -> new ItemNotFound("User"));

        Category category = null;
        if (transactionDTO.getCategoryId() != null) {
            category = categoryRepository.findById(transactionDTO.getCategoryId())
                    .orElseThrow(() -> new ItemNotFound("Category"));
        }

        Transaction transaction = new Transaction();
        transaction.setAmount(transactionDTO.getAmount());
        transaction.setTransactionDate(transactionDTO.getTransactionDate());
        transaction.setDescription(transactionDTO.getDescription());
        transaction.setTransactionType(transactionDTO.getTransactionType());
        transaction.setUser(user);
        transaction.setCategory(category);

        // Adjust user’s single balance
        if ("EXPENSE".equalsIgnoreCase(transactionDTO.getTransactionType())) {
            user.setBalance(user.getBalance().subtract(transactionDTO.getAmount()));
        } else if ("INCOME".equalsIgnoreCase(transactionDTO.getTransactionType())) {
            user.setBalance(user.getBalance().add(transactionDTO.getAmount()));
        }

        transactionRepository.save(transaction);
        userRepository.save(user); // update user’s balance

        transactionDTO.setId(transaction.getId());
        return transactionDTO;
    }
}
