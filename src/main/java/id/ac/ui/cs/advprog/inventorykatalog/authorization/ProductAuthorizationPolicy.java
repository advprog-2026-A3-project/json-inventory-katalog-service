package id.ac.ui.cs.advprog.inventorykatalog.authorization;

import id.ac.ui.cs.advprog.inventorykatalog.client.AuthProfileResponse;
import id.ac.ui.cs.advprog.inventorykatalog.model.Product;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.util.Objects;

public final class ProductAuthorizationPolicy {

    private static final ProductAuthorizationPolicy INSTANCE = new ProductAuthorizationPolicy();
    private static final String JASTIPER_ROLE = "JASTIPER";

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

    public void validateOwner(String currentJastiperId, Product product) {
        if (product == null) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "Product not found"
            );
        }

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
        if (!JASTIPER_ROLE.equals(profile.getRole())) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "Only JASTIPER can manage products"
            );
        }
    }
}