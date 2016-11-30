import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class MerchandiseTest {

    @Test
    public void initializeMerchandiseWithPrice() {
        int inputPrice = 100;
        Merchandise merchandise = new Merchandise(inputPrice);
        int outputPrice = merchandise.getPrice();
        assertEquals(inputPrice, outputPrice);
    }

    @Test
    public void initializedPriceIsNotRedPencilPromotion() {
        Merchandise merchandise = new Merchandise(100);
        boolean isRedPencilPromotion = merchandise.isRedPencilPromotion();
    }
}
