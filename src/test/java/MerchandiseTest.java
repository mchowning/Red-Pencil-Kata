import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

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
    public void priceDropOf5PercentCreatesRedPencilPromotion() {
        double updatedPrice = INITIAL_PRICE * 0.95;
        subject.setPrice(updatedPrice);
    }
}
