import org.joda.time.DateTimeUtils;
import org.joda.time.Days;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class RedPencilPromoCheckerTest {

    private static final double ALMOST_NOTHING = 0.1;
    private static final long THIRTY_DAYS = Days.days(30).toStandardDuration().getMillis();
    private static final long MIN_PRICE_STABLE_DURATION = THIRTY_DAYS;
    private static final long MAX_RED_PENCIL_PROMO_DURATION = THIRTY_DAYS;

    private RedPencilPromoChecker subject;

    @Before
    public void setUp() {
        subject = new RedPencilPromoChecker();
    }

    @Test
    public void uninitializedCheckerIsNotActive() {
        assertFalse(subject.isPromoActive());
    }

    @Test
    public void priceDropOf4PercentIsNotPromoEvenIfStable() {
        initializeWithPriceDrop(0.04, true);
        assertFalse(subject.isPromoActive());
    }

    @Test
    public void priceDropOf5PercentIsActivePromoIfStable() {
        initializeWithPriceDrop(0.05, true);
        assertTrue(subject.isPromoActive());
    }

    @Test
    public void priceDropOf5PercentIsNotActivePromoIfUnstable() {
        initializeWithPriceDrop(0.05, false);
        assertFalse(subject.isPromoActive());
    }

    @Test
    public void priceDropOf30PercentIsActivePromoIfStable() {
        initializeWithPriceDrop(0.30, true);
        assertTrue(subject.isPromoActive());
    }

    @Test
    public void priceDropOf30PercentIsNotActivePromoIfUnstable() {
        initializeWithPriceDrop(0.30, false);
        assertFalse(subject.isPromoActive());
    }

    @Test
    public void priceDropOf31PercentIsNotActivePromoEvenIfStable() {
        initializeWithPriceDrop(0.31, true);
        assertFalse(subject.isPromoActive());
    }

    @Test
    public void promoEndsAfterMaxPromoDuration() {
        initializeWithPromoThatHasRunFor(1 + MAX_RED_PENCIL_PROMO_DURATION);
        assertFalse(subject.isPromoActive());
    }

    @Test
    public void promoContinuesForMaxPromoDuration() {
        initializeWithPromoThatHasRunFor(MAX_RED_PENCIL_PROMO_DURATION);
        assertTrue(subject.isPromoActive());
    }

    @Test
    public void withinRangePriceReductionDuringPromoDoesNotExtendPromo() {
        double promoPrice = initializeWithPromoThatHasRunFor(MAX_RED_PENCIL_PROMO_DURATION);
        subject.notifyOfPriceUpdate(promoPrice - ALMOST_NOTHING);
        assertTrue(subject.isPromoActive());
        incrementTime(1);
        assertFalse(subject.isPromoActive());
    }

    @Test
    public void withinRangePriceReductionDuringPromoDoesNotShortenPromo() {
        double promoPrice = initializeWithPromoThatHasRunFor(MAX_RED_PENCIL_PROMO_DURATION - 1);
        subject.notifyOfPriceUpdate(promoPrice - ALMOST_NOTHING);
        assertTrue(subject.isPromoActive());
        incrementTime(1);
        assertTrue(subject.isPromoActive());
    }

    @Test
    public void previousPromoDoesNotPreventFuturePromo() {
        initializeWithPromoThatHasRunFor(MAX_RED_PENCIL_PROMO_DURATION + 1);
        subject.notifyOfPriceUpdate((double) 100);
        incrementTime(MIN_PRICE_STABLE_DURATION);
        subject.notifyOfPriceUpdate((double) 80);
        assertTrue(subject.isPromoActive());
    }

    @Test
    public void priceIncreaseStopsPromo() {
        double promoPrice = initializeWithPromoThatHasRunFor(0);
        assertTrue(subject.isPromoActive());
        subject.notifyOfPriceUpdate(promoPrice + ALMOST_NOTHING);
        assertFalse(subject.isPromoActive());
    }

    @Test
    public void priceDecreaseDuringPromoThatPushesReductionBelowMaxEndsPromo() {
        setTime(0);
        subject.notifyOfPriceUpdate((double) 100);
        incrementTime(MIN_PRICE_STABLE_DURATION + 1);
        subject.notifyOfPriceUpdate((double) 70);
        assertTrue(subject.isPromoActive());
        subject.notifyOfPriceUpdate((double) 65);
        assertFalse(subject.isPromoActive());
    }

    /*
     * helper methods
     */

    private double initializeWithPromoThatHasRunFor(long promoDuration) {
        setTime(0);
        subject.notifyOfPriceUpdate((double) 100);

        incrementTime(MIN_PRICE_STABLE_DURATION);
        int promoPrice = 80;
        subject.notifyOfPriceUpdate((double) promoPrice);
        assertTrue(subject.isPromoActive());

        incrementTime(promoDuration);
        return promoPrice;
    }

    private void initializeWithPriceDrop(double percentReduction, boolean isPreviousPriceStable) {
        setTime(0);
        double initialPrice = 100;
        subject.notifyOfPriceUpdate(initialPrice);

        long lengthOfPriceStability = isPreviousPriceStable ?
                                      MIN_PRICE_STABLE_DURATION :
                                      MIN_PRICE_STABLE_DURATION - 1;
        incrementTime(lengthOfPriceStability);
        double newPrice = initialPrice * (1.0 - percentReduction);
        subject.notifyOfPriceUpdate(newPrice);
    }

    private void setTime(long millis) {
        DateTimeUtils.setCurrentMillisFixed(millis);
    }

    private void incrementTime(long millis) {
        DateTimeUtils.setCurrentMillisFixed(DateTimeUtils.currentTimeMillis() + millis);
    }
}
