package id.ac.ui.cs.advprog.inventorykatalog.controller;

import id.ac.ui.cs.advprog.inventorykatalog.model.Product;
import id.ac.ui.cs.advprog.inventorykatalog.service.ProductService;
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
    private ProductService productService;

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
        // Panggil productService
        when(productService.findAll()).thenReturn(Arrays.asList(product1));

        mockMvc.perform(get("/api/products"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].nama").value("KitKat Matcha Jepang"));
    }

    @Test
    void testCreateProduct() throws Exception {
        // Panggil productService.save()
        when(productService.save(any(Product.class))).thenReturn(product1);

        String jsonRequest = "{\"nama\":\"KitKat Matcha Jepang\",\"harga\":55000.0,\"stok\":20}";

        mockMvc.perform(post("/api/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nama").value("KitKat Matcha Jepang"));
    }

    @Test
    void testDeleteProduct() throws Exception {
        // Panggil productService
        when(productService.existsById("123-abc")).thenReturn(true);

        mockMvc.perform(delete("/api/products/123-abc"))
                .andExpect(status().isOk());

        verify(productService, times(1)).deleteById("123-abc");
    }

    @Test
    void testUpdateProduct_Success() throws Exception {
        when(productService.findById("123-abc")).thenReturn(Optional.of(product1));
        when(productService.save(any(Product.class))).thenReturn(product1);

        String jsonRequest = "{\"nama\":\"KitKat Matcha Update\",\"harga\":60000.0,\"stok\":30}";

        mockMvc.perform(put("/api/products/123-abc")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                .andExpect(status().isOk());
    }

    @Test
    void testUpdateProduct_NotFound() throws Exception {
        when(productService.findById("999-xyz")).thenReturn(Optional.empty());

        String jsonRequest = "{\"nama\":\"Gak Ada\",\"harga\":0.0,\"stok\":0}";

        mockMvc.perform(put("/api/products/999-xyz")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                .andExpect(status().isNotFound());
    }

    @Test
    void testSearchByName() throws Exception {
        when(productService.findByNamaContainingIgnoreCase("KitKat")).thenReturn(Arrays.asList(product1));

        mockMvc.perform(get("/api/products/search")
                        .param("nama", "KitKat"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].nama").value("KitKat Matcha Jepang"));
    }

    @Test
    void testGetByJastiper() throws Exception {
        when(productService.findByJastiperId("jastiper-001")).thenReturn(Arrays.asList(product1));

        mockMvc.perform(get("/api/products/jastiper/jastiper-001"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].nama").value("KitKat Matcha Jepang"));
    }

    @Test
    void testInitDummyData_CountZero() {
        when(productService.count()).thenReturn(0L);
        productController.initDummyData();
        verify(productService, times(1)).save(any(Product.class));
    }

    @Test
    void testInitDummyData_CountNotZero() {
        when(productService.count()).thenReturn(1L);
        productController.initDummyData();
        verify(productService, never()).save(any(Product.class));
    }
}