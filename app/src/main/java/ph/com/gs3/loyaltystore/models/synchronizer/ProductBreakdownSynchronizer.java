package ph.com.gs3.loyaltystore.models.synchronizer;

import android.content.Context;
import android.util.Log;

import java.io.IOException;
import java.util.List;

import okhttp3.logging.HttpLoggingInterceptor;
import ph.com.gs3.loyaltystore.LoyaltyStoreApplication;
import ph.com.gs3.loyaltystore.models.api.ProductsBreakdownAPI;
import ph.com.gs3.loyaltystore.models.api.ServiceGenerator;
import ph.com.gs3.loyaltystore.models.sqlite.dao.ProductBreakdown;
import ph.com.gs3.loyaltystore.models.sqlite.dao.ProductBreakdownDao;
import retrofit2.Call;
import retrofit2.Response;

/**
 * Created by Ervinne Sodusta on 2/3/2016.
 */
public class ProductBreakdownSynchronizer {

    public static final String TAG = ProductBreakdownSynchronizer.class.getSimpleName();

    public static List<ProductBreakdown> sync(Context context, String server) {

        ServiceGenerator serviceGenerator = new ServiceGenerator(context, server, HttpLoggingInterceptor.Level.BODY);
        ProductsBreakdownAPI productsBreakdownAPI = serviceGenerator.createService(ProductsBreakdownAPI.class);

        Log.v(TAG, "Starting productsBreakdownAPI");

        Call<List<ProductBreakdown>> call = productsBreakdownAPI.getProductsBreakdownFromFormalistics();

        try {
            Response<List<ProductBreakdown>> response = call.execute();
            List<ProductBreakdown> productBreakdownList = response.body();

            if (productBreakdownList != null && productBreakdownList.size() > 0) {
                //  save productBreakdownList
                ProductBreakdownDao productBreakdownDao =
                        LoyaltyStoreApplication.getInstance().getSession().getProductBreakdownDao();

                Log.v(TAG, "========== PRODUCT BREAKDOWN INSERTED START ==========");

                for (ProductBreakdown productBreakdown : productBreakdownList) {
                    long id = productBreakdownDao.insertOrReplace(productBreakdown);

                    Log.v(TAG, "Product id: " + id);
                    Log.v(TAG, "Product name: " + productBreakdown.getName());
                    Log.v(TAG, "Product TS: " + productBreakdown.getTs());
                    Log.v(TAG, "Product product id: " + productBreakdown.getProduct_id());

                }

                Log.v(TAG, "========== PRODUCT BREAKDOWN INSERTED START ==========");

                //  Comment later
                /*List<Product> insertedProducts = productBreakdownDao.loadAll();
                for (Product product : insertedProducts) {
                    Log.v(TAG, "Product inserted: " + product);
                }*/

                return productBreakdownList;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;

    }

}
