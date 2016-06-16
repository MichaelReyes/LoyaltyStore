package ph.com.gs3.loyaltystore.adapters.objects;

import ph.com.gs3.loyaltystore.models.sqlite.dao.Product;

/**
 * Created by Bryan-PC on 10/05/2016.
 */
public class MenuRowItem {

    private Product product1;
    private Product product2;
    private Product product3;

    public Product getProduct1() {
        return product1;
    }

    public void setProduct1(Product product1) {
        this.product1 = product1;
    }

    public Product getProduct2() {
        return product2;
    }

    public void setProduct2(Product product2) {
        this.product2 = product2;
    }

    public Product getProduct3() {
        return product3;
    }

    public void setProduct3(Product product3) {
        this.product3 = product3;
    }
}
