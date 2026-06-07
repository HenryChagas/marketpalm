package com.marketpalm.service;

import com.marketpalm.model.Category;
import com.marketpalm.model.Product;
import com.marketpalm.repository.CategoryRepository;
import com.marketpalm.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.*;
import java.util.List;
import java.util.UUID;

@Service
public class ProductService {

    // Pasta onde as imagens serão salvas (relativa ao diretório de execução)
    private static final String PASTA_IMAGENS = "uploads/imagens/";

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    public Product salvarProduto(Product product) {
        if (product.getPrice().doubleValue() <= 0) {
            throw new RuntimeException("O preço deve ser maior que zero!");
        }
        if (product.getCategory() != null && product.getCategory().getId() != null) {
            Category category = categoryRepository.findById(product.getCategory().getId())
                    .orElseThrow(() -> new RuntimeException("Categoria não encontrada!"));
            product.setCategory(category);
        }
        return productRepository.save(product);
    }

    public Product buscarPorId(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Produto não encontrado com o ID: " + id));
    }

    public Product atualizarProduto(Long id, Product dadosNovos, MultipartFile imagem) throws IOException {
        Product existente = buscarPorId(id);

        existente.setName(dadosNovos.getName());
        existente.setBarcode(dadosNovos.getBarcode());
        existente.setPrice(dadosNovos.getPrice());
        existente.setStock(dadosNovos.getStock());

        if (dadosNovos.getCategory() != null && dadosNovos.getCategory().getId() != null) {
            Category category = categoryRepository.findById(dadosNovos.getCategory().getId())
                    .orElseThrow(() -> new RuntimeException("Categoria não encontrada!"));
            existente.setCategory(category);
        }

        if (imagem != null && !imagem.isEmpty()) {
            String caminhoImagem = salvarImagem(imagem);
            existente.setImagemUrl(caminhoImagem);
        }

        return productRepository.save(existente);
    }

    /**
     * Salva o arquivo de imagem na pasta local e retorna o caminho relativo.
     * O nome do arquivo é gerado com UUID para evitar colisões.
     */
    public String salvarImagem(MultipartFile arquivo) throws IOException {
        Path pasta = Paths.get(PASTA_IMAGENS);
        if (!Files.exists(pasta)) {
            Files.createDirectories(pasta);
        }

        String extensao = "";
        String nomeOriginal = arquivo.getOriginalFilename();
        if (nomeOriginal != null && nomeOriginal.contains(".")) {
            extensao = nomeOriginal.substring(nomeOriginal.lastIndexOf("."));
        }

        String nomeArquivo = UUID.randomUUID() + extensao;
        Path destino = pasta.resolve(nomeArquivo);
        Files.copy(arquivo.getInputStream(), destino, StandardCopyOption.REPLACE_EXISTING);

        return "/" + PASTA_IMAGENS + nomeArquivo;
    }

    public List<Product> listarTodos() {
        return productRepository.findAll();
    }

    public List<Product> buscarPorNome(String nome) {
        if (nome == null || nome.isBlank()) return listarTodos();
        return productRepository.findByNameContainingIgnoreCase(nome);
    }

    public Product buscarPorCodigo(String barcode) {
        return productRepository.findByBarcode(barcode)
                .orElseThrow(() -> new RuntimeException("Produto não encontrado com o código: " + barcode));
    }

    public void deletarProduto(Long id) {
        if (!productRepository.existsById(id)) {
            throw new RuntimeException("Não é possível deletar. Produto não encontrado!");
        }
        productRepository.deleteById(id);
    }

    public List<Product> listarPorCategoria(Long categoryId) {
        return productRepository.findByCategoryId(categoryId);
    }
}