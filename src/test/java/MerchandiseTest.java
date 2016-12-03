import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class MerchandiseTest {

    private static final double INITIAL_PRICE = 100;
    private static final double DOUBLE_TEST_DELTA = 0.001;

    private Merchandise subject;

    @Before
    public void setUp() {
        subject = new Merchandise(INITIAL_PRICE, new RedPencilPromoChecker());
    }

    @Test
    public void initializedPriceIsReturned() {
        assertEquals(INITIAL_PRICE, subject.getPrice(), DOUBLE_TEST_DELTA);
    }

    @Test
    public void notifiesPromoCheckerOfPriceChanges() {
        RedPencilPromoChecker promoChecker = mock(RedPencilPromoChecker.class);
        subject = new Merchandise(INITIAL_PRICE, promoChecker);
        verify(promoChecker).notifyOfPriceUpdate(INITIAL_PRICE);
    }

    @Test
    public void returnsIsPromoIfPromoCheckerReturnsTrue() {
        RedPencilPromoChecker promoChecker = mock(RedPencilPromoChecker.class);
        when(promoChecker.isPromoActive()).thenReturn(true);
        subject = new Merchandise(INITIAL_PRICE, promoChecker);
        assertTrue(subject.isRedPencilPromo());
    }

    @Test
    public void returnsIsNotPromoIfPromoCheckerReturnsFalse() {
        RedPencilPromoChecker promoChecker = mock(RedPencilPromoChecker.class);
        when(promoChecker.isPromoActive()).thenReturn(false);
        subject = new Merchandise(INITIAL_PRICE, promoChecker);
        assertFalse(subject.isRedPencilPromo());
    }
}
