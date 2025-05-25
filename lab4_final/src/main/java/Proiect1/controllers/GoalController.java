package Proiect1.controllers;

import Proiect1.dtos.GoalDTO;
import Proiect1.exceptions.ItemNotFound;
import Proiect1.services.GoalService;
import Proiect1.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/goals")
public class GoalController {

    private final GoalService goalService;
    private final UserService userService;

    @Autowired
    public GoalController(GoalService goalService, UserService userService) {
        this.goalService = goalService;
        this.userService = userService;
    }

    private Long getCurrentUserId() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return userService.getUserByEmail(email).getId();
    }

    @GetMapping("")
    public String listGoals(Model model) {
        Long userId = getCurrentUserId();
        model.addAttribute("goals", goalService.getUserGoals(userId));
        return "goalsList";
    }

    @GetMapping("/add")
    public String showAddForm(Model model) {
        model.addAttribute("goal", new GoalDTO());
        return "goalForm";
    }

    @PostMapping("/add")
    public String addGoal(@ModelAttribute("goal") GoalDTO goalDTO) {
        goalService.createGoal(getCurrentUserId(), goalDTO);
        return "redirect:/goals";
    }

    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model) {
        GoalDTO goal = goalService.getUserGoals(getCurrentUserId())
                .stream()
                .filter(g -> g.getId().equals(id))
                .findFirst()
                .orElseThrow(() -> new ItemNotFound("Goal"));
        model.addAttribute("goal", goal);
        return "goalForm";
    }

    @PostMapping("/edit/{id}")
    public String updateGoal(@PathVariable Long id, @ModelAttribute("goal") GoalDTO goalDTO) {
        goalService.createGoal(getCurrentUserId(), goalDTO);
        return "redirect:/goals";
    }

    @GetMapping("/delete/{id}")
    public String deleteGoal(@PathVariable Long id) {
        goalService.deleteGoal(id);
        return "redirect:/goals";
    }
}
