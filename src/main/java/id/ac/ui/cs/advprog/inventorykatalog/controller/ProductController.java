package id.ac.ui.cs.advprog.inventorykatalog.controller;

import id.ac.ui.cs.advprog.inventorykatalog.model.Product;
import id.ac.ui.cs.advprog.inventorykatalog.service.ProductService;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
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
    public ResponseEntity<Product> createProduct(@RequestBody Product product) {
        Product savedProduct = productService.save(product);
        return ResponseEntity.ok(savedProduct);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Product> updateProduct(@PathVariable String id, @RequestBody Product productDetails) {
        Optional<Product> optionalProduct = productService.findById(id);
        if (optionalProduct.isPresent()) {
            Product product = optionalProduct.get();
            product.setNama(productDetails.getNama());
            product.setDeskripsi(productDetails.getDeskripsi());
            product.setHarga(productDetails.getHarga());
            product.setStok(productDetails.getStok());
            product.setNegaraAsal(productDetails.getNegaraAsal());
            product.setTanggalPembelian(productDetails.getTanggalPembelian());
            product.setTanggalKembali(productDetails.getTanggalKembali());

            Product updatedProduct = productService.save(product);
            return ResponseEntity.ok(updatedProduct);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable String id) {
        if (productService.existsById(id)) {
            productService.deleteById(id);
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/search")
    public ResponseEntity<List<Product>> searchByName(@RequestParam String nama) {
        return ResponseEntity.ok(productService.findByNamaContainingIgnoreCase(nama));
    }

    @GetMapping("/jastiper/{jastiperId}")
    public ResponseEntity<List<Product>> getByJastiper(@PathVariable String jastiperId) {
        return ResponseEntity.ok(productService.findByJastiperId(jastiperId));
    }
}