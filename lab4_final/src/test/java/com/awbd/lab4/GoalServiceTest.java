package com.awbd.lab4;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import Proiect1.domain.Goal;
import Proiect1.domain.User;
import Proiect1.dtos.GoalDTO;
import Proiect1.exceptions.ItemNotFound;
import Proiect1.repositories.GoalRepository;
import Proiect1.repositories.UserRepository;
import Proiect1.services.GoalServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public class GoalServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private GoalRepository goalRepository;

    @InjectMocks
    private GoalServiceImpl goalService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testCreateGoal_Success() {
        Long userId = 1L;
        User user = new User();
        user.setId(userId);

        GoalDTO goalDTO = new GoalDTO();
        goalDTO.setGoalName("Vacation");
        goalDTO.setTargetAmount(BigDecimal.valueOf(1000));
        goalDTO.setSavedAmount(BigDecimal.valueOf(200));
        goalDTO.setDeadline(LocalDate.now().plusMonths(6));

        Goal savedGoal = new Goal();
        savedGoal.setId(1L);
        savedGoal.setGoalName(goalDTO.getGoalName());
        savedGoal.setTargetAmount(goalDTO.getTargetAmount());
        savedGoal.setSavedAmount(goalDTO.getSavedAmount());
        savedGoal.setDeadline(goalDTO.getDeadline());
        savedGoal.setUser(user);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(goalRepository.save(any(Goal.class))).thenReturn(savedGoal);

        GoalDTO result = goalService.createGoal(userId, goalDTO);

        assertNotNull(result);
        assertEquals(goalDTO.getGoalName(), result.getGoalName());
        assertEquals(goalDTO.getTargetAmount(), result.getTargetAmount());
        assertEquals(userId, result.getUserId());

        verify(userRepository).findById(userId);
        verify(goalRepository).save(any(Goal.class));
    }

    @Test
    void testCreateGoal_UserNotFound() {
        Long userId = 1L;
        GoalDTO dto = new GoalDTO();

        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(ItemNotFound.class, () -> goalService.createGoal(userId, dto));
        verify(userRepository).findById(userId);
        verify(goalRepository, never()).save(any());
    }

    @Test
    void testGetUserGoals_Success() {
        Long userId = 1L;
        User user = new User();
        user.setId(userId);

        Goal g1 = new Goal();
        g1.setId(1L);
        g1.setGoalName("Emergency Fund");
        g1.setTargetAmount(BigDecimal.valueOf(2000));
        g1.setSavedAmount(BigDecimal.valueOf(500));
        g1.setDeadline(LocalDate.now().plusMonths(3));
        g1.setUser(user);

        Goal g2 = new Goal();
        g2.setId(2L);
        g2.setGoalName("New Phone");
        g2.setTargetAmount(BigDecimal.valueOf(800));
        g2.setSavedAmount(BigDecimal.valueOf(100));
        g2.setDeadline(LocalDate.now().plusMonths(1));
        g2.setUser(user);

        when(goalRepository.findByUserId(userId)).thenReturn(List.of(g1, g2));

        List<GoalDTO> result = goalService.getUserGoals(userId);

        assertEquals(2, result.size());
        assertEquals("Emergency Fund", result.get(0).getGoalName());
        verify(goalRepository).findByUserId(userId);
    }

    @Test
    void testDeleteGoal_Success() {
        Long goalId = 1L;
        Goal goal = new Goal();
        goal.setId(goalId);

        when(goalRepository.findById(goalId)).thenReturn(Optional.of(goal));

        assertDoesNotThrow(() -> goalService.deleteGoal(goalId));
        verify(goalRepository).delete(goal);
    }

    @Test
    void testDeleteGoal_NotFound() {
        when(goalRepository.findById(99L)).thenReturn(Optional.empty());
        assertThrows(ItemNotFound.class, () -> goalService.deleteGoal(99L));
        verify(goalRepository, never()).delete(any());
    }
}
