package id.ac.ui.cs.advprog.inventorykatalog.controller;

import id.ac.ui.cs.advprog.inventorykatalog.client.AuthClient;
import id.ac.ui.cs.advprog.inventorykatalog.model.Product;
import id.ac.ui.cs.advprog.inventorykatalog.service.ProductService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.server.ResponseStatusException;

import java.util.Arrays;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class ProductControllerTest {

    private MockMvc mockMvc;

    @Mock
    private ProductService productService;

    @Mock
    private AuthClient authClient;

    @InjectMocks
    private ProductController productController;

    private Product product1;

    private static final String AUTH_HEADER = "Bearer test-token";
    private static final String JASTIPER_ID = "jastiper-001";

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders
                .standaloneSetup(productController)
                .build();

        lenient()
                .when(authClient.getCurrentJastiperId(anyString()))
                .thenReturn(JASTIPER_ID);

        product1 = new Product();
        product1.setId("123-abc");
        product1.setNama("KitKat Matcha Jepang");
        product1.setDeskripsi("Cokelat asli dari Akihabara");
        product1.setHarga(55000.0);
        product1.setStok(20);
        product1.setNegaraAsal("Jepang");
        product1.setJastiperId(JASTIPER_ID);
    }

    @Test
    void testGetAllProducts() throws Exception {
        when(productService.findAll())
                .thenReturn(Arrays.asList(product1));

        mockMvc.perform(get("/api/products"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].nama").value("KitKat Matcha Jepang"));
    }

    @Test
    void testCreateProduct() throws Exception {
        when(productService.save(any(Product.class)))
                .thenReturn(product1);

        String jsonRequest = """
                {
                  "nama": "KitKat Matcha Jepang",
                  "deskripsi": "Cokelat asli dari Akihabara",
                  "harga": 55000.0,
                  "stok": 20,
                  "negaraAsal": "Jepang",
                  "imageUrls": []
                }
                """;

        mockMvc.perform(post("/api/products")
                        .header(HttpHeaders.AUTHORIZATION, AUTH_HEADER)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nama").value("KitKat Matcha Jepang"));

        verify(authClient).getCurrentJastiperId(AUTH_HEADER);

        verify(productService).save(argThat(product ->
                JASTIPER_ID.equals(product.getJastiperId())
        ));
    }

    @Test
    void testCreateProductForbiddenWhenNotJastiper() throws Exception {
        when(authClient.getCurrentJastiperId(anyString()))
                .thenThrow(new ResponseStatusException(
                        HttpStatus.FORBIDDEN,
                        "Only JASTIPER can manage products"
                ));

        String jsonRequest = """
                {
                  "nama": "Produk Titipers",
                  "harga": 10000.0,
                  "stok": 5,
                  "negaraAsal": "Indonesia",
                  "imageUrls": []
                }
                """;

        mockMvc.perform(post("/api/products")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer titipers-token")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                .andExpect(status().isForbidden());

        verify(productService, never()).save(any(Product.class));
    }

    @Test
    void testGetMyProducts() throws Exception {
        when(productService.findByJastiperId(JASTIPER_ID))
                .thenReturn(Arrays.asList(product1));

        mockMvc.perform(get("/api/products/me")
                        .header(HttpHeaders.AUTHORIZATION, AUTH_HEADER))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].nama").value("KitKat Matcha Jepang"));

        verify(authClient).getCurrentJastiperId(AUTH_HEADER);
        verify(productService).findByJastiperId(JASTIPER_ID);
    }

    @Test
    void testGetProductById() throws Exception {
        when(productService.findById("123-abc"))
                .thenReturn(Optional.of(product1));

        mockMvc.perform(get("/api/products/123-abc"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nama").value("KitKat Matcha Jepang"));
    }

    @Test
    void testGetProductByIdNotFound() throws Exception {
        when(productService.findById("id-ngasal"))
                .thenReturn(Optional.empty());

        mockMvc.perform(get("/api/products/id-ngasal"))
                .andExpect(status().isNotFound());
    }

    @Test
    void testUpdateProductSuccess() throws Exception {
        when(productService.findById("123-abc"))
                .thenReturn(Optional.of(product1));

        when(productService.save(any(Product.class)))
                .thenReturn(product1);

        String jsonRequest = """
                {
                  "nama": "KitKat Matcha Update",
                  "deskripsi": "Updated description",
                  "harga": 60000.0,
                  "stok": 30,
                  "negaraAsal": "Jepang",
                  "imageUrls": []
                }
                """;

        mockMvc.perform(put("/api/products/123-abc")
                        .header(HttpHeaders.AUTHORIZATION, AUTH_HEADER)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                .andExpect(status().isOk());

        verify(authClient).getCurrentJastiperId(AUTH_HEADER);
        verify(productService).save(any(Product.class));
    }

    @Test
    void testUpdateProductNotFound() throws Exception {
        when(productService.findById("999-xyz"))
                .thenReturn(Optional.empty());

        String jsonRequest = """
                {
                  "nama": "Gak Ada",
                  "harga": 0.0,
                  "stok": 0,
                  "imageUrls": []
                }
                """;

        mockMvc.perform(put("/api/products/999-xyz")
                        .header(HttpHeaders.AUTHORIZATION, AUTH_HEADER)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                .andExpect(status().isNotFound());

        verify(authClient, never()).getCurrentJastiperId(anyString());
        verify(productService, never()).save(any(Product.class));
    }

    @Test
    void testUpdateProductForbiddenWhenNotOwner() throws Exception {
        product1.setJastiperId("different-jastiper");

        when(productService.findById("123-abc"))
                .thenReturn(Optional.of(product1));

        String jsonRequest = """
                {
                  "nama": "KitKat Matcha Update",
                  "harga": 60000.0,
                  "stok": 30,
                  "imageUrls": []
                }
                """;

        mockMvc.perform(put("/api/products/123-abc")
                        .header(HttpHeaders.AUTHORIZATION, AUTH_HEADER)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                .andExpect(status().isForbidden());

        verify(productService, never()).save(any(Product.class));
    }

    @Test
    void testUpdateProductWithImages() throws Exception {
        product1.setImageUrls(Arrays.asList("http://img.com/1.jpg"));

        when(productService.findById("123-abc"))
                .thenReturn(Optional.of(product1));

        when(productService.save(any(Product.class)))
                .thenReturn(product1);

        String jsonRequest = """
                {
                  "nama": "KitKat Update",
                  "harga": 60000.0,
                  "stok": 30,
                  "imageUrls": ["http://img.com/new.jpg"]
                }
                """;

        mockMvc.perform(put("/api/products/123-abc")
                        .header(HttpHeaders.AUTHORIZATION, AUTH_HEADER)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                .andExpect(status().isOk());

        verify(productService).save(argThat(product ->
                product.getImageUrls().contains("http://img.com/new.jpg")
        ));
    }

    @Test
    void testDeleteProduct() throws Exception {
        when(productService.findById("123-abc"))
                .thenReturn(Optional.of(product1));

        mockMvc.perform(delete("/api/products/123-abc")
                        .header(HttpHeaders.AUTHORIZATION, AUTH_HEADER))
                .andExpect(status().isOk());

        verify(authClient).getCurrentJastiperId(AUTH_HEADER);
        verify(productService, times(1)).deleteById("123-abc");
    }

    @Test
    void testDeleteProductNotFound() throws Exception {
        when(productService.findById("id-ngasal"))
                .thenReturn(Optional.empty());

        mockMvc.perform(delete("/api/products/id-ngasal")
                        .header(HttpHeaders.AUTHORIZATION, AUTH_HEADER))
                .andExpect(status().isNotFound());

        verify(authClient, never()).getCurrentJastiperId(anyString());
        verify(productService, never()).deleteById(anyString());
    }

    @Test
    void testDeleteProductForbiddenWhenNotOwner() throws Exception {
        product1.setJastiperId("different-jastiper");

        when(productService.findById("123-abc"))
                .thenReturn(Optional.of(product1));

        mockMvc.perform(delete("/api/products/123-abc")
                        .header(HttpHeaders.AUTHORIZATION, AUTH_HEADER))
                .andExpect(status().isForbidden());

        verify(productService, never()).deleteById(anyString());
    }

    @Test
    void testSearchByName() throws Exception {
        when(productService.findByNamaContainingIgnoreCase("KitKat"))
                .thenReturn(Arrays.asList(product1));

        mockMvc.perform(get("/api/products/search/nama")
                        .param("nama", "KitKat"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].nama").value("KitKat Matcha Jepang"));
    }

    @Test
    void testGetByJastiper() throws Exception {
        when(productService.findByJastiperId(JASTIPER_ID))
                .thenReturn(Arrays.asList(product1));

        mockMvc.perform(get("/api/products/search/jastiper")
                        .param("jastiperId", JASTIPER_ID))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].nama").value("KitKat Matcha Jepang"));
    }

    @Test
    void testInitDummyDataCountZero() {
        when(productService.count())
                .thenReturn(0L);

        productController.initDummyData();

        verify(productService, times(1)).save(any(Product.class));
    }

    @Test
    void testInitDummyDataCountNotZero() {
        when(productService.count())
                .thenReturn(1L);

        productController.initDummyData();

        verify(productService, never()).save(any(Product.class));
    }
}