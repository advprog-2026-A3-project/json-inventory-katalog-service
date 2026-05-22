package id.ac.ui.cs.advprog.inventorykatalog.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.Test;

class ProductTest {

    @Test
    void prePersistShouldApplyDefaultValues() {
        Product product = Product.builder()
                .nama("Snack")
                .harga(1000.0)
                .stok(10)
                .rating(null)
                .ratingCount(-10)
                .productType(null)
                .maxQuantityPerCheckout(-3)
                .jastiperId("jastiper-1")
                .imageUrls(List.of("image-1"))
                .build();

        product.prePersist();

        assertEquals(0.0, product.getRating());
        assertEquals(0, product.getRatingCount());
        assertEquals(ProductType.REGULAR, product.getProductType());
        assertEquals(0, product.getMaxQuantityPerCheckout());
        assertEquals(List.of("image-1"), product.getImageUrls());
    }

    @Test
    void preUpdateShouldApplyDefaultValues() {
        Product product = Product.builder()
                .rating(null)
                .ratingCount(-1)
                .productType(null)
                .maxQuantityPerCheckout(-1)
                .build();

        product.preUpdate();

        assertEquals(0.0, product.getRating());
        assertEquals(0, product.getRatingCount());
        assertEquals(ProductType.REGULAR, product.getProductType());
        assertEquals(0, product.getMaxQuantityPerCheckout());
    }

    @Test
    void prePersistShouldRejectNegativeRating() {
        Product product = Product.builder()
                .rating(-0.1)
                .ratingCount(0)
                .productType(ProductType.REGULAR)
                .build();

        assertThrows(IllegalArgumentException.class, product::prePersist);
    }

    @Test
    void prePersistShouldRejectRatingGreaterThanFive() {
        Product product = Product.builder()
                .rating(5.1)
                .ratingCount(0)
                .productType(ProductType.REGULAR)
                .build();

        assertThrows(IllegalArgumentException.class, product::prePersist);
    }

    @Test
    void limitedProductShouldRequireWarTimes() {
        Product product = Product.builder()
                .rating(0.0)
                .ratingCount(0)
                .productType(ProductType.LIMITED)
                .maxQuantityPerCheckout(1)
                .build();

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, product::prePersist);
        assertEquals("Limited product must have war start time and war end time", exception.getMessage());
    }

    @Test
    void limitedProductShouldRequireEndTimeAfterStartTime() {
        LocalDateTime start = LocalDateTime.of(2026, 5, 22, 10, 0);
        Product product = Product.builder()
                .rating(0.0)
                .ratingCount(0)
                .productType(ProductType.LIMITED)
                .warStartTime(start)
                .warEndTime(start)
                .maxQuantityPerCheckout(1)
                .build();

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, product::prePersist);
        assertEquals("War end time must be after war start time", exception.getMessage());
    }

    @Test
    void limitedProductShouldRequirePositiveMaxQuantity() {
        LocalDateTime start = LocalDateTime.of(2026, 5, 22, 10, 0);
        Product product = Product.builder()
                .rating(0.0)
                .ratingCount(0)
                .productType(ProductType.LIMITED)
                .warStartTime(start)
                .warEndTime(start.plusHours(1))
                .maxQuantityPerCheckout(0)
                .build();

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, product::prePersist);
        assertEquals("Limited product must have max quantity per checkout at least 1", exception.getMessage());
    }

    @Test
    void validLimitedProductShouldPassValidation() {
        LocalDateTime start = LocalDateTime.of(2026, 5, 22, 10, 0);
        Product product = Product.builder()
                .rating(4.5)
                .ratingCount(3)
                .productType(ProductType.LIMITED)
                .warStartTime(start)
                .warEndTime(start.plusHours(1))
                .maxQuantityPerCheckout(2)
                .build();

        product.prePersist();

        assertEquals(ProductType.LIMITED, product.getProductType());
        assertEquals(2, product.getMaxQuantityPerCheckout());
    }
}
