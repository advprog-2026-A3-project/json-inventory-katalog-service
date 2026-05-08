package id.ac.ui.cs.advprog.inventorykatalog.usecase;

import id.ac.ui.cs.advprog.inventorykatalog.client.AuthClient;
import id.ac.ui.cs.advprog.inventorykatalog.model.Product;
import id.ac.ui.cs.advprog.inventorykatalog.service.ProductService;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class UpdateProductUseCase {

    private final ProductService productService;
    private final AuthClient authClient;

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

        String currentJastiperId = authClient.getCurrentJastiperId(authorizationHeader);

        if (!currentJastiperId.equals(product.getJastiperId())) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "Only the product owner can update this product"
            );
        }

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