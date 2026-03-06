package id.ac.ui.cs.advprog.inventorykatalog.service;

import id.ac.ui.cs.advprog.inventorykatalog.model.Product;
import id.ac.ui.cs.advprog.inventorykatalog.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private ProductServiceImpl productService;

    private Product product;

    @BeforeEach
    void setUp() {
        product = new Product();
        product.setId("uuid-123");
        product.setNama("Sepatu Keren");
        product.setJastiperId("jastiper-abc");
    }

    @Test
    void testSave() {
        when(productRepository.save(product)).thenReturn(product);
        Product saved = productService.save(product);
        assertEquals(product.getNama(), saved.getNama());
        verify(productRepository, times(1)).save(product);
    }

    @Test
    void testFindById() {
        when(productRepository.findById("uuid-123")).thenReturn(Optional.of(product));
        Optional<Product> found = productService.findById("uuid-123");
        assertTrue(found.isPresent());
        assertEquals("Sepatu Keren", found.get().getNama());
    }

    @Test
    void testFindAll() {
        List<Product> products = new ArrayList<>();
        products.add(product);
        when(productRepository.findAll()).thenReturn(products);
        List<Product> result = productService.findAll();
        assertEquals(1, result.size());
    }

    @Test
    void testDeleteById() {
        doNothing().when(productRepository).deleteById("uuid-123");
        productService.deleteById("uuid-123");
        verify(productRepository, times(1)).deleteById("uuid-123");
    }

    @Test
    void testFindByNama() {
        List<Product> products = new ArrayList<>();
        products.add(product);
        when(productRepository.findByNamaContainingIgnoreCase("sepatu")).thenReturn(products);
        List<Product> result = productService.findByNamaContainingIgnoreCase("sepatu");
        assertFalse(result.isEmpty());
    }
}