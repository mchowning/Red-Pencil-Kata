import edu.umd.cs.findbugs.annotations.NonNull;

public class Merchandise {

    private final RedPencilPromoChecker promoChecker;
    private double price;

    public Merchandise(double price, @NonNull RedPencilPromoChecker promoChecker) {
        this.promoChecker = promoChecker;
        setPrice(price);
    }

    public double getPrice() {
        return price;
    }

    public boolean isRedPencilPromo() {
        return promoChecker.isPromoActive();
    }

    public void setPrice(double price) {
        this.price = price;
        promoChecker.notifyOfPriceUpdate(price);
    }
}
