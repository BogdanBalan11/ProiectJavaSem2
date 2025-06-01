package Proiect1.controllers;

import Proiect1.domain.Category;
import Proiect1.dtos.CategoryDTO;
import Proiect1.repositories.CategoryRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/categories")
public class CategoryController {

    private static final Logger logger = LoggerFactory.getLogger(CategoryController.class);

    private final CategoryRepository categoryRepository;

    @Autowired
    public CategoryController(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    @GetMapping("")
    public String listCategories(Model model) {
        logger.info("Fetching all categories");
        model.addAttribute("categories", categoryRepository.findAll());
        return "categoriesList";
    }

    @GetMapping("/add")
    public String showAddForm(Model model) {
        logger.debug("Opening add category form");
        model.addAttribute("category", new CategoryDTO());
        return "categoryForm";
    }

    @PostMapping("/add")
    public String addCategory(@ModelAttribute("category") CategoryDTO dto) {
        logger.info("Saving new category: name='{}'", dto.getName());

        Category category = new Category();
        category.setName(dto.getName());
        category.setDescription(dto.getDescription());
        categoryRepository.save(category);

        logger.debug("Category saved with name='{}'", category.getName());
        return "redirect:/categories";
    }

    @GetMapping("/delete/{id}")
    public String deleteCategory(@PathVariable Long id) {
        logger.warn("Deleting category with id={}", id);
        categoryRepository.deleteById(id);
        return "redirect:/categories";
    }
}
