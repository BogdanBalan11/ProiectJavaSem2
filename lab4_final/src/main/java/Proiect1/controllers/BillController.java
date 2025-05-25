package Proiect1.controllers;

import Proiect1.dtos.BillDTO;
import Proiect1.exceptions.ItemNotFound;
import Proiect1.services.BillService;
import Proiect1.services.UserService;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/bills")
public class BillController {
    BillService billService;
    private final UserService userService;

    public BillController(BillService billService, UserService userService) {
        this.billService = billService;
        this.userService = userService;
    }

    private Long getCurrentUserId() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return userService.getUserByEmail(email).getId();
    }

//    @RequestMapping("")
//    public String billsList(Model model) {
//        List<BillDTO> bills = billService.getUserBills(1L);
//        model.addAttribute("bills", bills);
//        return "billsList";
//    }

    @GetMapping("")
    public String listBills(Model model) {
        Long userId = getCurrentUserId();
        List<BillDTO> bills = billService.getUserBills(userId);
        model.addAttribute("bills", bills);
        return "billList";
    }

    @GetMapping("/add")
    public String showAddForm(Model model) {
        model.addAttribute("bill", new BillDTO());
        return "billForm";
    }

    @PostMapping("/add")
    public String saveBill(@ModelAttribute("bill") BillDTO billDTO) {
        Long userId = getCurrentUserId();
        billService.createBill(userId, billDTO);
        return "redirect:/bills";
    }

    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model) {
        BillDTO bill = billService.getUserBills(getCurrentUserId())
                .stream()
                .filter(b -> b.getId().equals(id))
                .findFirst()
                .orElseThrow(() -> new ItemNotFound("Bill"));
        model.addAttribute("bill", bill);
        return "billForm";
    }

    @PostMapping("/edit/{id}")
    public String updateBill(@PathVariable Long id, @ModelAttribute("bill") BillDTO billDTO) {
        billService.updateBill(id, billDTO);
        return "redirect:/bills";
    }

    @GetMapping("/delete/{id}")
    public String deleteBill(@PathVariable Long id) {
        billService.deleteBill(id);
        return "redirect:/bills";
    }
}
