import org.joda.time.DateTime;

import java.math.BigDecimal;

import edu.umd.cs.findbugs.annotations.NonNull;
import edu.umd.cs.findbugs.annotations.Nullable;

public class Merchandise {

    private static final Double PENCIL_PROMOTION_MIN_REDUCTION_PERCENT = 0.05;
    private static final Double PENCIL_PROMOTION_MAX_REDUCTION_PERCENT = 0.30;
    private static final int DAYS_FOR_STABLE_PRICE = 30;
    private static final int MAX_PENCIL_PROMO_DURATION_DAYS = 30;

    @Nullable private Price previousPrice;
    private Price price;

    public Merchandise(double price) {
        this.price = new Price(price);
    }

    public double getPrice() {
        return price.amount;
    }

    public boolean isRedPencilPromotion() {
        if (isPreviousPriceStable()) {
            double percentReduced = getPercentPriceReduced();
            boolean isRedPencilReductionAmount = percentReduced >= PENCIL_PROMOTION_MIN_REDUCTION_PERCENT
                                                        && percentReduced <= PENCIL_PROMOTION_MAX_REDUCTION_PERCENT;
            boolean isUnexpired = price.time.plusDays(MAX_PENCIL_PROMO_DURATION_DAYS).isAfterNow()
                                    || price.time.plusDays(MAX_PENCIL_PROMO_DURATION_DAYS).isEqualNow();
            return isRedPencilReductionAmount && isUnexpired;
        }
        return false;
    }

    private double getPercentPriceReduced() {
        BigDecimal bigPreviousPrice = BigDecimal.valueOf(previousPrice.amount);
        BigDecimal bigPrice = BigDecimal.valueOf(price.amount);
        BigDecimal percentOfPrevious = bigPrice.divide(bigPreviousPrice,
                                                       2,
                                                       BigDecimal.ROUND_HALF_UP);
        return BigDecimal.ONE.subtract(percentOfPrevious).doubleValue();
    }

    private boolean isPreviousPriceStable() {
        return previousPrice != null &&
                !previousPrice.time
                              .plusDays(DAYS_FOR_STABLE_PRICE)
                              .isAfter(price.time.toInstant());
    }

    public void setPrice(double newPrice) {
        previousPrice = price;
        price = new Price(newPrice);
    }

    private static class Price {

        private final double amount;
        @NonNull private final DateTime time;

        private Price(double amount) {
            this.amount = amount;
            this.time = DateTime.now();
        }
    }
}
