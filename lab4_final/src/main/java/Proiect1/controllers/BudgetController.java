package Proiect1.controllers;

import Proiect1.dtos.BudgetDTO;
import Proiect1.exceptions.ItemNotFound;
import Proiect1.services.BudgetService;
import Proiect1.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/budgets")
public class BudgetController {

    private final BudgetService budgetService;
    private final UserService userService;

    @Autowired
    public BudgetController(BudgetService budgetService, UserService userService) {
        this.budgetService = budgetService;
        this.userService = userService;
    }

    private Long getCurrentUserId() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return userService.getUserByEmail(email).getId();
    }

    @GetMapping("")
    public String listBudgets(Model model) {
        Long userId = getCurrentUserId();
        model.addAttribute("budgets", budgetService.getUserBudgets(userId));
        return "budgetsList";
    }

    @GetMapping("/add")
    public String showAddForm(Model model) {
        model.addAttribute("budget", new BudgetDTO());
        return "budgetForm";
    }

    @PostMapping("/add")
    public String addBudget(@ModelAttribute("budget") BudgetDTO budgetDTO) {
        budgetService.createBudget(getCurrentUserId(), budgetDTO);
        return "redirect:/budgets";
    }

    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model) {
        BudgetDTO budget = budgetService.getUserBudgets(getCurrentUserId()).stream()
                .filter(b -> b.getId().equals(id))
                .findFirst()
                .orElseThrow(() -> new ItemNotFound("Budget"));
        model.addAttribute("budget", budget);
        return "budgetForm";
    }

    @PostMapping("/edit/{id}")
    public String updateBudget(@PathVariable Long id, @ModelAttribute("budget") BudgetDTO budgetDTO) {
        budgetService.updateBudget(id, budgetDTO);
        return "redirect:/budgets";
    }

    @GetMapping("/delete/{id}")
    public String deleteBudget(@PathVariable Long id) {
        budgetService.deleteBudget(id);
        return "redirect:/budgets";
    }
}
