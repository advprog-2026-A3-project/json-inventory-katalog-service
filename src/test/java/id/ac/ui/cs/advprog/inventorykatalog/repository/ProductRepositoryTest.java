package id.ac.ui.cs.advprog.inventorykatalog.repository;

import id.ac.ui.cs.advprog.inventorykatalog.model.Product;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Import(ProductRepositoryImpl.class)
class ProductRepositoryTest {

    @Autowired
    private ProductRepository productRepository;

    private Product product1;
    private Product product2;

    @BeforeEach
    void setUp() {
        product1 = Product.builder()
                .nama("Sepatu Kets")
                .stok(15)
                .harga(300000.0)
                .jastiperId("jastip-001")
                .build();
        product1 = productRepository.save(product1);

        product2 = Product.builder()
                .nama("Tas Ransel Kets")
                .stok(5)
                .harga(150000.0)
                .jastiperId("jastip-002")
                .build();
        product2 = productRepository.save(product2);
    }

    @Test
    void testSaveAndFindById() {
        Optional<Product> foundProduct = productRepository.findById(product1.getId());
        assertTrue(foundProduct.isPresent());
        assertEquals("Sepatu Kets", foundProduct.get().getNama());
    }

    @Test
    void testFindAll() {
        List<Product> products = productRepository.findAll();
        assertEquals(2, products.size());
    }

    @Test
    void testFindByNamaContainingIgnoreCase() {
        List<Product> products = productRepository.findByNamaContainingIgnoreCase("KETS");
        assertEquals(2, products.size());
    }

    @Test
    void testDeleteById() {
        productRepository.deleteById(product1.getId());

        Optional<Product> deletedProduct = productRepository.findById(product1.getId());
        assertFalse(deletedProduct.isPresent());
    }
}