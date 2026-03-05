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
        product.setNama("Tas Ransel");
        product.setStok(10);
        product.setHarga(150000.0);
        product.setJastiperId("jastip-001");
    }

    @Test
    void testGetProductId() {
        assertEquals("eb558e9f-1c39-460e-8860-71af6af63bd6", product.getId());
    }

    @Test
    void testGetProductNama() {
        assertEquals("Tas Ransel", product.getNama());
    }

    @Test
    void testGetProductStok() {
        assertEquals(10, product.getStok());
    }

    @Test
    void testGetProductHarga() {
        assertEquals(150000.0, product.getHarga());
    }
}