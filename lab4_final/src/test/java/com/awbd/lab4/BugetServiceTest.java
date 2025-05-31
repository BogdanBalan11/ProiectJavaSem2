package com.awbd.lab4;

import Proiect1.domain.Budget;
import Proiect1.domain.User;
import Proiect1.dtos.BudgetDTO;
import Proiect1.exceptions.ItemNotFound;
import Proiect1.repositories.BudgetRepository;
import Proiect1.repositories.UserRepository;
import Proiect1.services.BudgetServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class BugetServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private BudgetRepository budgetRepository;

    @InjectMocks
    private BudgetServiceImpl budgetService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testCreateBudget_Success() {
        Long userId = 1L;
        BudgetDTO dto = new BudgetDTO();
        dto.setAmount(BigDecimal.valueOf(1000));
        dto.setStartDate(LocalDate.now());
        dto.setEndDate(LocalDate.now().plusMonths(1));
        dto.setUserIds(Set.of());

        User user = new User();
        user.setId(userId);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        Budget savedBudget = new Budget();
        savedBudget.setId(1L);
        savedBudget.setAmount(dto.getAmount());
        savedBudget.setStartDate(dto.getStartDate());
        savedBudget.setEndDate(dto.getEndDate());
        savedBudget.setUsers(Set.of(user));

        when(budgetRepository.save(any(Budget.class))).thenReturn(savedBudget);

        BudgetDTO result = budgetService.createBudget(userId, dto);

        assertNotNull(result);
        assertEquals(dto.getAmount(), result.getAmount());
        verify(userRepository).findById(userId);
        verify(budgetRepository).save(any(Budget.class));
    }

    @Test
    void testCreateBudget_UserNotFound() {
        Long userId = 1L;
        BudgetDTO dto = new BudgetDTO();

        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(ItemNotFound.class, () -> budgetService.createBudget(userId, dto));
        verify(userRepository).findById(userId);
        verify(budgetRepository, never()).save(any());
    }

    @Test
    void testGetUserBudgets_Success() {
        Long userId = 1L;
        User user = new User();
        user.setId(userId);

        Budget b1 = new Budget();
        b1.setId(1L);
        b1.setAmount(BigDecimal.valueOf(500));
        b1.setStartDate(LocalDate.now());
        b1.setEndDate(LocalDate.now().plusDays(30));
        b1.setUsers(Set.of(user));

        Budget b2 = new Budget();
        b2.setId(2L);
        b2.setAmount(BigDecimal.valueOf(800));
        b2.setStartDate(LocalDate.now());
        b2.setEndDate(LocalDate.now().plusDays(60));
        b2.setUsers(Set.of(user));

        when(budgetRepository.findByUsersId(userId)).thenReturn(List.of(b1, b2));

        List<BudgetDTO> result = budgetService.getUserBudgets(userId);

        assertEquals(2, result.size());
        verify(budgetRepository).findByUsersId(userId);
    }

    @Test
    void testUpdateBudget_Success() {
        Long id = 1L;

        Budget existing = new Budget();
        existing.setId(id);
        existing.setAmount(BigDecimal.valueOf(400));
        existing.setStartDate(LocalDate.now().minusDays(5));
        existing.setEndDate(LocalDate.now().plusDays(25));

        BudgetDTO dto = new BudgetDTO();
        dto.setAmount(BigDecimal.valueOf(1000));
        dto.setStartDate(LocalDate.now());
        dto.setEndDate(LocalDate.now().plusMonths(1));

        when(budgetRepository.findById(id)).thenReturn(Optional.of(existing));

        assertDoesNotThrow(() -> budgetService.updateBudget(id, dto));

        assertEquals(dto.getAmount(), existing.getAmount());
        verify(budgetRepository).save(existing);
    }

    @Test
    void testUpdateBudget_NotFound() {
        when(budgetRepository.findById(99L)).thenReturn(Optional.empty());
        assertThrows(ItemNotFound.class, () -> budgetService.updateBudget(99L, new BudgetDTO()));
    }

    @Test
    void testDeleteBudget_Success() {
        Long id = 1L;
        Budget budget = new Budget();
        budget.setId(id);

        when(budgetRepository.findById(id)).thenReturn(Optional.of(budget));
        assertDoesNotThrow(() -> budgetService.deleteBudget(id));
        verify(budgetRepository).delete(budget);
    }

    @Test
    void testDeleteBudget_NotFound() {
        when(budgetRepository.findById(42L)).thenReturn(Optional.empty());
        assertThrows(ItemNotFound.class, () -> budgetService.deleteBudget(42L));
    }
}
