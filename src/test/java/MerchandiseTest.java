import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class MerchandiseTest {

    @Test
    public void initializeMerchandiseWithPrice() {
        Merchandise merchandise = new Merchandise(100);
        int price = merchandise.getPrice();
        assertEquals(100, price);
    }
}
