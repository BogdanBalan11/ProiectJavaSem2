package Proiect1.services;

import Proiect1.domain.Budget;
import Proiect1.domain.User;
import Proiect1.dtos.BudgetDTO;
import Proiect1.exceptions.ItemNotFound;
import Proiect1.repositories.BudgetRepository;
import Proiect1.repositories.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.Set;
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
        Set<User> users = budgetDTO.getUserIds().stream()
                .map(id -> userRepository.findById(id).orElseThrow())
                .collect(Collectors.toSet());

        Budget budget = new Budget();
        budget.setAmount(budgetDTO.getAmount());
        budget.setStartDate(budgetDTO.getStartDate());
        budget.setEndDate(budgetDTO.getEndDate());
        budget.setUsers(users);
        budgetRepository.save(budget);
        return convertToDTO(budget);    }

    @Override
    public List<BudgetDTO> getUserBudgets(Long userId) {
        return budgetRepository.findByUsersId(userId).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    private BudgetDTO convertToDTO(Budget budget) {
        BudgetDTO dto = new BudgetDTO();
        dto.setId(budget.getId());
        dto.setAmount(budget.getAmount());
        dto.setStartDate(budget.getStartDate());
        dto.setEndDate(budget.getEndDate());
        dto.setUserIds(budget.getUsers().stream()
                .map(User::getId)
                .collect(Collectors.toSet()));
        return dto;
    }

    @Override
    public void updateBudget(Long id, BudgetDTO dto) {
        Set<User> users = dto.getUserIds().stream()
                .map(userId -> userRepository.findById(userId).orElseThrow())
                .collect(Collectors.toSet());

        Budget budget = budgetRepository.findById(id)
                .orElseThrow(() -> new ItemNotFound("Budget"));
        budget.setAmount(dto.getAmount());
        budget.setStartDate(dto.getStartDate());
        budget.setEndDate(dto.getEndDate());
        budget.setUsers(users);
        budgetRepository.save(budget);
    }

    @Override
    public void deleteBudget(Long id) {
        Budget budget = budgetRepository.findById(id)
                .orElseThrow(() -> new ItemNotFound("Budget"));
        budgetRepository.delete(budget);
    }

}
