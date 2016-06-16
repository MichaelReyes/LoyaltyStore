package ph.com.gs3.loyaltystore.models.api;

import ph.com.gs3.loyaltystore.models.api.objects.FormalisticsAPIResponse;
import ph.com.gs3.loyaltystore.models.api.objects.FormalisticsLoginResponse;
import ph.com.gs3.loyaltystore.models.api.objects.UserDeviceLogRequest;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

/**
 * Created by Ervinne Sodusta on 2/3/2016.
 */
public interface UsersAPI {

    @FormUrlEncoded
    @POST("/api/login")
    Call<FormalisticsLoginResponse> formalisticsLogin(@Field("email") String email, @Field("password") String password);

    @POST("/pos/user-device-login")
    Call<FormalisticsAPIResponse> logUserTimeIn(@Body UserDeviceLogRequest userDeviceLogRequest);

    @POST("/pos/user-device-logout")
    Call<FormalisticsAPIResponse> logUserTimeOut(@Body UserDeviceLogRequest userDeviceLogRequest);
}
