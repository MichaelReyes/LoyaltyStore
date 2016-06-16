package ph.com.gs3.loyaltystore.models.api;

import java.util.List;

import ph.com.gs3.loyaltystore.models.sqlite.dao.Product;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Created by Ervinne Sodusta on 2/3/2016.
 */
public interface ProductsAPI {

    @GET("products")
    Call<List<Product>> getProducts(@Query("lastUpdate") String lastUpdate);

    @GET("pos/product-list")
    Call<List<Product>> getProductsFromFormalistics();

}
