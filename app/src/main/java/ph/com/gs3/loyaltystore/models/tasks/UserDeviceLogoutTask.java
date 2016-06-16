package ph.com.gs3.loyaltystore.models.tasks;

import android.content.Context;
import android.os.AsyncTask;

import java.io.IOException;

import okhttp3.logging.HttpLoggingInterceptor;
import ph.com.gs3.loyaltystore.models.User;
import ph.com.gs3.loyaltystore.models.api.ServiceGenerator;
import ph.com.gs3.loyaltystore.models.api.UsersAPI;
import ph.com.gs3.loyaltystore.models.api.objects.FormalisticsAPIResponse;
import ph.com.gs3.loyaltystore.models.api.objects.UserDeviceLogRequest;
import ph.com.gs3.loyaltystore.models.values.Retailer;
import retrofit2.Call;
import retrofit2.Response;

public class UserDeviceLogoutTask extends AsyncTask<Void, Void, Void> {

    public static final String TAG = UserDeviceLogoutTask.class.getSimpleName();

    private Context context;
    private UserDeviceLogoutTaskListener listener;

    public UserDeviceLogoutTask(Context context, UserDeviceLogoutTaskListener listener) {
        this.context = context;
        this.listener = listener;
    }

    @Override
    protected Void doInBackground(Void... params) {

        User currentUser = User.getSavedUser(context);
        Retailer retailer = Retailer.getDeviceRetailerFromSharedPreferences(context);

        UserDeviceLogRequest userDeviceLogRequest = new UserDeviceLogRequest();
        userDeviceLogRequest.user_id = currentUser.getId();
        userDeviceLogRequest.user_name = currentUser.getName();
        userDeviceLogRequest.branch_id = retailer.getStoreId();
        userDeviceLogRequest.branch_name = retailer.getStoreName();

        ServiceGenerator serviceGenerator = new ServiceGenerator(context, currentUser.getFormalisticsServer(), HttpLoggingInterceptor.Level.BODY);
        UsersAPI usersAPI = serviceGenerator.createService(UsersAPI.class);

        Call<FormalisticsAPIResponse> call = usersAPI.logUserTimeOut(userDeviceLogRequest);

        Response response = null;
        try {
            response = call.execute();

            if (response != null) {
                FormalisticsAPIResponse apiResponse = (FormalisticsAPIResponse) response.body();

                if("SUCCESS".equals(apiResponse.status)){
                    listener.onUserLoggedOut();
                }else{
                    listener.onUserLogoutFailed();
                }

            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        listener.onUserLogoutTaskDone();
    }

    public interface UserDeviceLogoutTaskListener {

        void onUserLoggedOut();

        void onUserLogoutFailed();

        void onUserLogoutTaskDone();

    }

}