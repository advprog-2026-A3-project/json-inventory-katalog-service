package id.ac.ui.cs.advprog.inventorykatalog.authorization;

import id.ac.ui.cs.advprog.inventorykatalog.client.AuthProfileResponse;
import id.ac.ui.cs.advprog.inventorykatalog.model.Product;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ProductAuthorizationPolicyTest {

    private final ProductAuthorizationPolicy policy =
            ProductAuthorizationPolicy.getInstance();

    @Test
    void testSingletonReturnsSameInstance() {
        ProductAuthorizationPolicy firstInstance =
                ProductAuthorizationPolicy.getInstance();

        ProductAuthorizationPolicy secondInstance =
                ProductAuthorizationPolicy.getInstance();

        assertSame(firstInstance, secondInstance);
    }

    @Test
    void testResolveJastiperIdSuccess() {
        AuthProfileResponse profile = createProfile(4L, "JASTIPER", true);

        String jastiperId = policy.resolveJastiperId(profile);

        assertEquals("4", jastiperId);
    }

    @Test
    void testResolveJastiperIdRejectsTitipers() {
        AuthProfileResponse profile = createProfile(4L, "TITIPERS", true);

        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> policy.resolveJastiperId(profile)
        );

        assertEquals(HttpStatus.FORBIDDEN, exception.getStatusCode());
    }

    @Test
    void testResolveJastiperIdRejectsInactiveUser() {
        AuthProfileResponse profile = createProfile(4L, "JASTIPER", false);

        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> policy.resolveJastiperId(profile)
        );

        assertEquals(HttpStatus.FORBIDDEN, exception.getStatusCode());
    }

    @Test
    void testResolveJastiperIdRejectsMissingProfile() {
        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> policy.resolveJastiperId(null)
        );

        assertEquals(HttpStatus.UNAUTHORIZED, exception.getStatusCode());
    }

    @Test
    void testValidateOwnerSuccess() {
        Product product = new Product();
        product.setJastiperId("4");

        assertDoesNotThrow(() -> policy.validateOwner("4", product));
    }

    @Test
    void testValidateOwnerRejectsDifferentOwner() {
        Product product = new Product();
        product.setJastiperId("5");

        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> policy.validateOwner("4", product)
        );

        assertEquals(HttpStatus.FORBIDDEN, exception.getStatusCode());
    }

    private AuthProfileResponse createProfile(Long id, String role, boolean active) {
        AuthProfileResponse profile = new AuthProfileResponse();
        profile.setId(id);
        profile.setRole(role);
        profile.setActive(active);
        return profile;
    }
}