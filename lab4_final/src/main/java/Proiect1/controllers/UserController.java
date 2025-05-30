package Proiect1.controllers;

import Proiect1.dtos.UserDTO;
import Proiect1.dtos.UserRegistrationDTO;
import Proiect1.exceptions.UserWithSameEmailExists;
import Proiect1.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import java.security.Principal;

@Controller
public class UserController {
    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/login")
    public String showLoginPage() {
        return "login";
    }

    @GetMapping("/participant/form")
    public String showRegistrationForm(Model model) {
        model.addAttribute("user", new UserRegistrationDTO());
        return "register";
    }

    @PostMapping("/participant/form")
    public String register(@ModelAttribute("user") UserRegistrationDTO userDTO, Model model) {
        try {
            userService.registerUser(userDTO);
            return "redirect:/login";
        } catch (UserWithSameEmailExists e) {
            model.addAttribute("error", e.getMessage());
            return "register";
        }
    }

    @GetMapping("/")
    public String userDetails(Model model, Principal principal) {
        String email = principal.getName(); // from logged-in session
        UserDTO userDTO = userService.getUserByEmail(email);
        model.addAttribute("user", userDTO);
        return "user-details";
    }
}
