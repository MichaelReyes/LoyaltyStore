package ph.com.gs3.loyaltystore.models.synchronizer;

import android.content.Context;
import android.util.Log;

import com.google.gson.Gson;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.logging.HttpLoggingInterceptor;
import ph.com.gs3.loyaltystore.LoyaltyStoreApplication;
import ph.com.gs3.loyaltystore.models.api.ProductsForDeliveryAPI;
import ph.com.gs3.loyaltystore.models.api.ServiceGenerator;
import ph.com.gs3.loyaltystore.models.api.objects.ProductForDeliveryResponse;
import ph.com.gs3.loyaltystore.models.sqlite.dao.ProductForDelivery;
import ph.com.gs3.loyaltystore.models.sqlite.dao.ProductForDeliveryDao;
import ph.com.gs3.loyaltystore.models.values.Retailer;
import retrofit2.Call;
import retrofit2.Response;

/**
 * Created by Ervinne Sodusta on 2/26/2016.
 */
public class ProductForDeliverySynchronizer {

    public static final String TAG = ProductForDeliverySynchronizer.class.getSimpleName();

    public static List<ProductForDelivery> sync(Context context, String formalisticsServer, Retailer retailer) {

        List<ProductForDelivery> products = new ArrayList<>();

        ServiceGenerator serviceGenerator = new ServiceGenerator(context, formalisticsServer, HttpLoggingInterceptor.Level.BODY);
        ProductsForDeliveryAPI productsForDeliveryAPI = serviceGenerator.createService(ProductsForDeliveryAPI.class);

        Call<List<ProductForDeliveryResponse>> call = productsForDeliveryAPI.getProducts(retailer.getStoreId());

        try {
            Response<List<ProductForDeliveryResponse>> response = call.execute();
            List<ProductForDeliveryResponse> responses = response.body();

            if (responses != null && responses.size() > 0) {
                for (ProductForDeliveryResponse productForDeliveryResponse : responses) {

                    products.add(productForDeliveryResponse.toProductForDelivery());
                }
            }

            Gson gson = new Gson();

            if (products.size() > 0) {
                //  save products
                ProductForDeliveryDao dao = LoyaltyStoreApplication.getSession().getProductForDeliveryDao();

                for (ProductForDelivery product : products) {
                    product.setQuantity_received((double) 0);
                    long id = dao.insertOrReplace(product);
                    Log.v(TAG, "Product inserted: " + product.getName() + " - insert id: " + id + " ~ " + product.getDate_created() );
                }

                //  Comment later
                List<ProductForDelivery> insertedProducts = dao.loadAll();
                for (ProductForDelivery product : insertedProducts) {
                    Log.v(TAG, "Product inserted: " + product.getTrack_no() + ", Status : " + product.getStatus());
                }

                return products;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;

    }

}
