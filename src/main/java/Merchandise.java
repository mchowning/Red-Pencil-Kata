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

    public Merchandise(double price, RedPencilPromoChecker promoChecker) {
        setPrice(price);
    }

    public double getPrice() {
        return price.amount;
    }

    public boolean isRedPencilPromo() {
        return price != null &&
                price.redPencilPromo != null &&
                price.redPencilPromo.expiration.isAfterNow();
    }

    public void setPrice(double newPrice) {
        DateTime now = DateTime.now();
        RedPencilPromo redPencilPromo = getRedPencilPromo(newPrice, now);
        price = new Price(newPrice, now, redPencilPromo);
    }

    @Nullable
    private RedPencilPromo getRedPencilPromo(double newPrice, DateTime now) {
        if (shouldContinuePromo(newPrice)) {
            return price.redPencilPromo;
        } else if (shouldStartNewPromo(newPrice)) {
            DateTime expiration = now.plusDays(MAX_PENCIL_PROMO_DURATION_DAYS)
                                     .plusMillis(1);
            return new RedPencilPromo(price.amount, expiration);
        }
        return null;
    }

    private boolean shouldContinuePromo(double newPrice) {
        return price != null &&
                price.redPencilPromo != null &&
                newPrice <= price.amount &&
                isRedPencilPromo() &&
                isRedPencilPromoPriceChange(price.redPencilPromo.prePromoPrice, newPrice);
    }

    private boolean shouldStartNewPromo(double newPrice) {
        return price != null &&
                isCurrentPriceStable() &&
                isRedPencilPromoPriceChange(price.amount, newPrice);
    }

    private boolean isCurrentPriceStable() {
        DateTime timeForStability = price.time.plusDays(DAYS_FOR_STABLE_PRICE);
        return timeForStability.isBeforeNow() || timeForStability.isEqualNow();
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
        @Nullable private final RedPencilPromo redPencilPromo;

        private Price(double amount, @NonNull DateTime priceDate, @Nullable RedPencilPromo redPencilPromo) {
            this.amount = amount;
            this.time = priceDate;
            this.redPencilPromo = redPencilPromo;
        }
    }

    private static class RedPencilPromo {

        private final double prePromoPrice;
        @NonNull private final DateTime expiration;

        private RedPencilPromo(double prePromoPrice, @NonNull DateTime expiration) {
            this.prePromoPrice = prePromoPrice;
            this.expiration = expiration;
        }
    }
}
