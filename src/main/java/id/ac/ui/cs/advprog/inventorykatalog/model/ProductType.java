package id.ac.ui.cs.advprog.inventorykatalog.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum ProductType {
    REGULAR("regular"),
    LIMITED("limited");

    private final String value;

    ProductType(String value) {
        this.value = value;
    }

    @JsonValue
    public String getValue() {
        return value;
    }

    @JsonCreator
    public static ProductType fromValue(String value) {
        if (value == null) {
            return null;
        }

        for (ProductType type : ProductType.values()) {
            if (type.value.equalsIgnoreCase(value)
                    || type.name().equalsIgnoreCase(value)) {
                return type;
            }
        }

        throw new IllegalArgumentException("Invalid product type: " + value);
    }
}