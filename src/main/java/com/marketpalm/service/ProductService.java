package com.marketpalm.service;

import com.marketpalm.model.Category;
import com.marketpalm.model.Product;
import com.marketpalm.model.Sale;
import com.marketpalm.repository.CategoryRepository;
import com.marketpalm.repository.ProductRepository;
import com.marketpalm.repository.SaleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class ProductService {
    @Autowired
    private SaleRepository saleRepository;
    @Autowired // O Spring injeta o repositório aqui automaticamente
    private ProductRepository productRepository;
    @Autowired
    private CategoryRepository categoryRepository;

    public Product salvarProduto(Product product) {
        // Regra Profissional: Validar se o preço é positivo
        if (product.getPrice().doubleValue() <= 0) {
            throw new RuntimeException("O preço deve ser maior que zero!");
        }

        // NOVA LÓGICA: Se o produto veio com uma categoria e essa categoria tem um ID...
        if (product.getCategory() != null && product.getCategory().getId() != null) {
            // Buscamos a categoria completa no banco (que tem o nome preenchido)
            Category category = categoryRepository.findById(product.getCategory().getId())
                    .orElseThrow(() -> new RuntimeException("Categoria não encontrada!"));

            // Associamos a categoria completa (com ID e Nome) ao produto antes de salvar
            product.setCategory(category);
        }

        return productRepository.save(product);
    }

    public List<Product> listarTodos() {
        return productRepository.findAll();
    }

    public Product buscarPorCodigo(String barcode) {
        return productRepository.findByBarcode(barcode)
                .orElseThrow(() -> new RuntimeException("Produto não encontrado com o código: " + barcode));
    }

    public Product baixarEstoque(String barcode, Integer quantidade) {
        Product product = buscarPorCodigo(barcode); // Reutilizamos a busca que já funciona!

        if (product.getStock() < quantidade) {
            throw new RuntimeException("Estoque insuficiente para o produto: " + product.getName());
        }

        product.setStock(product.getStock() - quantidade);
        return productRepository.save(product);
    }

    public void deletarProduto(Long id) {
        if (!productRepository.existsById(id)) {
            throw new RuntimeException("Não é possível deletar. Produto não encontrado!");
        }
        productRepository.deleteById(id);
    }

    public Sale registrarVenda(String barcode, Integer quantidade) {
        // 1. Busca o produto e valida estoque (já fizemos isso!)
        Product product = buscarPorCodigo(barcode);

        if (product.getStock() < quantidade) {
            throw new RuntimeException("Estoque insuficiente!");
        }

        // 2. Baixa o estoque do produto
        product.setStock(product.getStock() - quantidade);
        productRepository.save(product);

        // 3. Cria o registro da Venda (O Recibo)
        Sale venda = new Sale();
        venda.setProduct(product);
        venda.setQuantity(quantidade);
        venda.setSaleDate(LocalDateTime.now()); // Pega a hora exata agora

        // Calcula o total: preço do produto * quantidade
        BigDecimal total = product.getPrice().multiply(new BigDecimal(quantidade));
        venda.setTotalPrice(total);

        // 4. Salva a venda no banco
        return saleRepository.save(venda);

        }

    public List<Product> listarPorCategoria(Long categoryId) {
        return productRepository.findByCategoryId(categoryId);
    }

}