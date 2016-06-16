package ph.com.gs3.loyaltystore.models.api;

import java.util.List;

import ph.com.gs3.loyaltystore.models.api.objects.FormalisticsAPIResponse;
import ph.com.gs3.loyaltystore.models.sqlite.dao.Store;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

/**
 * Created by Ervinne Sodusta on 2/3/2016.
 */
public interface StoreAPI {

    @GET("/stores/nodevice")
    Call<List<Store>> getStoresWithNoDeviceID();

    @FormUrlEncoded
    @POST("/stores/{id}/device/register")
    Call<String> registerStore(@Path("id") String id,
                               @Field("device_id") String device_id);

    @GET("pos/available-stores-for-registration")
    Call<List<Store>> getAvailableStoresForRegistration();


    @FormUrlEncoded
    @POST("/pos/register-store")
    Call<FormalisticsAPIResponse> registerStoreDevice(@Field("store_id") String storeId,
                                                      @Field("device_id") String deviceId);

}
