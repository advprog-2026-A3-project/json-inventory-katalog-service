package id.ac.ui.cs.advprog.inventorykatalog.authorization;

import id.ac.ui.cs.advprog.inventorykatalog.client.AuthProfileResponse;
import id.ac.ui.cs.advprog.inventorykatalog.model.Product;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.util.Objects;

public final class ProductAuthorizationPolicy {

    private static final ProductAuthorizationPolicy INSTANCE = new ProductAuthorizationPolicy();
    private static final String JASTIPER_ROLE = "JASTIPER";
    private static final String ADMIN_ROLE = "ADMIN";

    private ProductAuthorizationPolicy() {
    }

    public static ProductAuthorizationPolicy getInstance() {
        return INSTANCE;
    }

    public String resolveJastiperId(AuthProfileResponse profile) {
        validateAuthenticatedProfile(profile);
        validateActiveAccount(profile);
        validateJastiperRole(profile);

        return profile.getId().toString();
    }

    public void validateCanManageProduct(AuthProfileResponse profile, Product product) {
        validateAuthenticatedProfile(profile);
        validateActiveAccount(profile);
        validateExistingProduct(product);

        if (hasRole(profile, ADMIN_ROLE)) {
            return;
        }

        if (hasRole(profile, JASTIPER_ROLE)
                && Objects.equals(profile.getId().toString(), product.getJastiperId())) {
            return;
        }

        throw new ResponseStatusException(
                HttpStatus.FORBIDDEN,
                "Only ADMIN or the product owner can manage this product"
        );
    }

    public void validateOwner(String currentJastiperId, Product product) {
        validateExistingProduct(product);

        if (!Objects.equals(currentJastiperId, product.getJastiperId())) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "Only the product owner can manage this product"
            );
        }
    }

    private void validateAuthenticatedProfile(AuthProfileResponse profile) {
        if (profile == null || profile.getId() == null) {
            throw new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED,
                    "Cannot extract user profile from auth service"
            );
        }
    }

    private void validateActiveAccount(AuthProfileResponse profile) {
        if (!profile.isActive()) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "User account is not active"
            );
        }
    }

    private void validateJastiperRole(AuthProfileResponse profile) {
        if (!hasRole(profile, JASTIPER_ROLE)) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "Only JASTIPER can manage products"
            );
        }
    }

    private void validateExistingProduct(Product product) {
        if (product == null) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "Product not found"
            );
        }
    }

    private boolean hasRole(AuthProfileResponse profile, String expectedRole) {
        return expectedRole.equalsIgnoreCase(profile.getRole());
    }
}