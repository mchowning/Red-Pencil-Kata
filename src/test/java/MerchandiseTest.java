import org.joda.time.DateTimeUtils;
import org.joda.time.Days;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class MerchandiseTest {

    private static final double INITIAL_PRICE = 100;
    private static final long INITIAL_MILLIS = 0;
    private static final double DOUBLE_TEST_DELTA = 0.001;
    private static final long THIRTY_DAYS = Days.days(30).toStandardDuration().getMillis();

    private Merchandise subject;

    @Before
    public void setUp() {
        DateTimeUtils.setCurrentMillisFixed(INITIAL_MILLIS);
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
    public void priceDropOf4PercentIsNotRedPencilPromotionIfStable() {
        DateTimeUtils.setCurrentMillisFixed(THIRTY_DAYS);
        double updatedPrice = INITIAL_PRICE * 0.96;
        subject.setPrice(updatedPrice);
        assertFalse(subject.isRedPencilPromotion());
    }

    @Test
    public void priceDropOf5PercentIsRedPencilPromotionIfStable() {
        DateTimeUtils.setCurrentMillisFixed(THIRTY_DAYS);
        double updatedPrice = INITIAL_PRICE * 0.95;
        subject.setPrice(updatedPrice);
        assertTrue(subject.isRedPencilPromotion());
    }

    @Test
    public void priceDropOf5PercentIsNotRedPencilPromotionIfUnstable() {
        DateTimeUtils.setCurrentMillisFixed(THIRTY_DAYS - 1);
        double updatedPrice = INITIAL_PRICE * 0.95;
        subject.setPrice(updatedPrice);
        assertFalse(subject.isRedPencilPromotion());
    }

    @Test
    public void priceDropOf30PercentIsRedPencilPromotionIfStable() {
        DateTimeUtils.setCurrentMillisFixed(THIRTY_DAYS);
        double updatedPrice = INITIAL_PRICE * 0.70;
        subject.setPrice(updatedPrice);
        assertTrue(subject.isRedPencilPromotion());
    }

    @Test
    public void priceDropOf31PercentIsNotRedPencilPromotionIfStable() {
        DateTimeUtils.setCurrentMillisFixed(THIRTY_DAYS);
        double updatedPrice = INITIAL_PRICE * 0.69;
        subject.setPrice(updatedPrice);
        assertFalse(subject.isRedPencilPromotion());
    }
}
