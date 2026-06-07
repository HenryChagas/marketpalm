package com.marketpalm.controller;

import com.marketpalm.model.Category;
import com.marketpalm.model.Product;
import com.marketpalm.service.CategoryService;
import com.marketpalm.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Controller
@RequestMapping("/admin/cadastros")
public class CadastroController {

    @Autowired
    private ProductService productService;

    @Autowired
    private CategoryService categoryService;

    // ══════════════════════════════════════
    //  PRODUTOS
    // ══════════════════════════════════════

    // Listagem com busca opcional: GET /admin/cadastros/produtos?busca=coca
    @GetMapping("/produtos")
    public String listarProdutos(@RequestParam(required = false) String busca, Model model) {
        model.addAttribute("produtos", productService.buscarPorNome(busca));
        model.addAttribute("busca", busca);
        return "cadastros/produtos";
    }

    // Formulário de novo produto: GET /admin/cadastros/produtos/novo
    @GetMapping("/produtos/novo")
    public String novoProduto(Model model) {
        model.addAttribute("produto", new Product());
        model.addAttribute("categorias", categoryService.listarTodas());
        model.addAttribute("titulo", "Novo Produto");
        return "cadastros/produto-form";
    }

    // Formulário de edição: GET /admin/cadastros/produtos/{id}/editar
    @GetMapping("/produtos/{id}/editar")
    public String editarProduto(@PathVariable Long id, Model model) {
        model.addAttribute("produto", productService.buscarPorId(id));
        model.addAttribute("categorias", categoryService.listarTodas());
        model.addAttribute("titulo", "Editar Produto");
        return "cadastros/produto-form";
    }

    // Salvar novo produto (POST com multipart para suportar upload): POST /admin/cadastros/produtos/salvar
    @PostMapping("/produtos/salvar")
    public String salvarProduto(@ModelAttribute Product produto,
                                @RequestParam(value = "imagem", required = false) MultipartFile imagem,
                                Model model) {
        try {
            if (imagem != null && !imagem.isEmpty()) {
                String caminhoImagem = productService.salvarImagem(imagem);
                produto.setImagemUrl(caminhoImagem);
            }
            productService.salvarProduto(produto);
            return "redirect:/admin/cadastros/produtos?sucesso";
        } catch (Exception e) {
            model.addAttribute("erro", e.getMessage());
            model.addAttribute("produto", produto);
            model.addAttribute("categorias", categoryService.listarTodas());
            model.addAttribute("titulo", "Novo Produto");
            return "cadastros/produto-form";
        }
    }

    // Atualizar produto existente: POST /admin/cadastros/produtos/{id}/atualizar
    @PostMapping("/produtos/{id}/atualizar")
    public String atualizarProduto(@PathVariable Long id,
                                   @ModelAttribute Product produto,
                                   @RequestParam(value = "imagem", required = false) MultipartFile imagem,
                                   Model model) {
        try {
            productService.atualizarProduto(id, produto, imagem);
            return "redirect:/admin/cadastros/produtos?sucesso";
        } catch (IOException | RuntimeException e) {
            model.addAttribute("erro", e.getMessage());
            model.addAttribute("produto", produto);
            model.addAttribute("categorias", categoryService.listarTodas());
            model.addAttribute("titulo", "Editar Produto");
            return "cadastros/produto-form";
        }
    }

    // Excluir produto: POST /admin/cadastros/produtos/{id}/excluir
    @PostMapping("/produtos/{id}/excluir")
    public String excluirProduto(@PathVariable Long id) {
        try {
            productService.deletarProduto(id);
        } catch (RuntimeException ignored) {}
        return "redirect:/admin/cadastros/produtos";
    }

    // ══════════════════════════════════════
    //  CATEGORIAS
    // ══════════════════════════════════════

    // Listagem: GET /admin/cadastros/categorias
    @GetMapping("/categorias")
    public String listarCategorias(Model model) {
        model.addAttribute("categorias", categoryService.listarTodas());
        return "cadastros/categorias";
    }

    // Formulário de nova categoria: GET /admin/cadastros/categorias/nova
    @GetMapping("/categorias/nova")
    public String novaCategoria(Model model) {
        model.addAttribute("categoria", new Category());
        model.addAttribute("titulo", "Nova Categoria");
        return "cadastros/categoria-form";
    }

    // Formulário de edição: GET /admin/cadastros/categorias/{id}/editar
    @GetMapping("/categorias/{id}/editar")
    public String editarCategoria(@PathVariable Long id, Model model) {
        model.addAttribute("categoria", categoryService.buscarPorId(id));
        model.addAttribute("titulo", "Editar Categoria");
        return "cadastros/categoria-form";
    }

    // Salvar categoria: POST /admin/cadastros/categorias/salvar
    @PostMapping("/categorias/salvar")
    public String salvarCategoria(@ModelAttribute Category categoria, Model model) {
        try {
            categoryService.salvar(categoria);
            return "redirect:/admin/cadastros/categorias?sucesso";
        } catch (RuntimeException e) {
            model.addAttribute("erro", e.getMessage());
            model.addAttribute("categoria", categoria);
            model.addAttribute("titulo", "Nova Categoria");
            return "cadastros/categoria-form";
        }
    }

    // Atualizar categoria: POST /admin/cadastros/categorias/{id}/atualizar
    @PostMapping("/categorias/{id}/atualizar")
    public String atualizarCategoria(@PathVariable Long id,
                                     @ModelAttribute Category categoria,
                                     Model model) {
        try {
            categoria.setId(id);
            categoryService.salvar(categoria);
            return "redirect:/admin/cadastros/categorias?sucesso";
        } catch (RuntimeException e) {
            model.addAttribute("erro", e.getMessage());
            model.addAttribute("categoria", categoria);
            model.addAttribute("titulo", "Editar Categoria");
            return "cadastros/categoria-form";
        }
    }

    // Excluir categoria: POST /admin/cadastros/categorias/{id}/excluir
    @PostMapping("/categorias/{id}/excluir")
    public String excluirCategoria(@PathVariable Long id) {
        try {
            categoryService.deletar(id);
        } catch (RuntimeException ignored) {}
        return "redirect:/admin/cadastros/categorias";
    }
}