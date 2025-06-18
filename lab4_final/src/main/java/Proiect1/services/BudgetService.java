package Proiect1.services;

import Proiect1.dtos.BudgetDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface BudgetService {
    BudgetDTO createBudget(Long userId, BudgetDTO budgetDTO);
    List<BudgetDTO> getUserBudgets(Long userId);
    void updateBudget(Long id, BudgetDTO dto);
    void deleteBudget(Long id);
    Page<BudgetDTO> getUserBudgetsPaginated(Long userId, Pageable pageable);
}
