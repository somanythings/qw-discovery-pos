package uk.gov.borderforce.qwseizure;

/**
 * Created by lancep on 19/10/2016.
 */
public class SeizedGoods {
    public final String what;
    public final String quantity;

    public SeizedGoods(String what, String quantity) {
        this.what = what;
        this.quantity = quantity;
    }

    @Override
    public String toString() {
        return "SeizedGoods{" +
                "what='" + what + '\'' +
                ", quantity='" + quantity + '\'' +
                '}';
    }
}
