package id.ac.ui.cs.advprog.inventorykatalog.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import id.ac.ui.cs.advprog.inventorykatalog.dto.ProductRatingRequest;
import id.ac.ui.cs.advprog.inventorykatalog.model.Product;
import id.ac.ui.cs.advprog.inventorykatalog.service.ProductService;
import id.ac.ui.cs.advprog.inventorykatalog.usecase.CreateProductUseCase;
import id.ac.ui.cs.advprog.inventorykatalog.usecase.DeleteProductUseCase;
import id.ac.ui.cs.advprog.inventorykatalog.usecase.GetMyProductsUseCase;
import id.ac.ui.cs.advprog.inventorykatalog.usecase.UpdateProductUseCase;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import static org.mockito.ArgumentMatchers.eq;
import id.ac.ui.cs.advprog.inventorykatalog.dto.ProductRequest;

@ExtendWith(MockitoExtension.class)
class ProductControllerTest {

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

    private ProductController controller;

    @BeforeEach
    void setUp() {
        controller = new ProductController(
                productService,
                createProductUseCase,
                updateProductUseCase,
                deleteProductUseCase,
                getMyProductsUseCase
        );
    }

    @Test
    void initDummyDataShouldSeedProductWhenRepositoryIsEmpty() {
        when(productService.count()).thenReturn(0L);
        ArgumentCaptor<Product> captor = ArgumentCaptor.forClass(Product.class);

        controller.initDummyData();

        verify(productService).save(captor.capture());
        assertEquals("KitKat Matcha Jepang", captor.getValue().getNama());
        assertEquals("jastiper-001", captor.getValue().getJastiperId());
    }

    @Test
    void initDummyDataShouldNotSeedProductWhenRepositoryHasData() {
        when(productService.count()).thenReturn(1L);

        controller.initDummyData();

        verify(productService, never()).save(any());
    }

    @Test
    void getAllProductsShouldReturnProducts() {
        List<Product> products = List.of(new Product());
        when(productService.findAll()).thenReturn(products);

        ResponseEntity<?> response = controller.getAllProducts();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertSame(products, response.getBody());
    }

    @Test
    void createProductShouldUseCreateUseCase() {
        ProductRequest request = new ProductRequest();
        request.setNama("KitKat");
        request.setHarga(10000);
        request.setStok(5);

        Product saved = new Product();
        when(createProductUseCase.execute(eq("Bearer token"), any(Product.class))).thenReturn(saved);

        ResponseEntity<?> response = controller.createProduct("Bearer token", request);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertSame(saved, response.getBody());

        ArgumentCaptor<Product> captor = ArgumentCaptor.forClass(Product.class);
        verify(createProductUseCase).execute(eq("Bearer token"), captor.capture());
        assertEquals("KitKat", captor.getValue().getNama());
        assertEquals(10000, captor.getValue().getHarga());
        assertEquals(5, captor.getValue().getStok());
    }

    @Test
    void getMyProductsShouldUseGetMyProductsUseCase() {
        List<Product> products = List.of(new Product());
        when(getMyProductsUseCase.execute("Bearer token")).thenReturn(products);

        ResponseEntity<?> response = controller.getMyProducts("Bearer token");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertSame(products, response.getBody());
    }

    @Test
    void updateProductShouldUseUpdateUseCase() {
        ProductRequest request = new ProductRequest();
        request.setNama("Updated Product");
        request.setHarga(15000);
        request.setStok(10);

        Product updated = new Product();
        when(updateProductUseCase.execute(eq("p1"), eq("Bearer token"), any(Product.class))).thenReturn(updated);

        ResponseEntity<?> response = controller.updateProduct("p1", "Bearer token", request.toProduct());

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertSame(updated, response.getBody());

        ArgumentCaptor<Product> captor = ArgumentCaptor.forClass(Product.class);
        verify(updateProductUseCase).execute(eq("p1"), eq("Bearer token"), captor.capture());
        assertEquals("Updated Product", captor.getValue().getNama());
        assertEquals(15000, captor.getValue().getHarga());
        assertEquals(10, captor.getValue().getStok());
    }

    @Test
    void deleteProductShouldUseDeleteUseCase() {
        ResponseEntity<?> response = controller.deleteProduct("p1", "Bearer token");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(deleteProductUseCase).execute("p1", "Bearer token");
    }

    @Test
    void getProductByIdShouldReturnProductWhenFound() {
        Product product = new Product();
        when(productService.findById("p1")).thenReturn(Optional.of(product));

        ResponseEntity<?> response = controller.getProductById("p1");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertSame(product, response.getBody());
    }

    @Test
    void getProductByIdShouldReturnNotFoundWhenMissing() {
        when(productService.findById("p1")).thenReturn(Optional.empty());

        ResponseEntity<?> response = controller.getProductById("p1");

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    void searchByNameShouldReturnMatchingProducts() {
        List<Product> products = List.of(new Product());
        when(productService.findByNamaContainingIgnoreCase("kitkat")).thenReturn(products);

        ResponseEntity<?> response = controller.searchByName("kitkat");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertSame(products, response.getBody());
    }

    @Test
    void getByJastiperShouldReturnMatchingProducts() {
        List<Product> products = List.of(new Product());
        when(productService.findByJastiperId("jastiper-1")).thenReturn(products);

        ResponseEntity<?> response = controller.getByJastiper("jastiper-1");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertSame(products, response.getBody());
    }

    @Test
    void reduceStockShouldReturnUpdatedProduct() {
        Product updated = new Product();
        when(productService.reduceStock("p1", 2)).thenReturn(updated);

        ResponseEntity<?> response = controller.reduceStock("p1", 2);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertSame(updated, response.getBody());
    }

    @Test
    void addProductRatingShouldReturnUpdatedProduct() {
        ProductRatingRequest request = new ProductRatingRequest();
        request.setRating(4.5);
        Product updated = new Product();
        when(productService.addRating("p1", 4.5)).thenReturn(updated);

        ResponseEntity<?> response = controller.addProductRating("p1", request);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertSame(updated, response.getBody());
    }
}
