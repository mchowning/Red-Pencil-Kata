import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

public class MerchandiseTest {

    private static final int INITIAL_PRICE = 100;

    private Merchandise subject;

    @Before
    public void setUp() {
        subject = new Merchandise(INITIAL_PRICE);
    }

    @Test
    public void initializedPriceIsReturned() {
        assertEquals(INITIAL_PRICE, subject.getPrice());
    }

    @Test
    public void initializedPriceIsNotRedPencilPromotion() {
        boolean isRedPencilPromotion = subject.isRedPencilPromotion();
        assertFalse(isRedPencilPromotion);
    }

    @Test
    public void canUpdatePrice() {
        subject.setPrice(95);
        assertEquals(95, subject.getPrice());
    }

    @Test
    public void priceDropOf5PercentCreatesRedPencilPromotion() {
        double updatedPrice = INITIAL_PRICE * 0.95;
        subject.setPrice(updatedPrice);
    }
}
