import org.joda.time.DateTime;

import java.math.BigDecimal;

import edu.umd.cs.findbugs.annotations.NonNull;
import edu.umd.cs.findbugs.annotations.Nullable;

public class Merchandise {

    private static final Double PENCIL_PROMO_MIN_REDUCTION_PERCENT = 0.05;
    private static final Double PENCIL_PROMO_MAX_REDUCTION_PERCENT = 0.30;
    private static final int DAYS_FOR_STABLE_PRICE = 30;
    private static final int MAX_PENCIL_PROMO_DURATION_DAYS = 30;

    private Price price;

    public Merchandise(double price) {
        setPrice(price);
    }

    public double getPrice() {
        return price.amount;
    }

    public boolean isRedPencilPromo() {
        return price.redPencilPromoExpiration != null && isUnexpired(price.redPencilPromoExpiration);
    }

    private boolean isUnexpired(DateTime date) {
        return date.isAfterNow() || date.isEqualNow();
    }

    public void setPrice(double newPrice) {
        DateTime now = DateTime.now();
        DateTime redPencilPromoExpiration = getRedPencilPromoExpiration(newPrice, now);
        price = new Price(newPrice, now, redPencilPromoExpiration);
    }

    @Nullable
    private DateTime getRedPencilPromoExpiration(double newPrice, DateTime now) {
        DateTime redPencilPromoExpiration = null;
        if (price != null) {
            if (price.redPencilPromoExpiration != null && isUnexpired(price.redPencilPromoExpiration)) {
                redPencilPromoExpiration = price.redPencilPromoExpiration;
            } else if (isCurrentPriceStableAsOf(price.time, now) && isRedPencilPromoPriceChange(price.amount, newPrice)) {
                redPencilPromoExpiration = now.plusDays(MAX_PENCIL_PROMO_DURATION_DAYS);
            }
        }
        return redPencilPromoExpiration;
    }

    private boolean isCurrentPriceStableAsOf(DateTime currentPriceStartTime, DateTime time) {
        return !currentPriceStartTime.plusDays(DAYS_FOR_STABLE_PRICE)
                                     .isAfter(time);
    }

    private boolean isRedPencilPromoPriceChange(double oldPrice, double newPriceAmount) {
        double percentReduced = getPercentPriceReduced(oldPrice, newPriceAmount);
        return percentReduced >= PENCIL_PROMO_MIN_REDUCTION_PERCENT
                && percentReduced <= PENCIL_PROMO_MAX_REDUCTION_PERCENT;
    }

    private double getPercentPriceReduced(double originalPriceAmount, double newPriceAmount) {
        BigDecimal bigCurrentPrice = BigDecimal.valueOf(originalPriceAmount);
        BigDecimal bigNewPrice = BigDecimal.valueOf(newPriceAmount);
        BigDecimal percentOfCurrent = bigNewPrice.divide(bigCurrentPrice,
                                                          2,
                                                          BigDecimal.ROUND_HALF_UP);
        return BigDecimal.ONE.subtract(percentOfCurrent).doubleValue();
    }

    private static class Price {

        private final double amount;
        @NonNull private final DateTime time;
        @Nullable private final DateTime redPencilPromoExpiration;

        private Price(double amount, @NonNull DateTime priceDate, @Nullable DateTime redPencilPromoExpiration) {
            this.amount = amount;
            this.time = priceDate;
            this.redPencilPromoExpiration = redPencilPromoExpiration;
        }
    }
}
