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
    private RedPencilPromoChecker promoChecker;

    @Before
    public void setUp() {
        promoChecker = mock(RedPencilPromoChecker.class);
        subject = new Merchandise(INITIAL_PRICE, promoChecker);
    }

    @Test
    public void initializedPriceIsReturned() {
        assertEquals(INITIAL_PRICE, subject.getPrice(), DOUBLE_TEST_DELTA);
    }

    @Test
    public void notifiesPromoCheckerOfPriceChanges() {
        verify(promoChecker).notifyOfPriceUpdate(INITIAL_PRICE);
    }

    @Test
    public void returnsIsPromoIfPromoCheckerReturnsTrue() {
        when(promoChecker.isPromoActive()).thenReturn(true);
        assertTrue(subject.isRedPencilPromo());
    }

    @Test
    public void returnsIsNotPromoIfPromoCheckerReturnsFalse() {
        when(promoChecker.isPromoActive()).thenReturn(false);
        assertFalse(subject.isRedPencilPromo());
    }
}
