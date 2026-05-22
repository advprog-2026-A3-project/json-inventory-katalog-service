package id.ac.ui.cs.advprog.inventorykatalog.service;

import id.ac.ui.cs.advprog.inventorykatalog.model.Product;
import id.ac.ui.cs.advprog.inventorykatalog.repository.ProductRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

@Service
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;

    public ProductServiceImpl(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

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

    @Override
    @Transactional
    public Product reduceStock(String id, int quantity) {
        if (quantity < 1) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Quantity must be at least 1"
            );
        }

        int updatedRows = productRepository.reduceStockIfAvailable(id, quantity);

        if (updatedRows == 0) {
            if (!productRepository.existsById(id)) {
                throw new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Product not found"
                );
            }

            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Insufficient product stock"
            );
        }

        return productRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Product not found"
                ));
    }

    @Override
    @Transactional
    public Product addRating(String id, double newRating) {
        if (newRating < 0.0 || newRating > 5.0) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Rating must be between 0.0 and 5.0"
            );
        }

        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Product not found"
                ));

        double currentRating = product.getRating() == null ? 0.0 : product.getRating();
        int currentRatingCount = product.getRatingCount();

        double updatedRating = ((currentRating * currentRatingCount) + newRating)
                / (currentRatingCount + 1);

        product.setRating(updatedRating);
        product.setRatingCount(currentRatingCount + 1);

        return productRepository.save(product);
    }
}