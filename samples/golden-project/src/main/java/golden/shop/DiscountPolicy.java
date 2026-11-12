package golden.shop;

import java.math.BigDecimal;
import java.time.DayOfWeek;

public sealed interface DiscountPolicy {

    BigDecimal discount(Order order);

    record None() implements DiscountPolicy {

        @Override
        public BigDecimal discount(Order order) {
            return BigDecimal.ZERO;
        }
    }

    record Percentage(int percent) implements DiscountPolicy {

        public Percentage {
            if (percent < 0 || percent > 100) {
                throw new IllegalArgumentException("percent out of range");
            }
        }

        @Override
        public BigDecimal discount(Order order) {
            return order.subtotal()
                    .multiply(BigDecimal.valueOf(percent))
                    .divide(BigDecimal.valueOf(100));
        }
    }

    record Weekend(BigDecimal amount) implements DiscountPolicy {

        @Override
        public BigDecimal discount(Order order) {
            DayOfWeek day = order.placedOn().getDayOfWeek();
            boolean weekend = day == DayOfWeek.SATURDAY || day == DayOfWeek.SUNDAY;
            return weekend ? amount.min(order.subtotal()) : BigDecimal.ZERO;
        }
    }

    static DiscountPolicy forTier(Order.Tier tier) {
        return switch (tier) {
            case REGULAR -> new None();
            case SILVER -> new Percentage(5);
            case GOLD -> new Percentage(10);
        };
    }
}
