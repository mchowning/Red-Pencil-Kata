import org.joda.time.DateTime;

import java.math.BigDecimal;

import edu.umd.cs.findbugs.annotations.Nullable;

public class RedPencilPromoChecker {

    private static final Double PENCIL_PROMO_MIN_REDUCTION_PERCENT = 0.05;
    private static final Double PENCIL_PROMO_MAX_REDUCTION_PERCENT = 0.30;
    private static final int DAYS_FOR_STABLE_PRICE = 30;
    private static final int MAX_PENCIL_PROMO_DURATION_DAYS = 30;

    @Nullable private Price previousPrice;
    @Nullable private Price currentPrice;
    private double prePromoPrice;
    @Nullable private DateTime promoExpiration;

    public void notifyOfPriceUpdate(double newPriceAmount) {
        previousPrice = currentPrice;
        currentPrice = new Price(newPriceAmount);
        updatePromoState();
    }

    public boolean isPromoActive() {
        return promoExpiration != null && promoExpiration.isAfterNow();
    }

    private void updatePromoState() {
        if (isPromoActive() && !shouldContinuePromo()) {
            clearCurrentPromo();
        } else if (shouldStartNewPromo()) {
            promoExpiration = currentPrice.startTime
                                          .plusDays(MAX_PENCIL_PROMO_DURATION_DAYS)
                                          .plusMillis(1);
            prePromoPrice = previousPrice.amount;
        }
    }

    private void clearCurrentPromo() {
        promoExpiration = null;
    }

    private boolean shouldContinuePromo() {
        return currentPrice != null &&
                previousPrice != null &&
                currentPrice.amount <= previousPrice.amount &&
                isRedPencilPromoPriceChange(prePromoPrice, currentPrice.amount);
    }

    private boolean shouldStartNewPromo() {
        return currentPrice != null &&
                previousPrice != null &&
                wasPreviousPriceStable() &&
                isRedPencilPromoPriceChange(previousPrice.amount, currentPrice.amount);
    }

    private boolean wasPreviousPriceStable() {
        DateTime timeForStability = previousPrice.startTime.plusDays(DAYS_FOR_STABLE_PRICE);
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
