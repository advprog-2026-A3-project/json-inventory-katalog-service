package id.ac.ui.cs.advprog.inventorykatalog.repository;

import id.ac.ui.cs.advprog.inventorykatalog.model.Product;
import java.util.List;
import java.util.Optional;

public interface ProductRepository {
    Product save(Product product);
    Optional<Product> findById(String id);
    List<Product> findAll();
    void deleteById(String id);
    boolean existsById(String id);
    List<Product> findByNamaContainingIgnoreCase(String nama);
    List<Product> findByJastiperId(String jastiperId);
    long count();
    void deleteAll();

    // (Concurrency): update agar stok tidak minus saat banyak checkout bersamaan.
    int reduceStockIfAvailable(String id, int quantity);
}