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

    @Test
    void testUpdateProduct_Success() throws Exception {
        // Skenario: Produk ditemukan dan berhasil diupdate
        when(productRepository.findById("123-abc")).thenReturn(Optional.of(product1));
        when(productRepository.save(any(Product.class))).thenReturn(product1);

        String jsonRequest = "{\"nama\":\"KitKat Matcha Update\",\"harga\":60000.0,\"stok\":30}";

        mockMvc.perform(put("/api/products/123-abc")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                .andExpect(status().isOk());
    }

    @Test
    void testUpdateProduct_NotFound() throws Exception {
        when(productRepository.findById("999-xyz")).thenReturn(Optional.empty());

        String jsonRequest = "{\"nama\":\"Gak Ada\",\"harga\":0.0,\"stok\":0}";

        mockMvc.perform(put("/api/products/999-xyz")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                .andExpect(status().isNotFound());
    }

    @Test
    void testDeleteProduct_NotFound() throws Exception {
        // Delete produk tapi id-nya tidak ada (masuk ke blok 'else' -> return 404)
        when(productRepository.existsById("999-xyz")).thenReturn(false);

        mockMvc.perform(delete("/api/products/999-xyz"))
                .andExpect(status().isNotFound());
    }

    @Test
    void testSearchByName() throws Exception {
        when(productRepository.findByNamaContainingIgnoreCase("KitKat")).thenReturn(Arrays.asList(product1));

        mockMvc.perform(get("/api/products/search")
                        .param("nama", "KitKat"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].nama").value("KitKat Matcha Jepang"));
    }

    @Test
    void testGetByJastiper() throws Exception {
        // Test endpoint get by jastiper id
        when(productRepository.findByJastiperId("jastiper-001")).thenReturn(Arrays.asList(product1));

        mockMvc.perform(get("/api/products/jastiper/jastiper-001"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].nama").value("KitKat Matcha Jepang"));
    }

    @Test
    void testInitDummyData_CountZero() {
        // Database kosong, maka dummy data dibuat
        when(productRepository.count()).thenReturn(0L);
        productController.initDummyData();
        verify(productRepository, times(1)).save(any(Product.class));
    }

    @Test
    void testInitDummyData_CountNotZero() {
        // Database sudah ada isinya, dummy data tidak dibuat
        when(productRepository.count()).thenReturn(1L);
        productController.initDummyData();
        verify(productRepository, never()).save(any(Product.class));
    }
}