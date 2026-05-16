package com.marketpalm.repository;

import com.marketpalm.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    // Este método é "mágico": o Spring entende que deve buscar pelo campo 'barcode'
    Optional<Product> findByBarcode(String barcode);

    // Busca todos os produtos de uma categoria específica
    List<Product> findByCategoryId(Long categoryId);
}