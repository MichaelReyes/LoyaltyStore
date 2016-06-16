package ph.com.gs3.loyaltystore.models.tasks;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.google.gson.Gson;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.logging.HttpLoggingInterceptor;
import ph.com.gs3.loyaltystore.models.User;
import ph.com.gs3.loyaltystore.models.api.ProductsForDeliveryAPI;
import ph.com.gs3.loyaltystore.models.api.ServiceGenerator;
import ph.com.gs3.loyaltystore.models.api.objects.FormalisticsAPIResponse;
import ph.com.gs3.loyaltystore.models.services.ServerSyncService;
import ph.com.gs3.loyaltystore.models.sqlite.dao.ProductForDelivery;
import retrofit2.Call;
import retrofit2.Response;

/**
 * Created by Bryan-PC on 24/03/2016.
 */
public class ConfirmProductsForDeliveryTask extends AsyncTask<Void, Void, Void> {

    public static final String TAG = ConfirmProductsForDeliveryTask.class.getSimpleName();

    private Context context;
    private List<ProductForDelivery> products;
    private AcceptProductsTask.AcceptProductsTaskListener listener;

    public ConfirmProductsForDeliveryTask(Context context, List<ProductForDelivery> products, AcceptProductsTask.AcceptProductsTaskListener listener) {
        this.context = context;
        this.products = new ArrayList<>();
        this.products.addAll(products);
        this.listener = listener;

        Log.d(TAG, "ConfirmProductsForLoadingTask");
    }

    @Override
    protected Void doInBackground(Void... params) {

        Log.d(TAG, "Do in background");

        User currentUser = User.getSavedUser(context);

        if (!ServerSyncService.formalisticsLogin(currentUser, context)) {
            Log.d(TAG, "Needs Authentication");
            listener.onNeedsAuthentication();
            return null;
        }

        ServiceGenerator serviceGenerator = new ServiceGenerator(context, currentUser.getFormalisticsServer(), HttpLoggingInterceptor.Level.BODY);
        ProductsForDeliveryAPI productsForDeliveryAPI = serviceGenerator.createService(ProductsForDeliveryAPI.class);

        Gson gson = new Gson();

        Log.d(TAG, " REQUEST DATA : " + gson.toJson(this.products));

        Call<FormalisticsAPIResponse> responseBodyCall =
                productsForDeliveryAPI.confirmProductsForDelivery(this.products);

        try {
            Response<FormalisticsAPIResponse> response = responseBodyCall.execute();
            FormalisticsAPIResponse formalisticsApiResponse = response.body();

            if (formalisticsApiResponse != null && formalisticsApiResponse.status == "SUCCESS") {

                Log.d(TAG, "-------------------------> RESPONSE");

                Log.d(TAG, "error : " + formalisticsApiResponse.error);
                Log.d(TAG, "error_message : " + formalisticsApiResponse.error_message);
                Log.d(TAG, "status : " + formalisticsApiResponse.status);

                Log.d(TAG, "-------------------------> RESPONSE");

            } else {
                //TODO: throw exception
            }

        } catch (IOException e) {
            e.printStackTrace();
        }


        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        listener.onFinish();
    }


}
