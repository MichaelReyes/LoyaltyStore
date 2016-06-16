package ph.com.gs3.loyaltystore.models.api.objects;

import com.google.gson.annotations.SerializedName;

import java.util.List;

import ph.com.gs3.loyaltystore.models.sqlite.dao.SalesHasReward;
import ph.com.gs3.loyaltystore.models.sqlite.dao.SalesProduct;

public class SalesUploadRequest {

    @SerializedName("sales")
    public List<SalesRequest> salesList;

    @SerializedName("sales_rewards")
    public List<SalesHasReward> salesHasRewards;

    @SerializedName("sales_products")
    public List<SalesProduct> salesProducts;

}