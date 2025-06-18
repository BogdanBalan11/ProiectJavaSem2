package Proiect1.services;

import Proiect1.domain.Budget;
import Proiect1.domain.User;
import Proiect1.dtos.BudgetDTO;
import Proiect1.exceptions.ItemNotFound;
import Proiect1.repositories.BudgetRepository;
import Proiect1.repositories.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class BudgetServiceImpl implements BudgetService{

    private static final Logger logger = LoggerFactory.getLogger(BudgetServiceImpl.class);

    private final UserRepository userRepository;
    private final BudgetRepository budgetRepository;

    BudgetServiceImpl(UserRepository userRepository, BudgetRepository budgetRepository){
        this.userRepository = userRepository;
        this.budgetRepository = budgetRepository;
    }

    @Override
    public BudgetDTO createBudget(Long userId, BudgetDTO budgetDTO) {
        logger.debug("Creating budget for userId={} with data={}", userId, budgetDTO);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> {
                    logger.warn("User with id={} not found", userId);
                    return new ItemNotFound("User");
                });
        Set<User> users = budgetDTO.getUserIds().stream()
                .map(id -> userRepository.findById(id).orElseThrow(() -> {
                    logger.warn("User id={} not found while creating budget", id);
                    return new ItemNotFound("User");
                }))
                .collect(Collectors.toSet());

        Budget budget = new Budget();
        budget.setAmount(budgetDTO.getAmount());
        budget.setStartDate(budgetDTO.getStartDate());
        budget.setEndDate(budgetDTO.getEndDate());
        budget.setUsers(users);
        budgetRepository.save(budget);
        logger.info("Created budget with id={}", budget.getId());
        return convertToDTO(budget);    }

    @Override
    public List<BudgetDTO> getUserBudgets(Long userId) {
        logger.debug("Fetching budgets for userId={}", userId);
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
        logger.debug("Updating budget id={} with new data={}", id, dto);
        Set<User> users = dto.getUserIds().stream()
                .map(userId -> userRepository.findById(userId)
                        .orElseThrow(() -> {
                            logger.warn("User id={} not found while updating budget", userId);
                            return new ItemNotFound("User");
                        }))
                .collect(Collectors.toSet());

        Budget budget = budgetRepository.findById(id)
                .orElseThrow(() -> {
                    logger.warn("Budget with id={} not found", id);
                    return new ItemNotFound("Budget");
                });
        budget.setAmount(dto.getAmount());
        budget.setStartDate(dto.getStartDate());
        budget.setEndDate(dto.getEndDate());
        budget.setUsers(users);
        budgetRepository.save(budget);
        logger.info("Updated budget id={}", id);
    }

    @Override
    public void deleteBudget(Long id) {
        logger.debug("Attempting to delete budget id={}", id);
        Budget budget = budgetRepository.findById(id)
                .orElseThrow(() -> {
                    logger.warn("Budget with id={} not found", id);
                    return new ItemNotFound("Budget");
                });
        budgetRepository.delete(budget);
        logger.info("Deleted budget with id={}", id);
    }

    @Override
    public Page<BudgetDTO> getUserBudgetsPaginated(Long userId, Pageable pageable) {
        Page<Budget> page = budgetRepository.findByUsersId(userId, pageable);
        return page.map(this::convertToDTO);
    }

}
