package id.ac.ui.cs.advprog.inventorykatalog.controller;

import id.ac.ui.cs.advprog.inventorykatalog.model.Product;
import id.ac.ui.cs.advprog.inventorykatalog.model.ProductType;
import id.ac.ui.cs.advprog.inventorykatalog.service.ProductService;
import id.ac.ui.cs.advprog.inventorykatalog.usecase.CreateProductUseCase;
import id.ac.ui.cs.advprog.inventorykatalog.usecase.DeleteProductUseCase;
import id.ac.ui.cs.advprog.inventorykatalog.usecase.GetMyProductsUseCase;
import id.ac.ui.cs.advprog.inventorykatalog.usecase.UpdateProductUseCase;
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
import static org.mockito.ArgumentMatchers.anyDouble;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
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
    private CreateProductUseCase createProductUseCase;

    @Mock
    private UpdateProductUseCase updateProductUseCase;

    @Mock
    private DeleteProductUseCase deleteProductUseCase;

    @Mock
    private GetMyProductsUseCase getMyProductsUseCase;

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

        product1 = new Product();
        product1.setId("123-abc");
        product1.setNama("KitKat Matcha Jepang");
        product1.setDeskripsi("Cokelat asli dari Akihabara");
        product1.setHarga(55000.0);
        product1.setStok(20);
        product1.setRating(0.0);
        product1.setRatingCount(0);
        product1.setProductType(ProductType.REGULAR);
        product1.setMaxQuantityPerCheckout(0);
        product1.setNegaraAsal("Jepang");
        product1.setJastiperId(JASTIPER_ID);
    }

    @Test
    void testGetAllProducts() throws Exception {
        when(productService.findAll())
                .thenReturn(Arrays.asList(product1));

        mockMvc.perform(get("/api/products"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].nama").value("KitKat Matcha Jepang"))
                .andExpect(jsonPath("$[0].rating").value(0.0))
                .andExpect(jsonPath("$[0].ratingCount").value(0))
                .andExpect(jsonPath("$[0].productType").value("regular"))
                .andExpect(jsonPath("$[0].maxQuantityPerCheckout").value(0));
    }

    @Test
    void testCreateProduct() throws Exception {
        when(createProductUseCase.execute(anyString(), any(Product.class)))
                .thenReturn(product1);

        String jsonRequest = """
                {
                  "nama": "KitKat Matcha Jepang",
                  "deskripsi": "Cokelat asli dari Akihabara",
                  "harga": 55000.0,
                  "stok": 20,
                  "rating": 0.0,
                  "ratingCount": 0,
                  "productType": "regular",
                  "warStartTime": null,
                  "warEndTime": null,
                  "maxQuantityPerCheckout": 0,
                  "negaraAsal": "Jepang",
                  "imageUrls": []
                }
                """;

        mockMvc.perform(post("/api/products")
                        .header(HttpHeaders.AUTHORIZATION, AUTH_HEADER)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nama").value("KitKat Matcha Jepang"))
                .andExpect(jsonPath("$.rating").value(0.0))
                .andExpect(jsonPath("$.ratingCount").value(0))
                .andExpect(jsonPath("$.productType").value("regular"))
                .andExpect(jsonPath("$.maxQuantityPerCheckout").value(0));

        verify(createProductUseCase).execute(anyString(), any(Product.class));
    }

    @Test
    void testCreateProductForbiddenWhenNotJastiper() throws Exception {
        when(createProductUseCase.execute(anyString(), any(Product.class)))
                .thenThrow(new ResponseStatusException(
                        HttpStatus.FORBIDDEN,
                        "Only JASTIPER can manage products"
                ));

        String jsonRequest = """
                {
                  "nama": "Produk Titipers",
                  "harga": 10000.0,
                  "stok": 5,
                  "rating": 0.0,
                  "ratingCount": 0,
                  "productType": "regular",
                  "warStartTime": null,
                  "warEndTime": null,
                  "maxQuantityPerCheckout": 0,
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
        when(getMyProductsUseCase.execute(AUTH_HEADER))
                .thenReturn(Arrays.asList(product1));

        mockMvc.perform(get("/api/products/me")
                        .header(HttpHeaders.AUTHORIZATION, AUTH_HEADER))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].nama").value("KitKat Matcha Jepang"))
                .andExpect(jsonPath("$[0].rating").value(0.0))
                .andExpect(jsonPath("$[0].ratingCount").value(0))
                .andExpect(jsonPath("$[0].productType").value("regular"))
                .andExpect(jsonPath("$[0].maxQuantityPerCheckout").value(0));

        verify(getMyProductsUseCase).execute(AUTH_HEADER);
    }

    @Test
    void testGetProductById() throws Exception {
        when(productService.findById("123-abc"))
                .thenReturn(Optional.of(product1));

        mockMvc.perform(get("/api/products/123-abc"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nama").value("KitKat Matcha Jepang"))
                .andExpect(jsonPath("$.rating").value(0.0))
                .andExpect(jsonPath("$.ratingCount").value(0))
                .andExpect(jsonPath("$.productType").value("regular"))
                .andExpect(jsonPath("$.maxQuantityPerCheckout").value(0));
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
        when(updateProductUseCase.execute(anyString(), anyString(), any(Product.class)))
                .thenReturn(product1);

        String jsonRequest = """
                {
                  "nama": "KitKat Matcha Update",
                  "deskripsi": "Updated description",
                  "harga": 60000.0,
                  "stok": 30,
                  "rating": 0.0,
                  "ratingCount": 0,
                  "productType": "regular",
                  "warStartTime": null,
                  "warEndTime": null,
                  "maxQuantityPerCheckout": 0,
                  "negaraAsal": "Jepang",
                  "imageUrls": []
                }
                """;

        mockMvc.perform(put("/api/products/123-abc")
                        .header(HttpHeaders.AUTHORIZATION, AUTH_HEADER)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.rating").value(0.0))
                .andExpect(jsonPath("$.ratingCount").value(0))
                .andExpect(jsonPath("$.productType").value("regular"))
                .andExpect(jsonPath("$.maxQuantityPerCheckout").value(0));

        verify(updateProductUseCase)
                .execute(anyString(), anyString(), any(Product.class));
    }

    @Test
    void testUpdateProductNotFound() throws Exception {
        when(updateProductUseCase.execute(anyString(), anyString(), any(Product.class)))
                .thenThrow(new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Product not found"
                ));

        String jsonRequest = """
                {
                  "nama": "Gak Ada",
                  "harga": 0.0,
                  "stok": 0,
                  "rating": 0.0,
                  "ratingCount": 0,
                  "productType": "regular",
                  "warStartTime": null,
                  "warEndTime": null,
                  "maxQuantityPerCheckout": 0,
                  "imageUrls": []
                }
                """;

        mockMvc.perform(put("/api/products/999-xyz")
                        .header(HttpHeaders.AUTHORIZATION, AUTH_HEADER)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                .andExpect(status().isNotFound());

        verify(productService, never()).save(any(Product.class));
    }

    @Test
    void testUpdateProductForbiddenWhenNotOwner() throws Exception {
        when(updateProductUseCase.execute(anyString(), anyString(), any(Product.class)))
                .thenThrow(new ResponseStatusException(
                        HttpStatus.FORBIDDEN,
                        "Only ADMIN or the product owner can manage this product"
                ));

        String jsonRequest = """
                {
                  "nama": "KitKat Matcha Update",
                  "harga": 60000.0,
                  "stok": 30,
                  "rating": 0.0,
                  "ratingCount": 0,
                  "productType": "regular",
                  "warStartTime": null,
                  "warEndTime": null,
                  "maxQuantityPerCheckout": 0,
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
        product1.setImageUrls(Arrays.asList("http://img.com/new.jpg"));

        when(updateProductUseCase.execute(anyString(), anyString(), any(Product.class)))
                .thenReturn(product1);

        String jsonRequest = """
                {
                  "nama": "KitKat Update",
                  "harga": 60000.0,
                  "stok": 30,
                  "rating": 0.0,
                  "ratingCount": 0,
                  "productType": "regular",
                  "warStartTime": null,
                  "warEndTime": null,
                  "maxQuantityPerCheckout": 0,
                  "imageUrls": ["http://img.com/new.jpg"]
                }
                """;

        mockMvc.perform(put("/api/products/123-abc")
                        .header(HttpHeaders.AUTHORIZATION, AUTH_HEADER)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.imageUrls[0]").value("http://img.com/new.jpg"))
                .andExpect(jsonPath("$.rating").value(0.0))
                .andExpect(jsonPath("$.ratingCount").value(0))
                .andExpect(jsonPath("$.productType").value("regular"))
                .andExpect(jsonPath("$.maxQuantityPerCheckout").value(0));

        verify(updateProductUseCase)
                .execute(anyString(), anyString(), any(Product.class));
    }

    @Test
    void testDeleteProduct() throws Exception {
        mockMvc.perform(delete("/api/products/123-abc")
                        .header(HttpHeaders.AUTHORIZATION, AUTH_HEADER))
                .andExpect(status().isOk());

        verify(deleteProductUseCase, times(1))
                .execute("123-abc", AUTH_HEADER);
    }

    @Test
    void testDeleteProductNotFound() throws Exception {
        doThrow(new ResponseStatusException(
                HttpStatus.NOT_FOUND,
                "Product not found"
        )).when(deleteProductUseCase).execute("id-ngasal", AUTH_HEADER);

        mockMvc.perform(delete("/api/products/id-ngasal")
                        .header(HttpHeaders.AUTHORIZATION, AUTH_HEADER))
                .andExpect(status().isNotFound());

        verify(productService, never()).deleteById(anyString());
    }

    @Test
    void testDeleteProductForbiddenWhenNotOwner() throws Exception {
        doThrow(new ResponseStatusException(
                HttpStatus.FORBIDDEN,
                "Only ADMIN or the product owner can manage this product"
        )).when(deleteProductUseCase).execute("123-abc", AUTH_HEADER);

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
                .andExpect(jsonPath("$[0].nama").value("KitKat Matcha Jepang"))
                .andExpect(jsonPath("$[0].rating").value(0.0))
                .andExpect(jsonPath("$[0].ratingCount").value(0))
                .andExpect(jsonPath("$[0].productType").value("regular"))
                .andExpect(jsonPath("$[0].maxQuantityPerCheckout").value(0));
    }

    @Test
    void testGetByJastiper() throws Exception {
        when(productService.findByJastiperId(JASTIPER_ID))
                .thenReturn(Arrays.asList(product1));

        mockMvc.perform(get("/api/products/search/jastiper")
                        .param("jastiperId", JASTIPER_ID))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].nama").value("KitKat Matcha Jepang"))
                .andExpect(jsonPath("$[0].rating").value(0.0))
                .andExpect(jsonPath("$[0].ratingCount").value(0))
                .andExpect(jsonPath("$[0].productType").value("regular"))
                .andExpect(jsonPath("$[0].maxQuantityPerCheckout").value(0));
    }

    @Test
    void testReduceStock() throws Exception {
        product1.setStok(19);

        when(productService.reduceStock("123-abc", 1))
                .thenReturn(product1);

        mockMvc.perform(patch("/api/products/123-abc/stock/reduce")
                        .param("quantity", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.stok").value(19));

        verify(productService).reduceStock("123-abc", 1);
    }

    @Test
    void testAddProductRating() throws Exception {
        product1.setRating(4.5);
        product1.setRatingCount(1);

        when(productService.addRating(anyString(), anyDouble()))
                .thenReturn(product1);

        String jsonRequest = """
                {
                  "rating": 4.5
                }
                """;

        mockMvc.perform(patch("/api/products/123-abc/rating")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.rating").value(4.5))
                .andExpect(jsonPath("$.ratingCount").value(1));

        verify(productService).addRating("123-abc", 4.5);
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
