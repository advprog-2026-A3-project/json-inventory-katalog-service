package id.ac.ui.cs.advprog.inventorykatalog.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import id.ac.ui.cs.advprog.inventorykatalog.model.Product;
import id.ac.ui.cs.advprog.inventorykatalog.repository.ProductRepository;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    private ProductServiceImpl productService;

    @BeforeEach
    void setUp() {
        productService = new ProductServiceImpl(productRepository);
    }

    @Test
    void saveShouldDelegateToRepository() {
        Product product = product("p1", 10, 4.0, 2);
        when(productRepository.save(product)).thenReturn(product);

        assertSame(product, productService.save(product));
    }

    @Test
    void findByIdShouldDelegateToRepository() {
        Product product = product("p1", 10, 4.0, 2);
        when(productRepository.findById("p1")).thenReturn(Optional.of(product));

        assertEquals(Optional.of(product), productService.findById("p1"));
    }

    @Test
    void findAllShouldDelegateToRepository() {
        List<Product> products = List.of(product("p1", 10, 4.0, 2));
        when(productRepository.findAll()).thenReturn(products);

        assertSame(products, productService.findAll());
    }

    @Test
    void deleteByIdShouldDelegateToRepository() {
        productService.deleteById("p1");

        verify(productRepository).deleteById("p1");
    }

    @Test
    void existsByIdShouldDelegateToRepository() {
        when(productRepository.existsById("p1")).thenReturn(true);

        assertEquals(true, productService.existsById("p1"));
    }

    @Test
    void findByNamaContainingIgnoreCaseShouldDelegateToRepository() {
        List<Product> products = List.of(product("p1", 10, 4.0, 2));
        when(productRepository.findByNamaContainingIgnoreCase("kitkat")).thenReturn(products);

        assertSame(products, productService.findByNamaContainingIgnoreCase("kitkat"));
    }

    @Test
    void findByJastiperIdShouldDelegateToRepository() {
        List<Product> products = List.of(product("p1", 10, 4.0, 2));
        when(productRepository.findByJastiperId("jastiper-1")).thenReturn(products);

        assertSame(products, productService.findByJastiperId("jastiper-1"));
    }

    @Test
    void countShouldDelegateToRepository() {
        when(productRepository.count()).thenReturn(3L);

        assertEquals(3L, productService.count());
    }

    @Test
    void deleteAllShouldDelegateToRepository() {
        productService.deleteAll();

        verify(productRepository).deleteAll();
    }

    @Test
    void reduceStockShouldRejectQuantityLessThanOne() {
        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> productService.reduceStock("p1", 0)
        );

        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode());
        verify(productRepository, never()).reduceStockIfAvailable("p1", 0);
    }

    @Test
    void reduceStockShouldThrowNotFoundWhenProductDoesNotExist() {
        when(productRepository.reduceStockIfAvailable("p1", 2)).thenReturn(0);
        when(productRepository.existsById("p1")).thenReturn(false);

        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> productService.reduceStock("p1", 2)
        );

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
    }

    @Test
    void reduceStockShouldThrowBadRequestWhenStockIsInsufficient() {
        when(productRepository.reduceStockIfAvailable("p1", 20)).thenReturn(0);
        when(productRepository.existsById("p1")).thenReturn(true);

        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> productService.reduceStock("p1", 20)
        );

        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode());
    }

    @Test
    void reduceStockShouldThrowNotFoundWhenUpdatedProductCannotBeLoaded() {
        when(productRepository.reduceStockIfAvailable("p1", 2)).thenReturn(1);
        when(productRepository.findById("p1")).thenReturn(Optional.empty());

        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> productService.reduceStock("p1", 2)
        );

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
    }

    @Test
    void reduceStockShouldReturnUpdatedProduct() {
        Product updatedProduct = product("p1", 8, 4.0, 2);
        when(productRepository.reduceStockIfAvailable("p1", 2)).thenReturn(1);
        when(productRepository.findById("p1")).thenReturn(Optional.of(updatedProduct));

        assertSame(updatedProduct, productService.reduceStock("p1", 2));
    }

    @Test
    void addRatingShouldRejectRatingBelowZero() {
        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> productService.addRating("p1", -0.1)
        );

        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode());
        verify(productRepository, never()).findById("p1");
    }

    @Test
    void addRatingShouldRejectRatingAboveFive() {
        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> productService.addRating("p1", 5.1)
        );

        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode());
    }

    @Test
    void addRatingShouldThrowNotFoundForMissingProduct() {
        when(productRepository.findById("p1")).thenReturn(Optional.empty());

        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> productService.addRating("p1", 4.0)
        );

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
    }

    @Test
    void addRatingShouldInitializeNullRating() {
        Product product = product("p1", 10, null, 0);
        when(productRepository.findById("p1")).thenReturn(Optional.of(product));
        when(productRepository.save(product)).thenReturn(product);

        Product result = productService.addRating("p1", 5.0);

        assertSame(product, result);
        assertEquals(5.0, product.getRating());
        assertEquals(1, product.getRatingCount());
    }

    @Test
    void addRatingShouldCalculateAverageRating() {
        Product product = product("p1", 10, 4.0, 2);
        when(productRepository.findById("p1")).thenReturn(Optional.of(product));
        when(productRepository.save(product)).thenReturn(product);

        Product result = productService.addRating("p1", 5.0);

        assertSame(product, result);
        assertEquals(13.0 / 3.0, product.getRating());
        assertEquals(3, product.getRatingCount());
    }

    private Product product(String id, int stock, Double rating, int ratingCount) {
        Product product = new Product();
        product.setId(id);
        product.setStok(stock);
        product.setRating(rating);
        product.setRatingCount(ratingCount);
        return product;
    }
}
