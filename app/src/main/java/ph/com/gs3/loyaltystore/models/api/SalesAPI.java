package ph.com.gs3.loyaltystore.models.api;

import ph.com.gs3.loyaltystore.models.api.objects.SalesUploadRequest;
import ph.com.gs3.loyaltystore.models.api.objects.SalesUploadResponse;
import ph.com.gs3.loyaltystore.models.api.objects.SyncSalesAPIResponse;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

/**
 * Created by Ervinne Sodusta on 2/15/2016.
 */
public interface SalesAPI {

    @POST("sales/upload")
    Call<SalesUploadResponse> uploadSales(@Body SalesUploadRequest salesUploadRequest);

    @POST("pos/sales-upload")
    Call<SyncSalesAPIResponse> uploadSalesToFormalistics(@Body SalesUploadRequest salesUploadRequest);

}
