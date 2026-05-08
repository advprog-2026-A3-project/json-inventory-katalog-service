package id.ac.ui.cs.advprog.inventorykatalog.usecase;

import id.ac.ui.cs.advprog.inventorykatalog.client.AuthClient;
import id.ac.ui.cs.advprog.inventorykatalog.model.Product;
import id.ac.ui.cs.advprog.inventorykatalog.service.ProductService;
import org.springframework.stereotype.Service;

@Service
public class CreateProductUseCase {

    private final ProductService productService;
    private final AuthClient authClient;

    public CreateProductUseCase(ProductService productService, AuthClient authClient) {
        this.productService = productService;
        this.authClient = authClient;
    }

    public Product execute(String authorizationHeader, Product product) {
        String currentJastiperId = authClient.getCurrentJastiperId(authorizationHeader);
        product.setJastiperId(currentJastiperId);

        return productService.save(product);
    }
}