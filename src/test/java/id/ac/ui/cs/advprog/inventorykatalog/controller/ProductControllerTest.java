package id.ac.ui.cs.advprog.inventorykatalog.controller;

import id.ac.ui.cs.advprog.inventorykatalog.model.Product;
import id.ac.ui.cs.advprog.inventorykatalog.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Arrays;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class ProductControllerTest {

    private MockMvc mockMvc;

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private ProductController productController;

    private Product product1;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(productController).build();

        product1 = new Product();
        product1.setId("123-abc");
        product1.setNama("KitKat Matcha Jepang");
        product1.setHarga(55000.0);
        product1.setStok(20);
    }

    @Test
    void testGetAllProducts() throws Exception {
        when(productRepository.findAll()).thenReturn(Arrays.asList(product1));

        mockMvc.perform(get("/api/products"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].nama").value("KitKat Matcha Jepang"));
    }

    @Test
    void testCreateProduct() throws Exception {
        when(productRepository.save(any(Product.class))).thenReturn(product1);

        String jsonRequest = "{\"nama\":\"KitKat Matcha Jepang\",\"harga\":55000.0,\"stok\":20}";

        mockMvc.perform(post("/api/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nama").value("KitKat Matcha Jepang"));
    }

    @Test
    void testDeleteProduct() throws Exception {
        when(productRepository.existsById("123-abc")).thenReturn(true);

        mockMvc.perform(delete("/api/products/123-abc"))
                .andExpect(status().isOk());

        verify(productRepository, times(1)).deleteById("123-abc");
    }
}