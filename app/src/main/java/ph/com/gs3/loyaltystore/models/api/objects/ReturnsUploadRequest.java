package ph.com.gs3.loyaltystore.models.api.objects;

import com.google.gson.annotations.SerializedName;

import java.util.List;

import ph.com.gs3.loyaltystore.models.sqlite.dao.CashReturn;
import ph.com.gs3.loyaltystore.models.sqlite.dao.ItemReturn;


/**
 * Created by Bryan-PC on 29/04/2016.
 */
public class ReturnsUploadRequest {

    @SerializedName("item_returns")
    public List<ItemReturn> itemReturns;

    @SerializedName("cash_returns")
    public List<CashReturn> cashReturns;

}
