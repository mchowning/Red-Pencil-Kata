import org.joda.time.DateTimeUtils;
import org.joda.time.Days;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class MerchandiseTest {

    private static final double INITIAL_PRICE = 100;
    private static final double ALMOST_NOTHING = 0.1;
    private static final double DOUBLE_TEST_DELTA = 0.001;
    private static final long THIRTY_DAYS = Days.days(30).toStandardDuration().getMillis();
    private static final long MIN_PRICE_STABLE_DURATION = THIRTY_DAYS;
    private static final long MAX_RED_PENCIL_PROMO_DURATION = THIRTY_DAYS;

    private Merchandise subject;

    @Before
    public void setUp() {
        subject = new Merchandise(INITIAL_PRICE);
    }

    @Test
    public void initializedPriceIsReturned() {
        assertEquals(INITIAL_PRICE, subject.getPrice(), DOUBLE_TEST_DELTA);
    }

    @Test
    public void initializedPriceIsNotRedPencilPromo() {
        boolean isRedPencilPromo = subject.isRedPencilPromo();
        assertFalse(isRedPencilPromo);
    }

    @Test
    public void canUpdatePrice() {
        subject.setPrice(95);
        assertEquals(95, subject.getPrice(), DOUBLE_TEST_DELTA);
    }

    @Test
    public void reSettingInitialPriceIsNotPencilPromo() {
        subject.setPrice(INITIAL_PRICE);
        assertFalse(subject.isRedPencilPromo());
    }

    @Test
    public void priceDropOf4PercentIsNotRedPencilPromoEvenIfStable() {
        initializeWithPriceDrop(0.04, true);
        assertFalse(subject.isRedPencilPromo());
    }

    @Test
    public void priceDropOf5PercentIsRedPencilPromoIfStable() {
        initializeWithPriceDrop(0.05, true);
        assertTrue(subject.isRedPencilPromo());
    }

    @Test
    public void priceDropOf5PercentIsNotRedPencilPromoIfUnstable() {
        initializeWithPriceDrop(0.05, false);
        assertFalse(subject.isRedPencilPromo());
    }

    @Test
    public void priceDropOf30PercentIsRedPencilPromoIfStable() {
        initializeWithPriceDrop(0.30, true);
        assertTrue(subject.isRedPencilPromo());
    }

    @Test
    public void priceDropOf30PercentIsNotRedPencilPromoIfUnstable() {
        initializeWithPriceDrop(0.30, false);
        assertFalse(subject.isRedPencilPromo());
    }

    @Test
    public void priceDropOf31PercentIsNotRedPencilPromoEvenIfStable() {
        initializeWithPriceDrop(0.31, true);
        assertFalse(subject.isRedPencilPromo());
    }

    @Test
    public void redPencilPromoEndsAfterMaxPromoDuration() {
        initializeWithRedPencilPromoThatHasRunFor(1 + MAX_RED_PENCIL_PROMO_DURATION);
        assertFalse(subject.isRedPencilPromo());
    }

    @Test
    public void redPencilPromoContinuesForMaxPromoDuration() {
        initializeWithRedPencilPromoThatHasRunFor(MAX_RED_PENCIL_PROMO_DURATION);
        assertTrue(subject.isRedPencilPromo());
    }

    @Test
    public void withinRangePriceReductionDuringRedPencilPromoDoesNotExtendPromo() {
        double promoPrice = initializeWithRedPencilPromoThatHasRunFor(MAX_RED_PENCIL_PROMO_DURATION);
        subject.setPrice(promoPrice - ALMOST_NOTHING);
        assertTrue(subject.isRedPencilPromo());
        incrementTime(1);
        assertFalse(subject.isRedPencilPromo());
    }

    @Test
    public void withinRangePriceReductionDuringRedPencilPromoDoesNotShortenPromo() {
        double promoPrice = initializeWithRedPencilPromoThatHasRunFor(MAX_RED_PENCIL_PROMO_DURATION - 1);
        subject.setPrice(promoPrice - ALMOST_NOTHING);
        assertTrue(subject.isRedPencilPromo());
        incrementTime(1);
        assertTrue(subject.isRedPencilPromo());
    }

    /*
     * helper methods
     */

    private double initializeWithRedPencilPromoThatHasRunFor(long promoDuration) {
        setTime(0);
        subject.setPrice(100);

        incrementTime(MIN_PRICE_STABLE_DURATION);
        int promoPrice = 80;
        subject.setPrice(promoPrice);
        assertTrue(subject.isRedPencilPromo());

        incrementTime(promoDuration);
        return promoPrice;
    }

    private void initializeWithPriceDrop(double percentReduction, boolean isPreviousPriceStable) {
        setTime(0);
        subject.setPrice(100);

        long lengthOfPriceStability = isPreviousPriceStable ?
                                        MIN_PRICE_STABLE_DURATION :
                                        MIN_PRICE_STABLE_DURATION - 1;
        incrementTime(lengthOfPriceStability);
        double newPrice = subject.getPrice() * (1.0 - percentReduction);
        subject.setPrice(newPrice);
    }

    private void setTime(long millis) {
        DateTimeUtils.setCurrentMillisFixed(millis);
    }

    private void incrementTime(long millis) {
        DateTimeUtils.setCurrentMillisFixed(DateTimeUtils.currentTimeMillis() + millis);
    }
}
