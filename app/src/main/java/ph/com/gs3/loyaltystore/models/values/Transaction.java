package ph.com.gs3.loyaltystore.models.values;

import java.io.Serializable;

/**
 * Created by Michael Reyes on 27/01/2016.
 */
public class Transaction implements Serializable{

    private String productCode;
    private String name;
    private float cost;
    private int quantity;
    private float total;

    public String getProductCode() {
        return productCode;
    }

    public void setProductCode(String productCode) {
        this.productCode = productCode;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public float getCost() {
        return cost;
    }

    public void setCost(float cost) {
        this.cost = cost;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public float getTotal() {
        return total;
    }

    public void setTotal(float total) {
        this.total = total;
    }
}
