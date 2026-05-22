package id.ac.ui.cs.advprog.inventorykatalog.authorization;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import id.ac.ui.cs.advprog.inventorykatalog.client.AuthProfileResponse;
import id.ac.ui.cs.advprog.inventorykatalog.model.Product;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

class ProductAuthorizationPolicyTest {

    private final ProductAuthorizationPolicy policy = ProductAuthorizationPolicy.getInstance();

    @Test
    void resolveJastiperIdShouldReturnIdForActiveJastiper() {
        assertEquals("42", policy.resolveJastiperId(profile(42L, "jastiper", true)));
    }

    @Test
    void resolveJastiperIdShouldRejectNullProfile() {
        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> policy.resolveJastiperId(null)
        );

        assertEquals(HttpStatus.UNAUTHORIZED, exception.getStatusCode());
    }

    @Test
    void resolveJastiperIdShouldRejectProfileWithoutId() {
        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> policy.resolveJastiperId(profile(null, "JASTIPER", true))
        );

        assertEquals(HttpStatus.UNAUTHORIZED, exception.getStatusCode());
    }

    @Test
    void resolveJastiperIdShouldRejectInactiveAccount() {
        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> policy.resolveJastiperId(profile(1L, "JASTIPER", false))
        );

        assertEquals(HttpStatus.FORBIDDEN, exception.getStatusCode());
    }

    @Test
    void resolveJastiperIdShouldRejectNonJastiperRole() {
        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> policy.resolveJastiperId(profile(1L, "BUYER", true))
        );

        assertEquals(HttpStatus.FORBIDDEN, exception.getStatusCode());
    }

    @Test
    void validateCanManageProductShouldAllowAdmin() {
        Product product = productOwnedBy("jastiper-1");

        policy.validateCanManageProduct(profile(99L, "ADMIN", true), product);
    }

    @Test
    void validateCanManageProductShouldAllowProductOwner() {
        Product product = productOwnedBy("7");

        policy.validateCanManageProduct(profile(7L, "JASTIPER", true), product);
    }

    @Test
    void validateCanManageProductShouldRejectNonOwnerJastiper() {
        Product product = productOwnedBy("7");

        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> policy.validateCanManageProduct(profile(8L, "JASTIPER", true), product)
        );

        assertEquals(HttpStatus.FORBIDDEN, exception.getStatusCode());
    }

    @Test
    void validateCanManageProductShouldRejectInactiveAdmin() {
        Product product = productOwnedBy("7");

        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> policy.validateCanManageProduct(profile(1L, "ADMIN", false), product)
        );

        assertEquals(HttpStatus.FORBIDDEN, exception.getStatusCode());
    }

    @Test
    void validateCanManageProductShouldRejectMissingProduct() {
        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> policy.validateCanManageProduct(profile(1L, "ADMIN", true), null)
        );

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
    }

    @Test
    void validateOwnerShouldAllowMatchingOwner() {
        policy.validateOwner("owner-1", productOwnedBy("owner-1"));
    }

    @Test
    void validateOwnerShouldRejectDifferentOwner() {
        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> policy.validateOwner("owner-2", productOwnedBy("owner-1"))
        );

        assertEquals(HttpStatus.FORBIDDEN, exception.getStatusCode());
    }

    @Test
    void validateOwnerShouldRejectMissingProduct() {
        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> policy.validateOwner("owner-1", null)
        );

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
    }

    private AuthProfileResponse profile(Long id, String role, boolean active) {
        AuthProfileResponse profile = new AuthProfileResponse();
        profile.setId(id);
        profile.setRole(role);
        profile.setActive(active);
        return profile;
    }

    private Product productOwnedBy(String jastiperId) {
        Product product = new Product();
        product.setJastiperId(jastiperId);
        return product;
    }
}
