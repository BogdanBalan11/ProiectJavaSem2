package Proiect1.services;

import Proiect1.domain.Category;
import Proiect1.domain.Receipt;
import Proiect1.domain.Transaction;
import Proiect1.domain.User;
import Proiect1.dtos.ReceiptDTO;
import Proiect1.dtos.TransactionDTO;
import Proiect1.exceptions.ItemNotFound;
import Proiect1.repositories.CategoryRepository;
import Proiect1.repositories.TransactionRepository;
import Proiect1.repositories.UserRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

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

        if (transactionDTO.getReceipt() != null && transactionDTO.getReceipt().getDetails() != null) {
            Receipt receipt = new Receipt();
            receipt.setDetails(transactionDTO.getReceipt().getDetails());
            receipt.setTransaction(transaction); // associate the receipt with the transaction
            transaction.setReceipt(receipt);     // associate the transaction with the receipt
        }

        transactionRepository.save(transaction);
        userRepository.save(user); // update user’s balance

        transactionDTO.setId(transaction.getId());
        return transactionDTO;
    }

    private TransactionDTO convertToDTO(Transaction tx) {
        TransactionDTO dto = new TransactionDTO();
        dto.setId(tx.getId());
        dto.setAmount(tx.getAmount());
        dto.setTransactionDate(tx.getTransactionDate());
        dto.setDescription(tx.getDescription());
        dto.setTransactionType(tx.getTransactionType());
        dto.setUserId(tx.getUser().getId());
        if (tx.getCategory() != null) {
            dto.setCategoryId(tx.getCategory().getId());
            dto.setCategoryName(tx.getCategory().getName());
        }
        if (tx.getReceipt() != null) {
            ReceiptDTO receiptDTO = new ReceiptDTO();
            receiptDTO.setDetails(tx.getReceipt().getDetails());
            dto.setReceipt(receiptDTO);
        }
        return dto;
    }

    @Override
    public List<TransactionDTO> getUserTransactions(Long userId) {
        return transactionRepository.findByUserId(userId).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public TransactionDTO getTransactionById(Long id) {
        Transaction tx = transactionRepository.findById(id)
                .orElseThrow(() -> new ItemNotFound("Transaction"));
        return convertToDTO(tx);
    }

    @Override
    public void updateTransaction(Long id, TransactionDTO dto) {
        Transaction tx = transactionRepository.findById(id)
                .orElseThrow(() -> new ItemNotFound("Transaction"));

        BigDecimal oldAmount = tx.getAmount();
        String oldType = tx.getTransactionType();
        User user = tx.getUser();

        // reverse old amount
        if ("EXPENSE".equalsIgnoreCase(oldType)) {
            user.setBalance(user.getBalance().add(oldAmount));
        } else if ("INCOME".equalsIgnoreCase(oldType)) {
            user.setBalance(user.getBalance().subtract(oldAmount));
        }

        // update transaction
        tx.setAmount(dto.getAmount());
        tx.setTransactionDate(dto.getTransactionDate());
        tx.setTransactionType(dto.getTransactionType());
        tx.setDescription(dto.getDescription());

        if (dto.getCategoryId() != null) {
            Category cat = categoryRepository.findById(dto.getCategoryId())
                    .orElseThrow(() -> new ItemNotFound("Category"));
            tx.setCategory(cat);
        }

        if (dto.getReceipt() != null && dto.getReceipt().getDetails() != null) {
            if (tx.getReceipt() != null) {
                tx.getReceipt().setDetails(dto.getReceipt().getDetails());
            } else {
                Receipt receipt = new Receipt();
                receipt.setDetails(dto.getReceipt().getDetails());
                receipt.setTransaction(tx);
                tx.setReceipt(receipt);
            }
        }

        // apply new balance effect
        if ("EXPENSE".equalsIgnoreCase(dto.getTransactionType())) {
            user.setBalance(user.getBalance().subtract(dto.getAmount()));
        } else if ("INCOME".equalsIgnoreCase(dto.getTransactionType())) {
            user.setBalance(user.getBalance().add(dto.getAmount()));
        }

        transactionRepository.save(tx);
        userRepository.save(user);
    }

    @Override
    public void deleteTransaction(Long id) {
        Transaction tx = transactionRepository.findById(id)
                .orElseThrow(() -> new ItemNotFound("Transaction"));

        User user = tx.getUser();
        if ("EXPENSE".equalsIgnoreCase(tx.getTransactionType())) {
            user.setBalance(user.getBalance().add(tx.getAmount()));
        } else {
            user.setBalance(user.getBalance().subtract(tx.getAmount()));
        }

        transactionRepository.delete(tx);
        userRepository.save(user);
    }

}
