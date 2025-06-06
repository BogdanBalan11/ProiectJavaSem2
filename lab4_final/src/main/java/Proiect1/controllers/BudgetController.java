package Proiect1.controllers;

import Proiect1.dtos.BudgetDTO;
import Proiect1.exceptions.ItemNotFound;
import Proiect1.repositories.UserRepository;
import Proiect1.services.BudgetService;
import Proiect1.services.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/budgets")
public class BudgetController {

    private static final Logger logger = LoggerFactory.getLogger(BudgetController.class);

    private final BudgetService budgetService;
    private final UserService userService;
    private final UserRepository userRepository;

    @Autowired
    public BudgetController(BudgetService budgetService, UserService userService, UserRepository userRepository) {
        this.budgetService = budgetService;
        this.userService = userService;
        this.userRepository = userRepository;
    }

    private Long getCurrentUserId() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        logger.debug("Authenticated user email: {}", email);
        return userService.getUserByEmail(email).getId();
    }

    @GetMapping("")
    public String listBudgets(Model model) {
        Long userId = getCurrentUserId();
        logger.info("Fetching budgets for userId={}", userId);
        model.addAttribute("budgets", budgetService.getUserBudgets(userId));
        return "budgetsList";
    }

    @GetMapping("/add")
    public String showAddForm(Model model) {
        logger.debug("Opening add budget form");
        model.addAttribute("budget", new BudgetDTO());
        model.addAttribute("users", userRepository.findAll());
        return "budgetForm";
    }

    @PostMapping("/add")
    public String addBudget(@ModelAttribute("budget") BudgetDTO budgetDTO) {
        logger.info("Saving new budget for userId(s): {}", budgetDTO.getUserIds());
        budgetService.createBudget(getCurrentUserId(), budgetDTO);
        return "redirect:/budgets";
    }

    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model) {
        logger.debug("Opening edit form for budget id={}", id);
        BudgetDTO budget = budgetService.getUserBudgets(getCurrentUserId()).stream()
                .filter(b -> b.getId().equals(id))
                .findFirst()
                .orElseThrow(() -> {
                    logger.warn("Budget with id={} not found for current user", id);
                    return new ItemNotFound("Budget");
                });
        model.addAttribute("budget", budget);
        model.addAttribute("users", userRepository.findAll());
        return "budgetForm";
    }

    @PostMapping("/edit/{id}")
    public String updateBudget(@PathVariable Long id, @ModelAttribute("budget") BudgetDTO budgetDTO) {
        logger.info("Updating budget id={} with new data", id);
        budgetService.updateBudget(id, budgetDTO);
        return "redirect:/budgets";
    }

    @GetMapping("/delete/{id}")
    public String deleteBudget(@PathVariable Long id) {
        logger.info("Deleting budget with id={}", id);
        budgetService.deleteBudget(id);
        return "redirect:/budgets";
    }
}
