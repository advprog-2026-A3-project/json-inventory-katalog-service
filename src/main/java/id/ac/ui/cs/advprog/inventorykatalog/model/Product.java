package id.ac.ui.cs.advprog.inventorykatalog.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;
import java.util.List;

@Entity
@Table(name = "products")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(nullable = false)
    private String nama;

    private String deskripsi;

    @Column(nullable = false)
    private double harga;

    @Column(nullable = false)
    private int stok;

    @Column(nullable = false)
    private Double rating;

    @Column(nullable = false)
    private int ratingCount;

    private String negaraAsal;
    private LocalDate tanggalPembelian;
    private LocalDate tanggalKembali;

    @ElementCollection
    @CollectionTable(name = "product_images", joinColumns = @JoinColumn(name = "product_id"))
    @Column(name = "image_url")
    private List<String> imageUrls;

    @Column(nullable = false)
    private String jastiperId;

    @PrePersist
    public void prePersist() {
        applyDefaultValues();
        validateRating();
    }

    @PreUpdate
    public void preUpdate() {
        applyDefaultValues();
        validateRating();
    }

    private void applyDefaultValues() {
        if (rating == null) {
            rating = 0.0;
        }

        if (ratingCount < 0) {
            ratingCount = 0;
        }
    }

    private void validateRating() {
        if (rating < 0.0 || rating > 5.0) {
            throw new IllegalArgumentException("Rating must be between 0.0 and 5.0");
        }
    }
}