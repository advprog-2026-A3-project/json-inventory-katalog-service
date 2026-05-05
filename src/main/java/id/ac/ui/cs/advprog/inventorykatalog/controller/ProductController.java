package id.ac.ui.cs.advprog.inventorykatalog.controller;

import id.ac.ui.cs.advprog.inventorykatalog.client.AuthClient;
import id.ac.ui.cs.advprog.inventorykatalog.model.Product;
import id.ac.ui.cs.advprog.inventorykatalog.service.ProductService;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/products")
public class ProductController {

    @Autowired
    private ProductService productService;

    @Autowired
    private AuthClient authClient;

    @PostConstruct
    public void initDummyData() {
        if (productService.count() == 0) {
            Product barang1 = Product.builder()
                    .nama("KitKat Matcha Jepang")
                    .deskripsi("Cokelat asli dari Akihabara")
                    .harga(55000)
                    .stok(20)
                    .negaraAsal("Jepang")
                    .tanggalPembelian(LocalDate.of(2026, 5, 10))
                    .tanggalKembali(LocalDate.of(2026, 5, 15))
                    .jastiperId("jastiper-001")
                    .build();
            productService.save(barang1);
        }
    }

    @GetMapping
    public ResponseEntity<List<Product>> getAllProducts() {
        return ResponseEntity.ok(productService.findAll());
    }

    @PostMapping
    public ResponseEntity<Product> createProduct(
            @RequestHeader(value = "Authorization", required = false) String authorizationHeader,
            @RequestBody Product product
    ) {
        String currentJastiperId = authClient.getCurrentJastiperId(authorizationHeader);
        product.setJastiperId(currentJastiperId);

        Product savedProduct = productService.save(product);
        return ResponseEntity.ok(savedProduct);
    }

    @GetMapping("/me")
    public ResponseEntity<List<Product>> getMyProducts(
            @RequestHeader(value = "Authorization", required = false) String authorizationHeader
    ) {
        String currentJastiperId = authClient.getCurrentJastiperId(authorizationHeader);
        return ResponseEntity.ok(productService.findByJastiperId(currentJastiperId));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Product> updateProduct(
            @PathVariable String id,
            @RequestHeader(value = "Authorization", required = false) String authorizationHeader,
            @RequestBody Product productDetails
    ) {
        Optional<Product> optionalProduct = productService.findById(id);

        if (optionalProduct.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        String currentJastiperId = authClient.getCurrentJastiperId(authorizationHeader);
        Product product = optionalProduct.get();

        if (!currentJastiperId.equals(product.getJastiperId())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        product.setNama(productDetails.getNama());
        product.setDeskripsi(productDetails.getDeskripsi());
        product.setHarga(productDetails.getHarga());
        product.setStok(productDetails.getStok());
        product.setNegaraAsal(productDetails.getNegaraAsal());
        product.setTanggalPembelian(productDetails.getTanggalPembelian());
        product.setTanggalKembali(productDetails.getTanggalKembali());
        product.setImageUrls(productDetails.getImageUrls());

        Product updatedProduct = productService.save(product);
        return ResponseEntity.ok(updatedProduct);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduct(
            @PathVariable String id,
            @RequestHeader(value = "Authorization", required = false) String authorizationHeader
    ) {
        Optional<Product> optionalProduct = productService.findById(id);

        if (optionalProduct.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        String currentJastiperId = authClient.getCurrentJastiperId(authorizationHeader);
        Product product = optionalProduct.get();

        if (!currentJastiperId.equals(product.getJastiperId())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        productService.deleteById(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Product> getProductById(@PathVariable String id) {
        Optional<Product> product = productService.findById(id);
        return product.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/search/nama")
    public ResponseEntity<List<Product>> searchByName(@RequestParam String nama) {
        return ResponseEntity.ok(productService.findByNamaContainingIgnoreCase(nama));
    }

    @GetMapping("/search/jastiper")
    public ResponseEntity<List<Product>> getByJastiper(@RequestParam String jastiperId) {
        return ResponseEntity.ok(productService.findByJastiperId(jastiperId));
    }
}