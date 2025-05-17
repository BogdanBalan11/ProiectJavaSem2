package Proiect1.services;

import Proiect1.dtos.BudgetDTO;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface BudgetService {
    BudgetDTO createBudget(Long userId, BudgetDTO budgetDTO);
    List<BudgetDTO> getUserBudgets(Long userId);
}
