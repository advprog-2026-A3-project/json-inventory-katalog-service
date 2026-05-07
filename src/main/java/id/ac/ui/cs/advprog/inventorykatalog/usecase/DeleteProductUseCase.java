package id.ac.ui.cs.advprog.inventorykatalog.usecase;

import id.ac.ui.cs.advprog.inventorykatalog.client.AuthClient;
import id.ac.ui.cs.advprog.inventorykatalog.model.Product;
import id.ac.ui.cs.advprog.inventorykatalog.service.ProductService;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class DeleteProductUseCase {

    private final ProductService productService;
    private final AuthClient authClient;

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

        String currentJastiperId = authClient.getCurrentJastiperId(authorizationHeader);

        if (!currentJastiperId.equals(product.getJastiperId())) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "Only the product owner can delete this product"
            );
        }

        productService.deleteById(id);
    }
}