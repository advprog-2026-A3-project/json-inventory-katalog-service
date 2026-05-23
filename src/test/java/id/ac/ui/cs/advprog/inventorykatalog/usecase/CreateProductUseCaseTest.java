package id.ac.ui.cs.advprog.inventorykatalog.usecase;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import id.ac.ui.cs.advprog.inventorykatalog.client.AuthClient;
import id.ac.ui.cs.advprog.inventorykatalog.model.Product;
import id.ac.ui.cs.advprog.inventorykatalog.service.ProductService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

@ExtendWith(MockitoExtension.class)
class CreateProductUseCaseTest {

    @Mock
    private ProductService productService;

    @Mock
    private AuthClient authClient;

    private CreateProductUseCase useCase;

    @BeforeEach
    void setUp() {
        useCase = new CreateProductUseCase(productService, authClient);
    }

    @Test
    void executeShouldSetCurrentJastiperIdAndSaveProduct() {
        Product product = new Product();
        when(authClient.getCurrentJastiperId("Bearer token")).thenReturn("jastiper-1");
        when(productService.save(product)).thenReturn(product);

        Product result = useCase.execute("Bearer token", product);

        assertSame(product, result);
        assertEquals("jastiper-1", product.getJastiperId());
        verify(productService).save(product);
    }

    @Test
    void executeShouldIgnoreClientControlledFields() {
        Product product = new Product();
        product.setJastiperId("attacker");
        product.setRating(5.0);
        product.setRatingCount(99);
        when(authClient.getCurrentJastiperId("Bearer token")).thenReturn("jastiper-1");
        when(productService.save(product)).thenReturn(product);

        Product result = useCase.execute("Bearer token", product);

        assertSame(product, result);
        assertEquals("jastiper-1", product.getJastiperId());
        assertEquals(0.0, product.getRating());
        assertEquals(0, product.getRatingCount());
        verify(productService).save(product);
    }

    @Test
    void executeShouldPropagateAuthErrorAndNotSaveProduct() {
        Product product = new Product();
        ResponseStatusException authError = new ResponseStatusException(HttpStatus.UNAUTHORIZED, "invalid token");
        when(authClient.getCurrentJastiperId("Bearer bad-token")).thenThrow(authError);

        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> useCase.execute("Bearer bad-token", product)
        );

        assertSame(authError, exception);
        verifyNoInteractions(productService);
    }
}
