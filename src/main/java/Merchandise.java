import org.joda.time.DateTime;

import java.math.BigDecimal;

public class Merchandise {

    private static final Double PENCIL_PROMOTION_MIN_REDUCTION_PERCENT = 0.05;
    private static final Double PENCIL_PROMOTION_MAX_REDUCTION_PERCENT = 0.30;
    private static final int DAYS_FOR_STABLE_PRICE = 30;
    private static final Double UNINITIALIZED_PRICE = -1.0;

    private double previousPrice;
    private DateTime previousPriceTime;
    private double price;
    private DateTime priceTime;

    public Merchandise(double price) {
        this.price = price;
        this.priceTime = DateTime.now();
        this.previousPrice = UNINITIALIZED_PRICE;
    }

    public double getPrice() {
        return price;
    }

    public boolean isRedPencilPromotion() {
        if (previousPrice == UNINITIALIZED_PRICE || !isPreviousPriceStable()) {
            return false;
        } else {
            double percentReduced = getPercentPriceReduced();
            return percentReduced >= PENCIL_PROMOTION_MIN_REDUCTION_PERCENT &&
                    percentReduced <= PENCIL_PROMOTION_MAX_REDUCTION_PERCENT;
        }
    }

    private double getPercentPriceReduced() {
        BigDecimal bigPreviousPrice = BigDecimal.valueOf(previousPrice);
        BigDecimal bigPrice = BigDecimal.valueOf(price);
        BigDecimal percentOfPrevious = bigPrice.divide(bigPreviousPrice,
                                                       2,
                                                       BigDecimal.ROUND_HALF_UP);
        return BigDecimal.ONE.subtract(percentOfPrevious).doubleValue();
    }

    private boolean isPreviousPriceStable() {
        return previousPriceTime != null &&
                !previousPriceTime.plusDays(DAYS_FOR_STABLE_PRICE)
                                  .isAfter(priceTime.toInstant());
    }

    public void setPrice(double newPrice) {
        previousPrice = price;
        previousPriceTime = priceTime;
        price = newPrice;
        priceTime = DateTime.now();
    }
}
