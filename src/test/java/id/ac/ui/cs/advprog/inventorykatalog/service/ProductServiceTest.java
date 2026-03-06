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
        product.setId("eb558e9f-1c39-460e-8860-71af6af63bd6");
        product.setNama("Sepatu Limited Edition");
        product.setDeskripsi("Sepatu jastip dari Jepang");
        product.setHarga(2000000);
        product.setStok(5);
        product.setNegaraAsal("Jepang");
        product.setJastiperId("user-123");
    }

    @Test
    void testCreateProduct() {
        when(productRepository.create(product)).thenReturn(product);

        Product createdProduct = productService.create(product);

        assertNotNull(createdProduct);
        assertEquals(product.getNama(), createdProduct.getNama());
        verify(productRepository, times(1)).create(product);
    }

    @Test
    void testFindAllProducts() {
        List<Product> productList = new ArrayList<>();
        productList.add(product);
        when(productRepository.findAll()).thenReturn(productList);

        List<Product> allProducts = productService.findAll();

        assertFalse(allProducts.isEmpty());
        assertEquals(1, allProducts.size());
        assertEquals(product.getNama(), allProducts.get(0).getNama());
        verify(productRepository, times(1)).findAll();
    }

    @Test
    void testFindProductById() {
        when(productRepository.findById("eb558e9f-1c39-460e-8860-71af6af63bd6")).thenReturn(product);

        Product foundProduct = productService.findById("eb558e9f-1c39-460e-8860-71af6af63bd6");

        assertNotNull(foundProduct);
        assertEquals("eb558e9f-1c39-460e-8860-71af6af63bd6", foundProduct.getId());
        verify(productRepository, times(1)).findById(anyString());
    }

    @Test
    void testDeleteProduct() {
        doNothing().when(productRepository).delete("eb558e9f-1c39-460e-8860-71af6af63bd6");

        productService.delete("eb558e9f-1c39-460e-8860-71af6af63bd6");

        verify(productRepository, times(1)).delete("eb558e9f-1c39-460e-8860-71af6af63bd6");
    }
}