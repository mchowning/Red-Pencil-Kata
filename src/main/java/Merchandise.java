public class Merchandise {

    private static final Double UNINITIALIZED_PRICE = -1.0;

    private double previousPrice;
    private double price;

    public Merchandise(double price) {
        this.price = price;
        this.previousPrice = UNINITIALIZED_PRICE;
    }

    public double getPrice() {
        return price;
    }

    public boolean isRedPencilPromotion() {
        return previousPrice != UNINITIALIZED_PRICE && previousPrice != price;
    }

    public void setPrice(double newPrice) {
        previousPrice = price;
        price = newPrice;
    }
}
