package com.awbd.lab4;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import Proiect1.domain.Category;
import Proiect1.domain.Transaction;
import Proiect1.domain.User;
import Proiect1.dtos.TransactionDTO;
import Proiect1.exceptions.ItemNotFound;
import Proiect1.repositories.CategoryRepository;
import Proiect1.repositories.TransactionRepository;
import Proiect1.repositories.UserRepository;
import Proiect1.services.TransactionServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public class TransactionServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private CategoryRepository categoryRepository;

    @InjectMocks
    private TransactionServiceImpl transactionService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testAddTransaction_Income_Success() {
        Long userId = 1L;
        BigDecimal amount = BigDecimal.valueOf(500);
        User user = new User();
        user.setId(userId);
        user.setBalance(BigDecimal.valueOf(1000));

        TransactionDTO dto = new TransactionDTO();
        dto.setUserId(userId);
        dto.setAmount(amount);
        dto.setTransactionDate(LocalDate.now());
        dto.setTransactionType("INCOME");
        dto.setDescription("Salary");

        Transaction transaction = new Transaction();
        transaction.setId(10L);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(transactionRepository.save(any(Transaction.class))).thenAnswer(inv -> {
            Transaction t = inv.getArgument(0);
            t.setId(10L);
            return t;
        });

        TransactionDTO result = transactionService.addTransaction(dto);

        assertNotNull(result.getId());
        assertEquals(BigDecimal.valueOf(1500), user.getBalance());
        verify(transactionRepository).save(any(Transaction.class));
        verify(userRepository).save(user);
    }

    @Test
    void testAddTransaction_Expense_WithCategory() {
        Long userId = 1L;
        Long categoryId = 99L;
        BigDecimal amount = BigDecimal.valueOf(200);

        User user = new User();
        user.setId(userId);
        user.setBalance(BigDecimal.valueOf(1000));

        Category category = new Category();
        category.setId(categoryId);
        category.setName("Food");

        TransactionDTO dto = new TransactionDTO();
        dto.setUserId(userId);
        dto.setCategoryId(categoryId);
        dto.setAmount(amount);
        dto.setTransactionType("EXPENSE");
        dto.setTransactionDate(LocalDate.now());

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(categoryRepository.findById(categoryId)).thenReturn(Optional.of(category));
        when(transactionRepository.save(any(Transaction.class))).thenAnswer(inv -> {
            Transaction t = inv.getArgument(0);
            t.setId(5L);
            return t;
        });

        TransactionDTO result = transactionService.addTransaction(dto);

        assertEquals(5L, result.getId());
        assertEquals(BigDecimal.valueOf(800), user.getBalance());
        verify(categoryRepository).findById(categoryId);
        verify(transactionRepository).save(any());
        verify(userRepository).save(user);
    }

    @Test
    void testGetUserTransactions_ReturnsList() {
        Long userId = 1L;
        User user = new User();
        user.setId(userId);

        Transaction t = new Transaction();
        t.setId(1L);
        t.setAmount(BigDecimal.valueOf(100));
        t.setTransactionDate(LocalDate.now());
        t.setTransactionType("INCOME");
        t.setUser(user);

        when(transactionRepository.findByUserId(userId)).thenReturn(List.of(t));

        List<TransactionDTO> result = transactionService.getUserTransactions(userId);

        assertEquals(1, result.size());
        assertEquals("INCOME", result.get(0).getTransactionType());
    }

    @Test
    void testGetTransactionById_Success() {
        Long id = 42L;
        User user = new User();
        user.setId(1L);

        Transaction t = new Transaction();
        t.setId(id);
        t.setAmount(BigDecimal.valueOf(250));
        t.setTransactionType("EXPENSE");
        t.setTransactionDate(LocalDate.now());
        t.setUser(user);

        when(transactionRepository.findById(id)).thenReturn(Optional.of(t));

        TransactionDTO result = transactionService.getTransactionById(id);

        assertEquals(250, result.getAmount().intValue());
        assertEquals("EXPENSE", result.getTransactionType());
    }

    @Test
    void testUpdateTransaction_AdjustsBalance() {
        Long id = 1L;

        User user = new User();
        user.setId(1L);
        user.setBalance(BigDecimal.valueOf(1000));

        Transaction oldTx = new Transaction();
        oldTx.setId(id);
        oldTx.setTransactionType("EXPENSE");
        oldTx.setAmount(BigDecimal.valueOf(100));
        oldTx.setUser(user);

        TransactionDTO dto = new TransactionDTO();
        dto.setAmount(BigDecimal.valueOf(200));
        dto.setTransactionType("INCOME");
        dto.setTransactionDate(LocalDate.now());

        when(transactionRepository.findById(id)).thenReturn(Optional.of(oldTx));

        transactionService.updateTransaction(id, dto);

        assertEquals(BigDecimal.valueOf(1300), user.getBalance()); // 1000 + 100 (undo EXPENSE) + 200 (apply INCOME)
        verify(transactionRepository).save(oldTx);
        verify(userRepository).save(user);
    }

    @Test
    void testDeleteTransaction_AdjustsBalance() {
        Long id = 1L;
        User user = new User();
        user.setId(1L);
        user.setBalance(BigDecimal.valueOf(1000));

        Transaction tx = new Transaction();
        tx.setId(id);
        tx.setAmount(BigDecimal.valueOf(200));
        tx.setTransactionType("EXPENSE");
        tx.setUser(user);

        when(transactionRepository.findById(id)).thenReturn(Optional.of(tx));

        transactionService.deleteTransaction(id);

        assertEquals(BigDecimal.valueOf(1200), user.getBalance()); // 1000 + 200 (undo EXPENSE)
        verify(transactionRepository).delete(tx);
        verify(userRepository).save(user);
    }

    @Test
    void testAddTransaction_UserNotFound() {
        TransactionDTO dto = new TransactionDTO();
        dto.setUserId(100L);

        when(userRepository.findById(100L)).thenReturn(Optional.empty());

        assertThrows(ItemNotFound.class, () -> transactionService.addTransaction(dto));
    }

    @Test
    void testAddTransaction_CategoryNotFound() {
        TransactionDTO dto = new TransactionDTO();
        dto.setUserId(1L);
        dto.setCategoryId(99L);

        when(userRepository.findById(1L)).thenReturn(Optional.of(new User()));
        when(categoryRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ItemNotFound.class, () -> transactionService.addTransaction(dto));
    }
}
