package Proiect1.services;

import Proiect1.domain.Budget;
import Proiect1.domain.User;
import Proiect1.dtos.BudgetDTO;
import Proiect1.exceptions.ItemNotFound;
import Proiect1.repositories.BudgetRepository;
import Proiect1.repositories.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class BudgetServiceImpl implements BudgetService{

    private final UserRepository userRepository;
    private final BudgetRepository budgetRepository;

    BudgetServiceImpl(UserRepository userRepository, BudgetRepository budgetRepository){
        this.userRepository = userRepository;
        this.budgetRepository = budgetRepository;
    }

    @Override
    public BudgetDTO createBudget(Long userId, BudgetDTO budgetDTO) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ItemNotFound("User"));

        Budget budget = new Budget();
        budget.setAmount(budgetDTO.getAmount());
        budget.setStartDate(budgetDTO.getStartDate());
        budget.setEndDate(budgetDTO.getEndDate());
        budget.setUser(user);

        budgetRepository.save(budget);
        return convertToDTO(budget);    }

    @Override
    public List<BudgetDTO> getUserBudgets(Long userId) {
        return budgetRepository.findByUserId(userId).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    private BudgetDTO convertToDTO(Budget budget) {
        BudgetDTO dto = new BudgetDTO();
        dto.setId(budget.getId());
        dto.setAmount(budget.getAmount());
        dto.setStartDate(budget.getStartDate());
        dto.setEndDate(budget.getEndDate());
        dto.setUserId(budget.getUser().getId());
        return dto;
    }
}
