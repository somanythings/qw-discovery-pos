package uk.gov.borderforce.qwseizure;

/**
 * Created by lancep on 19/10/2016.
 */
public class SeizedGoods {
    public  String what;
    public int quantity;
    public String units;
    public SeizedGoods(){};
    public SeizedGoods(String what, int quantity, String units) {
        this.what = what;
        this.quantity = quantity;
        this.units = units;
    }

    @Override
    public String toString() {
        return "SeizedGoods{" +
                "what='" + what + '\'' +
                ", quantity=" + quantity +
                ", units='" + units + '\'' +
                '}';
    }

    public SeizedGoods copyOnQuantity(int newVal) {
        return new SeizedGoods(this.what, newVal, this.units);
    }

    public SeizedGoods copyOnUnits(String units) {
        return new SeizedGoods(this.what, this.quantity, units);
    }
}
