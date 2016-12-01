import java.math.BigDecimal;

public class Merchandise {

    private static final Double PENCIL_PROMOTION_MIN_REDUCTION_PERCENT = 0.05;
    private static final Double PENCIL_PROMOTION_MAX_REDUCTION_PERCENT = 0.30;
    private static final Double UNINITIALIZED_PRICE = -1.0;

    private double previousPrice;
    private double price;

    public Merchandise(double price) {
        this.price = price;
        this.previousPrice = UNINITIALIZED_PRICE;
    }

    public double getPrice() {
        return price;
    }

    public boolean isRedPencilPromotion() {
        if (previousPrice == UNINITIALIZED_PRICE) {
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

    public void setPrice(double newPrice) {
        previousPrice = price;
        price = newPrice;
    }
}
