package com.marketpalm.controller;

import com.marketpalm.model.Product;
import com.marketpalm.model.Sale;
import com.marketpalm.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin("*")
@RestController // Diz ao Spring que esta classe responde a chamadas HTTP (APIs)
@RequestMapping("/api/products") // O "endereço" base desta API
public class ProductController {

    @Autowired
    private ProductService productService;

    // Rota para listar todos os produtos: GET http://localhost:8080/api/products
    @GetMapping
    public List<Product> getAll() {
        return productService.listarTodos();
    }

    // Rota para cadastrar um produto: POST http://localhost:8080/api/products
    @PostMapping
    public Product create(@RequestBody Product product) {
        return productService.salvarProduto(product);
    }

    // Buscar por código de barras: GET http://localhost:8080/api/products/barcode/7891234567890
    @GetMapping("/barcode/{barcode}")
    public Product getByBarcode(@PathVariable String barcode) {
        return productService.buscarPorCodigo(barcode);
    }

    // Deletar produto: DELETE http://localhost:8080/api/products/1
    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        productService.deletarProduto(id);
    }

    // Buscar produtos por categoria: GET http://localhost:8080/api/products/categoria/1
    @GetMapping("/categoria/{categoryId}")
    public List<Product> listarPorCategoria(@PathVariable Long categoryId) {
        return productService.listarPorCategoria(categoryId);
    }
}