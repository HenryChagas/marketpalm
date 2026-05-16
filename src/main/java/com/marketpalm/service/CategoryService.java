package com.marketpalm.service;

import com.marketpalm.model.Category;
import com.marketpalm.repository.CategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class CategoryService {

    @Autowired
    private CategoryRepository categoryRepository;

    public Category salvar(Category category) {
        return categoryRepository.save(category);
    }

    public List<Category> listarTodas() {
        return categoryRepository.findAll();
    }

    public Category buscarPorId(Long id) {
        return categoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Categoria não encontrada!"));
    }
}