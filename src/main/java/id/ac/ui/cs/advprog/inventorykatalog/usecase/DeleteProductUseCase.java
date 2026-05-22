package id.ac.ui.cs.advprog.inventorykatalog.usecase;

import id.ac.ui.cs.advprog.inventorykatalog.authorization.ProductAuthorizationPolicy;
import id.ac.ui.cs.advprog.inventorykatalog.client.AuthClient;
import id.ac.ui.cs.advprog.inventorykatalog.client.AuthProfileResponse;
import id.ac.ui.cs.advprog.inventorykatalog.model.Product;
import id.ac.ui.cs.advprog.inventorykatalog.service.ProductService;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class DeleteProductUseCase {

    private final ProductService productService;
    private final AuthClient authClient;
    private final ProductAuthorizationPolicy authorizationPolicy =
            ProductAuthorizationPolicy.getInstance();

    public DeleteProductUseCase(ProductService productService, AuthClient authClient) {
        this.productService = productService;
        this.authClient = authClient;
    }

    public void execute(String id, String authorizationHeader) {
        Product product = productService.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Product not found"
                ));

        AuthProfileResponse currentUser = authClient.getCurrentUserProfile(authorizationHeader);
        authorizationPolicy.validateCanManageProduct(currentUser, product);

        productService.deleteById(id);
    }
}