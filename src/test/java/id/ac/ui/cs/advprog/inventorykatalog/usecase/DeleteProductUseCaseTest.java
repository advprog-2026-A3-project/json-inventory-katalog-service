package id.ac.ui.cs.advprog.inventorykatalog.usecase;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import id.ac.ui.cs.advprog.inventorykatalog.client.AuthClient;
import id.ac.ui.cs.advprog.inventorykatalog.client.AuthProfileResponse;
import id.ac.ui.cs.advprog.inventorykatalog.model.Product;
import id.ac.ui.cs.advprog.inventorykatalog.service.ProductService;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

@ExtendWith(MockitoExtension.class)
class DeleteProductUseCaseTest {

    @Mock
    private ProductService productService;

    @Mock
    private AuthClient authClient;

    private DeleteProductUseCase useCase;

    @BeforeEach
    void setUp() {
        useCase = new DeleteProductUseCase(productService, authClient);
    }

    @Test
    void executeShouldThrowNotFoundWhenProductDoesNotExist() {
        when(productService.findById("p1")).thenReturn(Optional.empty());

        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> useCase.execute("p1", "Bearer token")
        );

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
        verify(authClient, never()).getCurrentUserProfile(any());
    }

    @Test
    void executeShouldDeleteWhenUserIsAdmin() {
        Product product = productOwnedBy("owner-1");
        when(productService.findById("p1")).thenReturn(Optional.of(product));
        when(authClient.getCurrentUserProfile("Bearer admin")).thenReturn(profile(99L, "ADMIN", true));

        useCase.execute("p1", "Bearer admin");

        verify(productService).deleteById("p1");
    }

    @Test
    void executeShouldDeleteWhenUserIsOwner() {
        Product product = productOwnedBy("10");
        when(productService.findById("p1")).thenReturn(Optional.of(product));
        when(authClient.getCurrentUserProfile("Bearer owner")).thenReturn(profile(10L, "JASTIPER", true));

        useCase.execute("p1", "Bearer owner");

        verify(productService).deleteById("p1");
    }

    @Test
    void executeShouldRejectNonOwnerJastiper() {
        Product product = productOwnedBy("10");
        when(productService.findById("p1")).thenReturn(Optional.of(product));
        when(authClient.getCurrentUserProfile("Bearer other")).thenReturn(profile(11L, "JASTIPER", true));

        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> useCase.execute("p1", "Bearer other")
        );

        assertEquals(HttpStatus.FORBIDDEN, exception.getStatusCode());
        verify(productService, never()).deleteById("p1");
    }

    @Test
    void executeShouldRejectInactiveUser() {
        Product product = productOwnedBy("10");
        when(productService.findById("p1")).thenReturn(Optional.of(product));
        when(authClient.getCurrentUserProfile("Bearer inactive")).thenReturn(profile(10L, "JASTIPER", false));

        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> useCase.execute("p1", "Bearer inactive")
        );

        assertEquals(HttpStatus.FORBIDDEN, exception.getStatusCode());
        verify(productService, never()).deleteById("p1");
    }

    private Product productOwnedBy(String ownerId) {
        Product product = new Product();
        product.setId("p1");
        product.setJastiperId(ownerId);
        return product;
    }

    private AuthProfileResponse profile(Long id, String role, boolean active) {
        AuthProfileResponse profile = new AuthProfileResponse();
        profile.setId(id);
        profile.setRole(role);
        profile.setActive(active);
        return profile;
    }
}
