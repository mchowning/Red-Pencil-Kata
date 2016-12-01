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
        return price.redPencilPromo != null && isUnexpired(price.redPencilPromo.expiration);
    }

    private boolean isUnexpired(DateTime date) {
        return date.isAfterNow() || date.isEqualNow();
    }

    public void setPrice(double newPrice) {
        DateTime now = DateTime.now();
        RedPencilPromo redPencilPromo = getRedPencilPromo(newPrice, now);
        price = new Price(newPrice, now, redPencilPromo);
    }

    @Nullable
    private RedPencilPromo getRedPencilPromo(double newPrice, DateTime now) {
        RedPencilPromo redPencilPromo = null;
        if (price != null && price.amount >= newPrice) {
            if (price.redPencilPromo != null) {
                double percentReduced = getPercentPriceReduced(price.redPencilPromo.prePromoPrice, newPrice);
                if (percentReduced > PENCIL_PROMO_MAX_REDUCTION_PERCENT) {
                    return null;
                } else if (price.redPencilPromo.expiration != null && isUnexpired(price.redPencilPromo.expiration)) {
                    redPencilPromo = new RedPencilPromo(price.redPencilPromo.prePromoPrice, price.redPencilPromo.expiration);
                }
            } else if (isCurrentPriceStableAsOf(price.time, now) && isRedPencilPromoPriceChange(price.amount, newPrice)) {
                redPencilPromo = new RedPencilPromo(price.amount, now.plusDays(MAX_PENCIL_PROMO_DURATION_DAYS));
            }
        }
        return redPencilPromo;
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
        @Nullable private final RedPencilPromo redPencilPromo;

        private Price(double amount, @NonNull DateTime priceDate, @Nullable RedPencilPromo redPencilPromo) {
            this.amount = amount;
            this.time = priceDate;
            this.redPencilPromo = redPencilPromo;
        }

    }

    private static class RedPencilPromo {

        private final double prePromoPrice;
        private final DateTime expiration;

        private RedPencilPromo(double prePromoPrice, DateTime expiration) {
            this.prePromoPrice = prePromoPrice;
            this.expiration = expiration;
        }
    }
}
