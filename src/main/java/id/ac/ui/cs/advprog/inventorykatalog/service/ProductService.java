package id.ac.ui.cs.advprog.inventorykatalog.service;

import id.ac.ui.cs.advprog.inventorykatalog.model.Product;
import java.util.List;
import java.util.Optional;

public interface ProductService {
    Product save(Product product);
    Optional<Product> findById(String id);
    List<Product> findAll();
    void deleteById(String id);
    boolean existsById(String id);
    List<Product> findByNamaContainingIgnoreCase(String nama);
    List<Product> findByJastiperId(String jastiperId);
    long count();
    void deleteAll();
}