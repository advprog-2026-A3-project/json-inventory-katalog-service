package id.ac.ui.cs.advprog.inventorykatalog.usecase;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import id.ac.ui.cs.advprog.inventorykatalog.client.AuthClient;
import id.ac.ui.cs.advprog.inventorykatalog.model.Product;
import id.ac.ui.cs.advprog.inventorykatalog.service.ProductService;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

@ExtendWith(MockitoExtension.class)
class GetMyProductsUseCaseTest {

    @Mock
    private ProductService productService;

    @Mock
    private AuthClient authClient;

    private GetMyProductsUseCase useCase;

    @BeforeEach
    void setUp() {
        useCase = new GetMyProductsUseCase(productService, authClient);
    }

    @Test
    void executeShouldReturnProductsOwnedByCurrentJastiper() {
        List<Product> products = List.of(new Product());
        when(authClient.getCurrentJastiperId("Bearer token")).thenReturn("jastiper-1");
        when(productService.findByJastiperId("jastiper-1")).thenReturn(products);

        assertSame(products, useCase.execute("Bearer token"));
    }

    @Test
    void executeShouldPropagateAuthError() {
        ResponseStatusException authError = new ResponseStatusException(HttpStatus.FORBIDDEN, "not jastiper");
        when(authClient.getCurrentJastiperId("Bearer token")).thenThrow(authError);

        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> useCase.execute("Bearer token")
        );

        assertSame(authError, exception);
        verifyNoInteractions(productService);
    }
}
