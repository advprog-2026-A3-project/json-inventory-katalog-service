package id.ac.ui.cs.advprog.inventorykatalog.controller;

import id.ac.ui.cs.advprog.inventorykatalog.model.Product;
import id.ac.ui.cs.advprog.inventorykatalog.service.ProductService;
import id.ac.ui.cs.advprog.inventorykatalog.usecase.CreateProductUseCase;
import id.ac.ui.cs.advprog.inventorykatalog.usecase.DeleteProductUseCase;
import id.ac.ui.cs.advprog.inventorykatalog.usecase.GetMyProductsUseCase;
import id.ac.ui.cs.advprog.inventorykatalog.usecase.UpdateProductUseCase;
import jakarta.annotation.PostConstruct;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import id.ac.ui.cs.advprog.inventorykatalog.dto.ProductRatingRequest;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/products")
public class ProductController {

    private final ProductService productService;
    private final CreateProductUseCase createProductUseCase;
    private final UpdateProductUseCase updateProductUseCase;
    private final DeleteProductUseCase deleteProductUseCase;
    private final GetMyProductsUseCase getMyProductsUseCase;

    public ProductController(
            ProductService productService,
            CreateProductUseCase createProductUseCase,
            UpdateProductUseCase updateProductUseCase,
            DeleteProductUseCase deleteProductUseCase,
            GetMyProductsUseCase getMyProductsUseCase
    ) {
        this.productService = productService;
        this.createProductUseCase = createProductUseCase;
        this.updateProductUseCase = updateProductUseCase;
        this.deleteProductUseCase = deleteProductUseCase;
        this.getMyProductsUseCase = getMyProductsUseCase;
    }

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
        Product savedProduct = createProductUseCase.execute(authorizationHeader, product);

        return ResponseEntity.ok(savedProduct);
    }

    @GetMapping("/me")
    public ResponseEntity<List<Product>> getMyProducts(
            @RequestHeader(value = "Authorization", required = false) String authorizationHeader
    ) {
        List<Product> products = getMyProductsUseCase.execute(authorizationHeader);

        return ResponseEntity.ok(products);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Product> updateProduct(
            @PathVariable String id,
            @RequestHeader(value = "Authorization", required = false) String authorizationHeader,
            @RequestBody Product productDetails
    ) {
        Product updatedProduct = updateProductUseCase.execute(
                id,
                authorizationHeader,
                productDetails
        );

        return ResponseEntity.ok(updatedProduct);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduct(
            @PathVariable String id,
            @RequestHeader(value = "Authorization", required = false) String authorizationHeader
    ) {
        deleteProductUseCase.execute(id, authorizationHeader);

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

    @PatchMapping("/{id}/stock/reduce")
    public ResponseEntity<Product> reduceStock(
            @PathVariable String id,
            @RequestParam int quantity
    ) {
        Product updatedProduct = productService.reduceStock(id, quantity);
        return ResponseEntity.ok(updatedProduct);
    }

    @PatchMapping("/{id}/rating")
    public ResponseEntity<Product> addProductRating(
            @PathVariable String id,
            @RequestBody ProductRatingRequest request
    ) {
        Product updatedProduct = productService.addRating(id, request.getRating());
        return ResponseEntity.ok(updatedProduct);
    }
}