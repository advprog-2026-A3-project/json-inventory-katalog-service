package id.ac.ui.cs.advprog.inventorykatalog.repository;

import id.ac.ui.cs.advprog.inventorykatalog.model.Product;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductRepositoryTest {

    @Mock
    private ProductRepository productRepository;

    private Product product1;

    @BeforeEach
    void setUp() {
        product1 = new Product();
        product1.setId("123-abc");
        product1.setNama("Sepatu Kets");
        product1.setStok(15);
        product1.setHarga(300000.0);
        product1.setJastiperId("jastip-001");
    }

    @Test
    void testFindById() {
        when(productRepository.findById("123-abc")).thenReturn(Optional.of(product1));

        Optional<Product> foundProduct = productRepository.findById("123-abc");
        assertTrue(foundProduct.isPresent());
        assertEquals("Sepatu Kets", foundProduct.get().getNama());
    }

    @Test
    void testFindByNamaContainingIgnoreCase() {
        when(productRepository.findByNamaContainingIgnoreCase("kets")).thenReturn(List.of(product1));

        List<Product> products = productRepository.findByNamaContainingIgnoreCase("kets");
        assertEquals(1, products.size());
        assertEquals("Sepatu Kets", products.get(0).getNama());
    }

    @Test
    void testFindByJastiperId() {
        when(productRepository.findByJastiperId("jastip-001")).thenReturn(List.of(product1));

        List<Product> products = productRepository.findByJastiperId("jastip-001");
        assertEquals(1, products.size());
        assertEquals("Sepatu Kets", products.get(0).getNama());
    }
}