package golden.shop;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class OrderService {

    private static final BigDecimal FREE_SHIPPING_THRESHOLD = new BigDecimal("100");
    private static final Map<String, BigDecimal> SHIPPING_BY_COUNTRY = Map.of(
            "DE", new BigDecimal("4.90"),
            "FR", new BigDecimal("5.90"),
            "US", new BigDecimal("12.00"));

    private final DiscountPolicy weekendPolicy;
    private final List<String> auditLog = new ArrayList<>();

    public OrderService(DiscountPolicy weekendPolicy) {
        this.weekendPolicy = weekendPolicy;
    }

    public BigDecimal calculate(Order order) {
        if (order == null || order.lines().isEmpty()) {
            throw new IllegalArgumentException("order must have lines");
        }
        BigDecimal total = BigDecimal.ZERO;
        int electronicsCount = 0;
        for (OrderLine line : order.lines()) {
            BigDecimal lineTotal = line.total();
            switch (line.category()) {
                case FOOD:
                    if (line.quantity() >= 10) {
                        lineTotal = lineTotal.multiply(new BigDecimal("0.95"));
                    }
                    break;
                case ELECTRONICS:
                    electronicsCount += line.quantity();
                    boolean premiumCustomer = order.customer().tier() != Order.Tier.REGULAR;
                    if (lineTotal.compareTo(new BigDecimal("1000")) > 0 && premiumCustomer) {
                        lineTotal = lineTotal.subtract(new BigDecimal("50"));
                    }
                    break;
                case BOOKS:
                    lineTotal = lineTotal.multiply(new BigDecimal("0.90"));
                    break;
                default:
                    break;
            }
            total = total.add(lineTotal);
        }
        if (electronicsCount > 3) {
            total = total.subtract(new BigDecimal("20"));
        }
        BigDecimal tierDiscount = DiscountPolicy.forTier(order.customer().tier()).discount(order);
        BigDecimal weekendDiscount = weekendPolicy.discount(order);
        total = total.subtract(tierDiscount.max(weekendDiscount));
        if (order.hasCoupon()) {
            if (order.couponCode().startsWith("WELCOME") && order.customer().firstOrder()) {
                total = total.multiply(new BigDecimal("0.85"));
            } else if (order.couponCode().startsWith("FLAT")) {
                try {
                    BigDecimal flat = new BigDecimal(order.couponCode().substring(4));
                    total = total.subtract(flat);
                } catch (NumberFormatException e) {
                    auditLog.add("invalid coupon " + order.couponCode());
                }
            } else {
                auditLog.add("unknown coupon " + order.couponCode());
            }
        }
        BigDecimal shipping = SHIPPING_BY_COUNTRY.get(order.customer().country());
        if (shipping == null) {
            shipping = new BigDecimal("20.00");
        }
        if (total.compareTo(FREE_SHIPPING_THRESHOLD) >= 0 || order.customer().tier() == Order.Tier.GOLD) {
            shipping = BigDecimal.ZERO;
        }
        total = total.add(shipping);
        return total.signum() < 0 ? BigDecimal.ZERO : total.setScale(2, RoundingMode.HALF_UP);
    }

    public boolean canPlace(Order order) {
        return order != null
                && !order.lines().isEmpty()
                && order.customer() != null
                && SHIPPING_BY_COUNTRY.containsKey(order.customer().country());
    }

    public List<String> place(Order order) {
        if (!canPlace(order)) {
            auditLog.add("rejected " + (order == null ? "null" : order.id()));
            return List.copyOf(auditLog);
        }
        BigDecimal amount = calculate(order);
        auditLog.add("placed " + order.id() + " for " + amount);
        return List.copyOf(auditLog);
    }

    public List<String> auditLog() {
        return List.copyOf(auditLog);
    }
}
