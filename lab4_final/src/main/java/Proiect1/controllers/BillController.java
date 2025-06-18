package Proiect1.controllers;

import Proiect1.dtos.BillDTO;
import Proiect1.exceptions.ItemNotFound;
import Proiect1.services.BillService;
import Proiect1.services.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;


@Controller
@RequestMapping("/bills")
public class BillController {

    private static final Logger logger = LoggerFactory.getLogger(BillController.class);

    BillService billService;
    private final UserService userService;

    public BillController(BillService billService, UserService userService) {
        this.billService = billService;
        this.userService = userService;
    }

    private Long getCurrentUserId() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        logger.debug("Authenticated user email: {}", email);
        return userService.getUserByEmail(email).getId();
    }

    @GetMapping("")
    public String listBills(Model model,
                            @RequestParam(defaultValue = "0") int page,
                            @RequestParam(defaultValue = "5") int size) {
        Long userId = getCurrentUserId();
        logger.info("Fetching bills for userId={}", userId);
        Pageable pageable = PageRequest.of(page, size, Sort.by("nextDueDate").ascending());
        Page<BillDTO> billPage = billService.getUserBillsPaginated(userId, pageable);

        model.addAttribute("billPage", billPage);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", billPage.getTotalPages());

        return "billList";
    }

    @GetMapping("/add")
    public String showAddForm(Model model) {
        logger.debug("Showing bill add form");
        model.addAttribute("bill", new BillDTO());
        return "billForm";
    }

    @PostMapping("/add")
    public String saveBill(@ModelAttribute("bill") BillDTO billDTO) {
        Long userId = getCurrentUserId();
        logger.info("Creating new bill for userId={} with name={}", userId, billDTO.getBillName());
        billService.createBill(userId, billDTO);
        return "redirect:/bills";
    }

    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model) {
        logger.debug("Showing edit form for billId={}", id);
        BillDTO bill = billService.getUserBills(getCurrentUserId())
                .stream()
                .filter(b -> b.getId().equals(id))
                .findFirst()
                .orElseThrow(()-> {
                    logger.warn("Bill with id={} not found", id);
                    return new ItemNotFound("Bill");
                });
        model.addAttribute("bill", bill);
        return "billForm";
    }

    @PostMapping("/edit/{id}")
    public String updateBill(@PathVariable Long id, @ModelAttribute("bill") BillDTO billDTO) {
        logger.info("Updating bill id={} with new name={}", id, billDTO.getBillName());
        billService.updateBill(id, billDTO);
        return "redirect:/bills";
    }

    @GetMapping("/delete/{id}")
    public String deleteBill(@PathVariable Long id) {
        logger.info("Deleting bill with id={}", id);
        billService.deleteBill(id);
        return "redirect:/bills";
    }
}
