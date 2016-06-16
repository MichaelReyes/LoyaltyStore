package ph.com.gs3.loyaltystore.models.api.objects;

import java.util.Date;

import ph.com.gs3.loyaltystore.models.sqlite.dao.ProductForDelivery;

/**
 * Created by Ervinne Sodusta on 2/26/2016.
 */
public class ProductForDeliveryResponse {

    public long id;
    public long product_id;
    public String TrackNo;
    public String Status;
    public Date DateCreated;
    public Date DateUpdated;
    public String pick_bom;
    public int txt_stock;
    public String txt_desc;
    public double txt_deliver;
    public String TS;
    public String rad_type;
    public double txt_cash;
    public String pick_branch;
    public long txt_branch_id;

    public ProductForDelivery toProductForDelivery() {

        ProductForDelivery p = new ProductForDelivery();

        p.setId(id);
        p.setTrack_no(TrackNo);
        p.setProduct_id(product_id);
        p.setName(txt_desc);
        p.setQuantity(txt_deliver);
        /*p.setRemaining_quantity(txt_deliver);
        p.setDelivered_quantity(0);*/
        p.setDistribution_type(rad_type);
        p.setCash(txt_cash);
        p.setPick_bom(pick_bom);
        p.setStatus(Status);
        p.setDate_created(DateCreated);
        p.setBranch(pick_branch);
        p.setBranch_id(txt_branch_id);

        return p;

    }

}
