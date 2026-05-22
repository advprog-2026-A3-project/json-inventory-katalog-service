package id.ac.ui.cs.advprog.inventorykatalog.usecase;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import id.ac.ui.cs.advprog.inventorykatalog.client.AuthClient;
import id.ac.ui.cs.advprog.inventorykatalog.client.AuthProfileResponse;
import id.ac.ui.cs.advprog.inventorykatalog.model.Product;
import id.ac.ui.cs.advprog.inventorykatalog.model.ProductType;
import id.ac.ui.cs.advprog.inventorykatalog.service.ProductService;
import java.time.LocalDate;
import java.time.LocalDateTime;
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
class UpdateProductUseCaseTest {

    @Mock
    private ProductService productService;

    @Mock
    private AuthClient authClient;

    private UpdateProductUseCase useCase;

    @BeforeEach
    void setUp() {
        useCase = new UpdateProductUseCase(productService, authClient);
    }

    @Test
    void executeShouldThrowNotFoundWhenProductDoesNotExist() {
        when(productService.findById("p1")).thenReturn(Optional.empty());

        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> useCase.execute("p1", "Bearer token", new Product())
        );

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
        verify(authClient, never()).getCurrentUserProfile(any());
    }

    @Test
    void executeShouldUpdateProductWhenUserIsAdmin() {
        Product existingProduct = existingProduct();
        Product details = updateDetails();
        when(productService.findById("p1")).thenReturn(Optional.of(existingProduct));
        when(authClient.getCurrentUserProfile("Bearer admin")).thenReturn(profile(9L, "ADMIN", true));
        when(productService.save(existingProduct)).thenReturn(existingProduct);

        Product result = useCase.execute("p1", "Bearer admin", details);

        assertSame(existingProduct, result);
        assertUpdatedFields(existingProduct);
        assertEquals("1", existingProduct.getJastiperId());
        assertEquals("p1", existingProduct.getId());
        verify(productService).save(existingProduct);
    }

    @Test
    void executeShouldUpdateProductWhenUserIsProductOwner() {
        Product existingProduct = existingProduct();
        Product details = updateDetails();
        when(productService.findById("p1")).thenReturn(Optional.of(existingProduct));
        when(authClient.getCurrentUserProfile("Bearer owner")).thenReturn(profile(1L, "JASTIPER", true));
        when(productService.save(existingProduct)).thenReturn(existingProduct);

        Product result = useCase.execute("p1", "Bearer owner", details);

        assertSame(existingProduct, result);
        assertUpdatedFields(existingProduct);
    }

    @Test
    void executeShouldRejectNonOwnerJastiper() {
        Product existingProduct = existingProduct();
        when(productService.findById("p1")).thenReturn(Optional.of(existingProduct));
        when(authClient.getCurrentUserProfile("Bearer other")).thenReturn(profile(2L, "JASTIPER", true));

        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> useCase.execute("p1", "Bearer other", updateDetails())
        );

        assertEquals(HttpStatus.FORBIDDEN, exception.getStatusCode());
        verify(productService, never()).save(any());
    }

    @Test
    void executeShouldRejectInactiveUser() {
        Product existingProduct = existingProduct();
        when(productService.findById("p1")).thenReturn(Optional.of(existingProduct));
        when(authClient.getCurrentUserProfile("Bearer inactive")).thenReturn(profile(1L, "JASTIPER", false));

        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> useCase.execute("p1", "Bearer inactive", updateDetails())
        );

        assertEquals(HttpStatus.FORBIDDEN, exception.getStatusCode());
        verify(productService, never()).save(any());
    }

    private Product existingProduct() {
        Product product = new Product();
        product.setId("p1");
        product.setJastiperId("1");
        product.setNama("Old name");
        return product;
    }

    private Product updateDetails() {
        LocalDateTime start = LocalDateTime.of(2026, 5, 22, 10, 0);
        Product product = new Product();
        product.setNama("New name");
        product.setDeskripsi("New description");
        product.setHarga(15000.0);
        product.setStok(12);
        product.setNegaraAsal("Jepang");
        product.setTanggalPembelian(LocalDate.of(2026, 5, 10));
        product.setTanggalKembali(LocalDate.of(2026, 5, 15));
        product.setImageUrls(List.of("img-1", "img-2"));
        product.setProductType(ProductType.LIMITED);
        product.setWarStartTime(start);
        product.setWarEndTime(start.plusHours(1));
        product.setMaxQuantityPerCheckout(2);
        product.setJastiperId("attacker");
        product.setRating(5.0);
        product.setRatingCount(99);
        return product;
    }

    private void assertUpdatedFields(Product product) {
        assertEquals("New name", product.getNama());
        assertEquals("New description", product.getDeskripsi());
        assertEquals(15000.0, product.getHarga());
        assertEquals(12, product.getStok());
        assertEquals("Jepang", product.getNegaraAsal());
        assertEquals(LocalDate.of(2026, 5, 10), product.getTanggalPembelian());
        assertEquals(LocalDate.of(2026, 5, 15), product.getTanggalKembali());
        assertEquals(List.of("img-1", "img-2"), product.getImageUrls());
        assertEquals(ProductType.LIMITED, product.getProductType());
        assertEquals(LocalDateTime.of(2026, 5, 22, 10, 0), product.getWarStartTime());
        assertEquals(LocalDateTime.of(2026, 5, 22, 11, 0), product.getWarEndTime());
        assertEquals(2, product.getMaxQuantityPerCheckout());
    }

    private AuthProfileResponse profile(Long id, String role, boolean active) {
        AuthProfileResponse profile = new AuthProfileResponse();
        profile.setId(id);
        profile.setRole(role);
        profile.setActive(active);
        return profile;
    }
}
