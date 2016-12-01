import org.joda.time.DateTimeUtils;
import org.joda.time.Days;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class MerchandiseTest {

    private static final double INITIAL_PRICE = 100;
    private static final double DOUBLE_TEST_DELTA = 0.001;
    private static final long THIRTY_DAYS = Days.days(30).toStandardDuration().getMillis();

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
        setPriceDrop(0.04, true);
        assertFalse(subject.isRedPencilPromotion());
    }

    @Test
    public void priceDropOf5PercentIsRedPencilPromotionIfStable() {
        setPriceDrop(0.05, true);
        assertTrue(subject.isRedPencilPromotion());
    }

    @Test
    public void priceDropOf5PercentIsNotRedPencilPromotionIfUnstable() {
        setPriceDrop(0.05, false);
        assertFalse(subject.isRedPencilPromotion());
    }

    @Test
    public void priceDropOf30PercentIsRedPencilPromotionIfStable() {
        setPriceDrop(0.30, true);
        assertTrue(subject.isRedPencilPromotion());
    }

    @Test
    public void priceDropOf30PercentIsNotRedPencilPromotionIfUnstable() {
        setPriceDrop(0.30, false);
        assertFalse(subject.isRedPencilPromotion());
    }

    @Test
    public void priceDropOf31PercentIsNotRedPencilPromotionEvenIfStable() {
        setPriceDrop(0.31, true);
        assertFalse(subject.isRedPencilPromotion());
    }

    /*
     * helper methods
     */

    private void setPriceDrop(double percentReduction, boolean isPreviousPriceStable) {
        DateTimeUtils.setCurrentMillisFixed(0);
        subject.setPrice(100);
        long lengthOfPriceStability = isPreviousPriceStable ?
                                          THIRTY_DAYS :
                                          THIRTY_DAYS - 1;
        DateTimeUtils.setCurrentMillisFixed(lengthOfPriceStability);
        double newPrice = 100 * (1.0 - percentReduction);
        subject.setPrice(newPrice);
    }
}
