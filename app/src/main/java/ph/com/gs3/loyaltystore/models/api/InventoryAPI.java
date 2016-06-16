package ph.com.gs3.loyaltystore.models.api;

import java.util.List;

import ph.com.gs3.loyaltystore.models.api.objects.FormalisticsAPIResponse;
import ph.com.gs3.loyaltystore.models.sqlite.dao.ItemInventory;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

/**
 * Created by Michael Reyes on 5/4/2016.
 */
public interface InventoryAPI {

    @POST("pos/update-inventory")
    Call<FormalisticsAPIResponse> updateInventoryInFormalistics(@Body List<ItemInventory> itemInventoryList);

}
