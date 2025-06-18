package Proiect1.controllers;

import Proiect1.dtos.TransactionDTO;
import Proiect1.repositories.CategoryRepository;
import Proiect1.services.TransactionService;
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
@RequestMapping("/transactions")
public class TransactionController {

    private static final Logger logger = LoggerFactory.getLogger(TransactionController.class);

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
    public String listTransactions(Model model,
                                   @RequestParam(defaultValue = "0") int page,
                                   @RequestParam(defaultValue = "5") int size) {
        Long userId = getCurrentUserId();
        logger.info("Listing transactions for userId={}", userId);
        Pageable pageable = PageRequest.of(page, size, Sort.by("transactionDate").descending());
        Page<TransactionDTO> transactionPage = transactionService.getUserTransactionsPaginated(userId, pageable);

        model.addAttribute("transactionPage", transactionPage);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", transactionPage.getTotalPages());

        return "transactionsList";
    }

    @GetMapping("/add")
    public String showAddForm(Model model) {
        logger.debug("Opening transaction add form");
        TransactionDTO dto = new TransactionDTO();
        dto.setUserId(getCurrentUserId());
        model.addAttribute("transaction", dto);
        model.addAttribute("categories", categoryRepository.findAll());
        return "transactionForm";
    }

    @PostMapping("/add")
    public String addTransaction(@ModelAttribute("transaction") TransactionDTO transactionDTO) {
        logger.info("Adding new transaction of type={} and amount={}", transactionDTO.getTransactionType(), transactionDTO.getAmount());
        transactionService.addTransaction(transactionDTO);
        return "redirect:/transactions";
    }

    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model) {
        logger.debug("Opening edit form for transactionId={}", id);
        TransactionDTO transaction = transactionService.getTransactionById(id);
        model.addAttribute("transaction", transaction);
        model.addAttribute("categories", categoryRepository.findAll());
        return "transactionForm";
    }

    @PostMapping("/edit/{id}")
    public String updateTransaction(@PathVariable Long id,
                                    @ModelAttribute("transaction") TransactionDTO transactionDTO) {
        logger.info("Updating transaction with id={}", id);
        transactionService.updateTransaction(id, transactionDTO);
        return "redirect:/transactions";
    }

    @GetMapping("/delete/{id}")
    public String deleteTransaction(@PathVariable Long id) {
        logger.warn("Deleting transaction with id={}", id);
        transactionService.deleteTransaction(id);
        return "redirect:/transactions";
    }
}
