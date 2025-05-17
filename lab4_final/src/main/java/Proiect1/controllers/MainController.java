package Proiect1.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;


@Controller
@RequestMapping("/")
public class MainController {


    @RequestMapping("")
    public String productForm() {

        return "main";
    }



}