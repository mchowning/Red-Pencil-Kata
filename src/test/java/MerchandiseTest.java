import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class MerchandiseTest {

    private static final double INITIAL_PRICE = 100;
    private static final double DOUBLE_TEST_DELTA = 0.001;

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
    public void priceDropOf4PercentIsNotRedPencilPromotion() {
        double updatedPrice = INITIAL_PRICE * 0.96;
        subject.setPrice(updatedPrice);
        assertFalse(subject.isRedPencilPromotion());
    }

    @Test
    public void priceDropOf5PercentIsRedPencilPromotion() {
        double updatedPrice = INITIAL_PRICE * 0.95;
        subject.setPrice(updatedPrice);
        assertTrue(subject.isRedPencilPromotion());
    }

    @Test
    public void priceDropOf30PercentIsRedPencilPromotion() {
        double updatedPrice = INITIAL_PRICE * 0.70;
        subject.setPrice(updatedPrice);
        assertTrue(subject.isRedPencilPromotion());
    }

    @Test
    public void priceDropOf31PercentIsNotRedPencilPromotion() {
        double updatedPrice = INITIAL_PRICE * 0.69;
        subject.setPrice(updatedPrice);
        assertFalse(subject.isRedPencilPromotion());
    }
}
