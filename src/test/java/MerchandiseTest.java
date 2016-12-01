import org.joda.time.DateTimeUtils;
import org.joda.time.Days;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class MerchandiseTest {

    private static final double INITIAL_PRICE = 100;
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
    public void initializedPriceIsNotRedPencilPromotion() {
        boolean isRedPencilPromotion = subject.isRedPencilPromotion();
        assertFalse(isRedPencilPromotion);
    }

    @Test
    public void canUpdatePrice() {
        subject.setPrice(95);
        assertEquals(95, subject.getPrice(), DOUBLE_TEST_DELTA);
    }

    @Test
    public void reSettingInitialPriceIsNotPencilPromotion() {
        subject.setPrice(INITIAL_PRICE);
        assertFalse(subject.isRedPencilPromotion());
    }

    @Test
    public void priceDropOf4PercentIsNotRedPencilPromotionEvenIfStable() {
        initializeWithPriceDrop(0.04, true);
        assertFalse(subject.isRedPencilPromotion());
    }

    @Test
    public void priceDropOf5PercentIsRedPencilPromotionIfStable() {
        initializeWithPriceDrop(0.05, true);
        assertTrue(subject.isRedPencilPromotion());
    }

    @Test
    public void priceDropOf5PercentIsNotRedPencilPromotionIfUnstable() {
        initializeWithPriceDrop(0.05, false);
        assertFalse(subject.isRedPencilPromotion());
    }

    @Test
    public void priceDropOf30PercentIsRedPencilPromotionIfStable() {
        initializeWithPriceDrop(0.30, true);
        assertTrue(subject.isRedPencilPromotion());
    }

    @Test
    public void priceDropOf30PercentIsNotRedPencilPromotionIfUnstable() {
        initializeWithPriceDrop(0.30, false);
        assertFalse(subject.isRedPencilPromotion());
    }

    @Test
    public void priceDropOf31PercentIsNotRedPencilPromotionEvenIfStable() {
        initializeWithPriceDrop(0.31, true);
        assertFalse(subject.isRedPencilPromotion());
    }

    @Test
    public void redPencilPromotionEndsAfterMaxPromoDuration() {
        initializeWithRedPencilPromotionThatHasRunFor(1 + MAX_RED_PENCIL_PROMO_DURATION);
        assertFalse(subject.isRedPencilPromotion());
    }

    @Test
    public void redPencilPromotionContinuesForMaxPromoDuration() {
        initializeWithRedPencilPromotionThatHasRunFor(MAX_RED_PENCIL_PROMO_DURATION);
        assertTrue(subject.isRedPencilPromotion());
    }

    /*
     * helper methods
     */

    private void initializeWithRedPencilPromotionThatHasRunFor(long promotionDuration) {
        setTime(0);
        subject.setPrice(100);

        incrementTime(MIN_PRICE_STABLE_DURATION);
        subject.setPrice(80);
        assertTrue(subject.isRedPencilPromotion());

        incrementTime(promotionDuration);
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
