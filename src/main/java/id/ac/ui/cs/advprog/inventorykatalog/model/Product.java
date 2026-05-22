package id.ac.ui.cs.advprog.inventorykatalog.model;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
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

    // BARU(War): REGULAR untuk barang biasa, LIMITED untuk barang war/flash sale.
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ProductType productType;

    // BARU(War): Waktu mulai sesi war.
    private LocalDateTime warStartTime;

    // BARU(War): Waktu selesai sesi war.
    private LocalDateTime warEndTime;

    // BARU(War): Batas maksimal quantity per checkout.
    @Column(nullable = false)
    private int maxQuantityPerCheckout;

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
        validateWarFields();
    }

    @PreUpdate
    public void preUpdate() {
        applyDefaultValues();
        validateRating();
        validateWarFields();
    }

    private void applyDefaultValues() {
        if (rating == null) {
            rating = 0.0;
        }

        if (ratingCount < 0) {
            ratingCount = 0;
        }

        if (productType == null) {
            productType = ProductType.REGULAR;
        }

        if (maxQuantityPerCheckout < 0) {
            maxQuantityPerCheckout = 0;
        }
    }

    private void validateRating() {
        if (rating < 0.0 || rating > 5.0) {
            throw new IllegalArgumentException("Rating must be between 0.0 and 5.0");
        }
    }

    private void validateWarFields() {
        if (productType == ProductType.LIMITED) {
            if (warStartTime == null || warEndTime == null) {
                throw new IllegalArgumentException("Limited product must have war start time and war end time");
            }

            if (!warEndTime.isAfter(warStartTime)) {
                throw new IllegalArgumentException("War end time must be after war start time");
            }

            if (maxQuantityPerCheckout < 1) {
                throw new IllegalArgumentException("Limited product must have max quantity per checkout at least 1");
            }
        }
    }
}