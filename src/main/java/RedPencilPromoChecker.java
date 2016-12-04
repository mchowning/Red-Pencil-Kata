import org.joda.time.DateTime;

import java.math.BigDecimal;

import edu.umd.cs.findbugs.annotations.Nullable;

public class RedPencilPromoChecker {

    private static final Double PENCIL_PROMO_MIN_REDUCTION_PERCENT = 0.05;
    private static final Double PENCIL_PROMO_MAX_REDUCTION_PERCENT = 0.30;
    private static final int DAYS_FOR_STABLE_PRICE = 30;
    private static final int MAX_PENCIL_PROMO_DURATION_DAYS = 30;

    @Nullable private Price storedPrice;
    @Nullable private DateTime promoExpiration;
    private double prePromoPrice;

    public void notifyOfPriceUpdate(double newPriceAmount) {
        Price newPrice = new Price(newPriceAmount);
        updatePromoState(newPrice);
        storedPrice = newPrice;
    }

    public boolean isPromoActive() {
        return promoExpiration != null && promoExpiration.isAfterNow();
    }

    private void updatePromoState(Price newPrice) {
        if (storedPrice != null) {
            if (isPromoActive() && !shouldContinuePromo(newPrice)) {
                clearCurrentPromo();
            } else if (shouldStartNewPromo(newPrice)) {
                startNewPromo(newPrice);
            }
        }
    }

    private void clearCurrentPromo() {
        promoExpiration = null;
    }

    private void startNewPromo(Price newPrice) {
        promoExpiration = newPrice.startTime
                                   .plusDays(MAX_PENCIL_PROMO_DURATION_DAYS)
                                   .plusMillis(1);
        prePromoPrice = storedPrice.amount;
    }

    private boolean shouldContinuePromo(Price newPrice) {
        return newPrice.amount <= storedPrice.amount &&
                isRedPencilPromoPriceChange(prePromoPrice, newPrice.amount);
    }

    private boolean shouldStartNewPromo(Price newPrice) {
        return wasPreviousPriceStable() &&
                isRedPencilPromoPriceChange(storedPrice.amount, newPrice.amount);
    }

    private boolean wasPreviousPriceStable() {
        DateTime timeForStability = storedPrice.startTime.plusDays(DAYS_FOR_STABLE_PRICE);
        return timeForStability.isBeforeNow() || timeForStability.isEqualNow();
    }

    private boolean isRedPencilPromoPriceChange(double originalPriceAmount, double newPriceAmount) {
        double percentReduced = getPercentPriceReduced(originalPriceAmount, newPriceAmount);
        return percentReduced >= PENCIL_PROMO_MIN_REDUCTION_PERCENT
                && percentReduced <= PENCIL_PROMO_MAX_REDUCTION_PERCENT;
    }

    private double getPercentPriceReduced(double originalPriceAmount, double newPriceAmount) {
        BigDecimal bigOriginalPrice = BigDecimal.valueOf(originalPriceAmount);
        BigDecimal bigNewPrice = BigDecimal.valueOf(newPriceAmount);
        BigDecimal percentOfCurrent = bigNewPrice.divide(bigOriginalPrice,
                                                         2,
                                                         BigDecimal.ROUND_HALF_UP);
        return BigDecimal.ONE.subtract(percentOfCurrent).doubleValue();
    }

    private static class Price {
        private final double amount;
        private final DateTime startTime;

        private Price(double amount) {
            this.amount = amount;
            this.startTime = DateTime.now();
        }
    }
}
