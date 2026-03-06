package id.ac.ui.cs.advprog.inventorykatalog.service;

import id.ac.ui.cs.advprog.inventorykatalog.model.Product;
import id.ac.ui.cs.advprog.inventorykatalog.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ProductServiceImpl implements ProductService {

    @Autowired
    private ProductRepository productRepository;

    @Override
    public Product save(Product product) {
        return productRepository.save(product);
    }

    @Override
    public Optional<Product> findById(String id) {
        return productRepository.findById(id);
    }

    @Override
    public List<Product> findAll() {
        return productRepository.findAll();
    }

    @Override
    public void deleteById(String id) {
        productRepository.deleteById(id);
    }

    @Override
    public boolean existsById(String id) {
        return productRepository.existsById(id);
    }

    @Override
    public List<Product> findByNamaContainingIgnoreCase(String nama) {
        return productRepository.findByNamaContainingIgnoreCase(nama);
    }

    @Override
    public List<Product> findByJastiperId(String jastiperId) {
        return productRepository.findByJastiperId(jastiperId);
    }

    @Override
    public long count() {
        return productRepository.count();
    }

    @Override
    public void deleteAll() {
        productRepository.deleteAll();
    }
}