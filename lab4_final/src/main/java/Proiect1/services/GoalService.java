package Proiect1.services;

import Proiect1.dtos.GoalDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface GoalService {
    GoalDTO createGoal(Long userId, GoalDTO goalDTO);
    List<GoalDTO> getUserGoals(Long userId);
    void deleteGoal(Long goalId);
    Page<GoalDTO> getUserGoalsPaginated(Long userId, Pageable pageable);

}
