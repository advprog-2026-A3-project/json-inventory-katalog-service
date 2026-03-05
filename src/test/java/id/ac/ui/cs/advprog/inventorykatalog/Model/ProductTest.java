package id.ac.ui.cs.advprog.inventorykatalog.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class ProductTest {
    Product product;

    @BeforeEach
    void setUp() {
        product = new Product();
        product.setId("eb558e9f-1c39-460e-8860-71af6af63bd6");
        product.setName("Sampo Cap Bambang");
        product.setQuantity(100);
    }

    @Test
    void testGetProductId() {
        assertEquals("eb558e9f-1c39-460e-8860-71af6af63bd6", product.getId());
    }

    @Test
    void testGetProductName() {
        assertEquals("Sampo Cap Bambang", product.getName());
    }

    @Test
    void testGetProductQuantity() {
        assertEquals(100, product.getQuantity());
    }
}