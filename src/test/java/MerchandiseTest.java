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
}
