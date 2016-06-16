package ph.com.gs3.loyaltystore.models.api;

import java.util.List;

import ph.com.gs3.loyaltystore.models.sqlite.dao.ProductBreakdown;
import retrofit2.Call;
import retrofit2.http.GET;

/**
 * Created by Ervinne Sodusta on 2/3/2016.
 */
public interface ProductsBreakdownAPI {

    @GET("pos/products-breakdown-list")
    Call<List<ProductBreakdown>> getProductsBreakdownFromFormalistics();

}
