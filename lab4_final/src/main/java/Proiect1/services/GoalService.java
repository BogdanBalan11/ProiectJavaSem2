package Proiect1.services;

import Proiect1.dtos.GoalDTO;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface GoalService {
    GoalDTO createGoal(Long userId, GoalDTO goalDTO);
    List<GoalDTO> getUserGoals(Long userId);

}
