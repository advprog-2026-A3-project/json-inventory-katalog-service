package id.ac.ui.cs.advprog.inventorykatalog.controller;

import id.ac.ui.cs.advprog.inventorykatalog.model.Product;
import id.ac.ui.cs.advprog.inventorykatalog.repository.ProductRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;

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
            System.out.println("Data dummy berhasil disimpan ke Supabase!");
        }
    }

    @GetMapping
    public ResponseEntity<List<Product>> getAllProducts() {
        List<Product> semuaBarang = productRepository.findAll();
        return ResponseEntity.ok(semuaBarang);
    }
}