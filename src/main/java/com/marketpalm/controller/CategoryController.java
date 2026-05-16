package com.marketpalm.controller;

import com.marketpalm.model.Category;
import com.marketpalm.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/categories")
@CrossOrigin("*")
public class CategoryController {

    @Autowired
    private CategoryService categoryService;

    @PostMapping
    public Category criar(@RequestBody Category category) {
        return categoryService.salvar(category);
    }

    @GetMapping
    public List<Category> listar() {
        return categoryService.listarTodas();
    }
}