package Proiect1.controllers;

import Proiect1.dtos.UserDTO;
import Proiect1.dtos.UserRegistrationDTO;
import Proiect1.exceptions.UserWithSameEmailExists;
import Proiect1.services.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import java.security.Principal;

@Controller
public class UserController {

    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/login")
    public String showLoginPage() {
        logger.debug("Accessed login page");
        return "login";
    }

    @GetMapping("/participant/form")
    public String showRegistrationForm(Model model) {
        logger.debug("Accessed registration form");
        model.addAttribute("user", new UserRegistrationDTO());
        return "register";
    }

    @PostMapping("/participant/form")
    public String register(@ModelAttribute("user") UserRegistrationDTO userDTO, Model model) {
        logger.info("Attempting to register user with email: {}", userDTO.getEmail());
        try {
            userService.registerUser(userDTO);
            logger.info("User registered successfully: {}", userDTO.getEmail());
            return "redirect:/login";
        } catch (UserWithSameEmailExists e) {
            logger.warn("Registration failed - email already exists: {}", userDTO.getEmail());
            model.addAttribute("error", e.getMessage());
            return "register";
        }
    }

    @GetMapping("/")
    public String userDetails(Model model, Principal principal) {
        if (principal == null) {
            logger.warn("Unauthenticated access to user details page");
            return "redirect:/login";
        }
        String email = principal.getName(); // from logged-in session
        logger.debug("Fetching user details for: {}", email);
        UserDTO userDTO = userService.getUserByEmail(email);
        model.addAttribute("user", userDTO);
        return "user-details";
    }
}
