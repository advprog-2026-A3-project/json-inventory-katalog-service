package id.ac.ui.cs.advprog.inventorykatalog.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

class ProductTypeTest {

    @Test
    void getValueShouldReturnJsonValue() {
        assertEquals("regular", ProductType.REGULAR.getValue());
        assertEquals("limited", ProductType.LIMITED.getValue());
    }

    @Test
    void fromValueShouldAcceptJsonValueCaseInsensitive() {
        assertEquals(ProductType.REGULAR, ProductType.fromValue("regular"));
        assertEquals(ProductType.REGULAR, ProductType.fromValue("REGULAR"));
        assertEquals(ProductType.LIMITED, ProductType.fromValue("limited"));
        assertEquals(ProductType.LIMITED, ProductType.fromValue("LIMITED"));
    }

    @Test
    void fromValueShouldAcceptEnumNameCaseInsensitive() {
        assertEquals(ProductType.REGULAR, ProductType.fromValue("Regular"));
        assertEquals(ProductType.LIMITED, ProductType.fromValue("Limited"));
    }

    @Test
    void fromValueShouldReturnNullWhenInputIsNull() {
        assertNull(ProductType.fromValue(null));
    }

    @Test
    void fromValueShouldRejectUnknownType() {
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> ProductType.fromValue("flash-sale")
        );

        assertEquals("Invalid product type: flash-sale", exception.getMessage());
    }
}
