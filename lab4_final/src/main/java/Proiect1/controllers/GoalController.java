package Proiect1.controllers;

import Proiect1.dtos.GoalDTO;
import Proiect1.exceptions.ItemNotFound;
import Proiect1.services.GoalService;
import Proiect1.services.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/goals")
public class GoalController {

    private static final Logger logger = LoggerFactory.getLogger(GoalController.class);

    private final GoalService goalService;
    private final UserService userService;

    @Autowired
    public GoalController(GoalService goalService, UserService userService) {
        this.goalService = goalService;
        this.userService = userService;
    }

    private Long getCurrentUserId() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        logger.debug("Retrieving userId for logged in user: {}", email);
        return userService.getUserByEmail(email).getId();
    }

    @GetMapping("")
    public String listGoals(Model model,
                            @RequestParam(defaultValue = "0") int page,
                            @RequestParam(defaultValue = "5") int size) {
        Long userId = getCurrentUserId();
        logger.info("Fetching goals for userId={}", userId);
        Pageable pageable = PageRequest.of(page, size, Sort.by("deadline").ascending());
        Page<GoalDTO> goalPage = goalService.getUserGoalsPaginated(userId, pageable);

        model.addAttribute("goalPage", goalPage);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", goalPage.getTotalPages());

        return "goalsList";
    }

    @GetMapping("/add")
    public String showAddForm(Model model) {
        logger.debug("Opening goal add form");
        model.addAttribute("goal", new GoalDTO());
        return "goalForm";
    }

    @PostMapping("/add")
    public String addGoal(@ModelAttribute("goal") GoalDTO goalDTO) {
        logger.info("Saving new goal: {}", goalDTO.getGoalName());
        goalService.createGoal(getCurrentUserId(), goalDTO);
        return "redirect:/goals";
    }

    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model) {
        logger.debug("Opening edit form for goalId={}", id);
        GoalDTO goal = goalService.getUserGoals(getCurrentUserId())
                .stream()
                .filter(g -> g.getId().equals(id))
                .findFirst()
                .orElseThrow(() -> {
                    logger.error("Goal with id={} not found for editing", id);
                    return new ItemNotFound("Goal");
                });
        model.addAttribute("goal", goal);
        return "goalForm";
    }

    @PostMapping("/edit/{id}")
    public String updateGoal(@PathVariable Long id, @ModelAttribute("goal") GoalDTO goalDTO) {
        logger.info("Updating goal with id={}", id);
        goalService.createGoal(getCurrentUserId(), goalDTO);
        return "redirect:/goals";
    }

    @GetMapping("/delete/{id}")
    public String deleteGoal(@PathVariable Long id) {
        logger.warn("Deleting goal with id={}", id);
        goalService.deleteGoal(id);
        return "redirect:/goals";
    }
}
