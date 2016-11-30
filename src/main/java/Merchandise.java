public class Merchandise {

    private double price;

    public Merchandise(double price) {
        this.price = price;
    }

    public double getPrice() {
        return price;
    }

    public boolean isRedPencilPromotion() {
        return false;
    }

    public void setPrice(double price) {
        this.price = price;
    }
}
