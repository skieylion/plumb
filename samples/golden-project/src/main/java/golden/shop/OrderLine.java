package golden.shop;

import java.math.BigDecimal;

public record OrderLine(String sku, int quantity, BigDecimal unitPrice, Category category) {

    public enum Category { FOOD, ELECTRONICS, BOOKS, CLOTHING }

    public OrderLine {
        if (sku == null || sku.isBlank()) {
            throw new IllegalArgumentException("sku must not be blank");
        }
        if (quantity <= 0) {
            throw new IllegalArgumentException("quantity must be positive");
        }
        if (unitPrice == null || unitPrice.signum() < 0) {
            throw new IllegalArgumentException("unitPrice must not be negative");
        }
    }

    public BigDecimal total() {
        return unitPrice.multiply(BigDecimal.valueOf(quantity));
    }
}
