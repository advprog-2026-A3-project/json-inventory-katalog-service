package id.ac.ui.cs.advprog.inventorykatalog.controller;

import id.ac.ui.cs.advprog.inventorykatalog.model.Product;
import id.ac.ui.cs.advprog.inventorykatalog.repository.ProductRepository;
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
    private ProductRepository productRepository;

    @PostConstruct
    public void initDummyData() {
        if (productRepository.count() == 0) {
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
            productRepository.save(barang1);
        }
    }

    @GetMapping
    public ResponseEntity<List<Product>> getAllProducts() {
        return ResponseEntity.ok(productRepository.findAll());
    }

    @PostMapping
    public ResponseEntity<Product> createProduct(@RequestBody Product product) {
        Product savedProduct = productRepository.save(product);
        return ResponseEntity.ok(savedProduct);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Product> updateProduct(@PathVariable String id, @RequestBody Product productDetails) {
        Optional<Product> optionalProduct = productRepository.findById(id);
        if (optionalProduct.isPresent()) {
            Product product = optionalProduct.get();
            product.setNama(productDetails.getNama());
            product.setDeskripsi(productDetails.getDeskripsi());
            product.setHarga(productDetails.getHarga());
            product.setStok(productDetails.getStok());
            product.setNegaraAsal(productDetails.getNegaraAsal());
            product.setTanggalPembelian(productDetails.getTanggalPembelian());
            product.setTanggalKembali(productDetails.getTanggalKembali());

            Product updatedProduct = productRepository.save(product);
            return ResponseEntity.ok(updatedProduct);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable String id) {
        if (productRepository.existsById(id)) {
            productRepository.deleteById(id);
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    // http://localhost:8080/api/products/search?nama=kitkat
    @GetMapping("/search")
    public ResponseEntity<List<Product>> searchByName(@RequestParam String nama) {
        return ResponseEntity.ok(productRepository.findByNamaContainingIgnoreCase(nama));
    }

    // http://localhost:8080/api/products/jastiper/jastiper-001
    @GetMapping("/jastiper/{jastiperId}")
    public ResponseEntity<List<Product>> getByJastiper(@PathVariable String jastiperId) {
        return ResponseEntity.ok(productRepository.findByJastiperId(jastiperId));
    }
}