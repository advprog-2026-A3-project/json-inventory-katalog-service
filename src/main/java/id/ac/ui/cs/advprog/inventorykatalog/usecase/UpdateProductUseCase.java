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
public class UpdateProductUseCase {

    private final ProductService productService;
    private final AuthClient authClient;
    private final ProductAuthorizationPolicy authorizationPolicy =
            ProductAuthorizationPolicy.getInstance();

    public UpdateProductUseCase(ProductService productService, AuthClient authClient) {
        this.productService = productService;
        this.authClient = authClient;
    }

    public Product execute(String id, String authorizationHeader, Product productDetails) {
        Product product = productService.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Product not found"
                ));


        AuthProfileResponse currentUser = authClient.getCurrentUserProfile(authorizationHeader);
        authorizationPolicy.validateCanManageProduct(currentUser, product);

        product.setNama(productDetails.getNama());
        product.setDeskripsi(productDetails.getDeskripsi());
        product.setHarga(productDetails.getHarga());
        product.setStok(productDetails.getStok());
        product.setNegaraAsal(productDetails.getNegaraAsal());
        product.setTanggalPembelian(productDetails.getTanggalPembelian());
        product.setTanggalKembali(productDetails.getTanggalKembali());
        product.setImageUrls(productDetails.getImageUrls());

        return productService.save(product);
    }
}