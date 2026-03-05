package id.ac.ui.cs.advprog.inventorykatalog.repository;

import id.ac.ui.cs.advprog.inventorykatalog.model.Product;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class ProductRepositoryTest {

    @Autowired
    private ProductRepository productRepository;

    private Product product1;
    private Product product2;

    @BeforeEach
    void setUp() {
        product1 = new Product();
        product1.setNama("Sepatu Kets");
        product1.setStok(15);
        product1.setHarga(300000.0);
        product1.setJastiperId("jastip-001");
        productRepository.save(product1);

        product2 = new Product();
        product2.setNama("Tas Ransel Kets");
        product2.setStok(5);
        product2.setHarga(150000.0);
        product2.setJastiperId("jastip-002");
        productRepository.save(product2);
    }

    @Test
    void testSaveAndFindById() {
        Optional<Product> foundProduct = productRepository.findById(product1.getId());
        assertTrue(foundProduct.isPresent());
        assertEquals("Sepatu Kets", foundProduct.get().getNama());
    }

    @Test
    void testFindByNamaContainingIgnoreCase() {
        List<Product> products = productRepository.findByNamaContainingIgnoreCase("kets");
        assertEquals(2, products.size());
    }

    @Test
    void testFindByJastiperId() {
        List<Product> products = productRepository.findByJastiperId("jastip-002");
        assertEquals(1, products.size());
        assertEquals("Tas Ransel Kets", products.get(0).getNama());
    }
}