package Proiect1.controllers;

import Proiect1.dtos.BillDTO;
import Proiect1.services.BillService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/bills")
public class BillController {
    BillService billService;

    public BillController(BillService billService) {
        this.billService = billService;
    }

    @RequestMapping("")
        public String billsList(Model model) {
        System.out.println(">>> Bills endpoint hit");
        List<BillDTO> bills = billService.getUserBills(1L);
        model.addAttribute("bills", bills);
        return "billsList";
        }
}
