package Proiect1.controllers;

import Proiect1.dtos.TransactionDTO;
import Proiect1.repositories.CategoryRepository;
import Proiect1.services.TransactionService;
import Proiect1.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/transactions")
public class TransactionController {

    private final TransactionService transactionService;
    private final UserService userService;
    private final CategoryRepository categoryRepository;

    @Autowired
    public TransactionController(TransactionService transactionService,
                                 UserService userService,
                                 CategoryRepository categoryRepository) {
        this.transactionService = transactionService;
        this.userService = userService;
        this.categoryRepository = categoryRepository;
    }

    private Long getCurrentUserId() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return userService.getUserByEmail(email).getId();
    }

    @GetMapping("")
    public String listTransactions(Model model) {
        Long userId = getCurrentUserId();
        List<TransactionDTO> transactions = transactionService.getUserTransactions(userId);
        model.addAttribute("transactions", transactions);
        return "transactionsList";
    }

    @GetMapping("/add")
    public String showAddForm(Model model) {
        TransactionDTO dto = new TransactionDTO();
        dto.setUserId(getCurrentUserId());
        model.addAttribute("transaction", dto);
        model.addAttribute("categories", categoryRepository.findAll());
        return "transactionForm";
    }

    @PostMapping("/add")
    public String addTransaction(@ModelAttribute("transaction") TransactionDTO transactionDTO) {
        transactionService.addTransaction(transactionDTO);
        return "redirect:/transactions";
    }

    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model) {
        TransactionDTO transaction = transactionService.getTransactionById(id);
        model.addAttribute("transaction", transaction);
        model.addAttribute("categories", categoryRepository.findAll());
        return "transactionForm";
    }

    @PostMapping("/edit/{id}")
    public String updateTransaction(@PathVariable Long id,
                                    @ModelAttribute("transaction") TransactionDTO transactionDTO) {
        transactionService.updateTransaction(id, transactionDTO);
        return "redirect:/transactions";
    }

    @GetMapping("/delete/{id}")
    public String deleteTransaction(@PathVariable Long id) {
        transactionService.deleteTransaction(id);
        return "redirect:/transactions";
    }
}
