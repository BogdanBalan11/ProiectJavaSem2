package Proiect1.controllers;

import Proiect1.domain.Category;
import Proiect1.dtos.CategoryDTO;
import Proiect1.repositories.CategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/categories")
public class CategoryController {

    private final CategoryRepository categoryRepository;

    @Autowired
    public CategoryController(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    @GetMapping("")
    public String listCategories(Model model) {
        model.addAttribute("categories", categoryRepository.findAll());
        return "categoriesList";
    }

    @GetMapping("/add")
    public String showAddForm(Model model) {
        model.addAttribute("category", new CategoryDTO());
        return "categoryForm";
    }

    @PostMapping("/add")
    public String addCategory(@ModelAttribute("category") CategoryDTO dto) {
        Category category = new Category();
        category.setName(dto.getName());
        category.setDescription(dto.getDescription());
        categoryRepository.save(category);
        return "redirect:/categories";
    }

    @GetMapping("/delete/{id}")
    public String deleteCategory(@PathVariable Long id) {
        categoryRepository.deleteById(id);
        return "redirect:/categories";
    }
}
