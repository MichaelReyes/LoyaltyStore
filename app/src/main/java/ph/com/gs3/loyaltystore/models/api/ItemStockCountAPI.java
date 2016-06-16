package ph.com.gs3.loyaltystore.models.api;

import java.util.List;

import ph.com.gs3.loyaltystore.models.api.objects.UploadAPIResponse;
import ph.com.gs3.loyaltystore.models.sqlite.dao.ItemStockCount;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

/**
 * Created by Ervinne Sodusta on 2/15/2016.
 */
public interface ItemStockCountAPI {

    @POST("pos/item-stock-count-upload")
    Call<UploadAPIResponse> uploadItemStockCountToFormalistics(@Body List<ItemStockCount> itemStockCountList);

}
