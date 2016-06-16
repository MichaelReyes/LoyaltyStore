package ph.com.gs3.loyaltystore.models.tasks;

import android.content.Context;
import android.os.AsyncTask;

import java.io.IOException;
import java.util.List;

import okhttp3.logging.HttpLoggingInterceptor;
import ph.com.gs3.loyaltystore.models.User;
import ph.com.gs3.loyaltystore.models.api.ProductsForDeliveryAPI;
import ph.com.gs3.loyaltystore.models.api.ServiceGenerator;
import ph.com.gs3.loyaltystore.models.api.objects.AcceptRejectProductRequest;
import ph.com.gs3.loyaltystore.models.api.objects.FormalisticsAPIResponse;
import ph.com.gs3.loyaltystore.models.services.ServerSyncService;
import ph.com.gs3.loyaltystore.models.sqlite.dao.ProductForDelivery;
import retrofit2.Call;
import retrofit2.Response;

public class AcceptProductsTask extends AsyncTask<Void, Void, Void> {

    public static final String TAG = AcceptProductsTask.class.getSimpleName();

    private Context context;
    private List<ProductForDelivery> products;
    private AcceptProductsTaskListener listener;

    public AcceptProductsTask(Context context, List<ProductForDelivery> products, AcceptProductsTaskListener listener) {
        this.context = context;
        this.products = products;
        this.listener = listener;
    }

    @Override
    protected Void doInBackground(Void... params) {

        User currentUser = User.getSavedUser(context);

        if (!ServerSyncService.formalisticsLogin(currentUser, context)) {
            listener.onNeedsAuthentication();
            return null;
        }

        ServiceGenerator serviceGenerator = new ServiceGenerator(context, currentUser.getFormalisticsServer(), HttpLoggingInterceptor.Level.BODY);
        ProductsForDeliveryAPI productsForDeliveryAPI = serviceGenerator.createService(ProductsForDeliveryAPI.class);

        for (ProductForDelivery product : products) {
            AcceptRejectProductRequest request = new AcceptRejectProductRequest();

            request.action = "";
            request.form_id = 777;
            request.request_id = product.getId();
            request.request_data = "{}";

            Call<FormalisticsAPIResponse> responseBodyCall = productsForDeliveryAPI.update(request);

            try {
                Response rawResponse = responseBodyCall.execute();
                if (rawResponse != null && rawResponse.body() != null) {
                    FormalisticsAPIResponse formalisticsApiResponse = (FormalisticsAPIResponse) rawResponse.body();

                    if (formalisticsApiResponse != null && formalisticsApiResponse.status == "SUCCESS") {

                    } else {
                        //TODO: throw exception
                    }

                } else {
                    //TODO: throw exception
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        listener.onFinish();
    }

    public interface AcceptProductsTaskListener {
        void onNeedsAuthentication();

        void onFinish();
    }

}