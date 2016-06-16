package ph.com.gs3.loyaltystore.models.api;

import java.util.List;

import okhttp3.ResponseBody;
import ph.com.gs3.loyaltystore.models.api.objects.AcceptRejectProductRequest;
import ph.com.gs3.loyaltystore.models.api.objects.FormalisticsAPIResponse;
import ph.com.gs3.loyaltystore.models.api.objects.ProductForDeliveryResponse;
import ph.com.gs3.loyaltystore.models.sqlite.dao.ProductForDelivery;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

/**
 * Created by Ervinne Sodusta on 2/26/2016.
 */
public interface ProductsForDeliveryAPI {

    @FormUrlEncoded
    @POST("pos/products-for-delivery")
    Call<List<ProductForDeliveryResponse>> getProducts(@Field("store_id") long storeId);

    @POST("pos/accept-products")
    Call<ResponseBody> acceptProducts();

    @POST("pos/reject-products")
    Call<ResponseBody> rejectProducts();

    @FormUrlEncoded
    @POST("pos/confirm-products-for-loading")
    Call<FormalisticsAPIResponse> confirmProductsForLoaing(@Field("request_data") String requestData);

    @POST("api/update-request")
    Call<FormalisticsAPIResponse> update(@Body AcceptRejectProductRequest request);

    @POST("pos/confirm-products-for-delivery")
    Call<FormalisticsAPIResponse> confirmProductsForDelivery(@Body List<ProductForDelivery> productForDeliveryList);

}
