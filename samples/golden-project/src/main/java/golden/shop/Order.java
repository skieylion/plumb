package golden.shop;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public record Order(String id, Customer customer, List<OrderLine> lines, LocalDate placedOn, String couponCode) {

    public record Customer(String id, Tier tier, String country, boolean firstOrder) {
    }

    public enum Tier { REGULAR, SILVER, GOLD }

    public Order {
        lines = List.copyOf(lines);
    }

    public BigDecimal subtotal() {
        return lines.stream()
                .map(OrderLine::total)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public boolean hasCoupon() {
        return couponCode != null && !couponCode.isBlank();
    }
}
