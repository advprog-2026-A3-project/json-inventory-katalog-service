package id.ac.ui.cs.advprog.inventorykatalog.usecase;

import id.ac.ui.cs.advprog.inventorykatalog.client.AuthClient;
import id.ac.ui.cs.advprog.inventorykatalog.model.Product;
import id.ac.ui.cs.advprog.inventorykatalog.service.ProductService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GetMyProductsUseCase {

    private final ProductService productService;
    private final AuthClient authClient;

    public GetMyProductsUseCase(ProductService productService, AuthClient authClient) {
        this.productService = productService;
        this.authClient = authClient;
    }

    public List<Product> execute(String authorizationHeader) {
        String currentJastiperId = authClient.getCurrentJastiperId(authorizationHeader);

        return productService.findByJastiperId(currentJastiperId);
    }
}