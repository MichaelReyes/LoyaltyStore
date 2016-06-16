package ph.com.gs3.loyaltystore.adapters.objects;

import ph.com.gs3.loyaltystore.models.sqlite.dao.ItemInventory;

/**
 * Created by Bryan-PC on 10/05/2016.
 */
public class InventoryRowItem {

    private ItemInventory item1;
    private ItemInventory item2;

    public ItemInventory getItem1() {
        return item1;
    }

    public void setItem1(ItemInventory item1) {
        this.item1 = item1;
    }

    public ItemInventory getItem2() {
        return item2;
    }

    public void setItem2(ItemInventory item2) {
        this.item2 = item2;
    }

}
