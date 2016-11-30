import org.junit.Test;

public class MerchandiseTest {

    @Test
    public void initializeMerchandiseWithPrice() {
        Merchandise merchandise = new Merchandise(100);
        int price = merchandise.getPrice();
    }
}
