package ph.com.gs3.loyaltystore.models.api;

import ph.com.gs3.loyaltystore.models.api.objects.ReturnsUploadRequest;
import ph.com.gs3.loyaltystore.models.api.objects.SyncReturnsAPIResponse;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

/**
 * Created by Michael Reyes on 5/4/2016.
 */
public interface ReturnsAPI {

    @POST("pos/returns-upload")
    Call<SyncReturnsAPIResponse> uploadReturnsToFormalistics(@Body ReturnsUploadRequest returnsUploadRequest);

}
