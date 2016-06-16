package ph.com.gs3.loyaltystore.models.synchronizer;

import android.content.Context;
import android.util.Log;

import java.io.IOException;
import java.util.List;

import okhttp3.logging.HttpLoggingInterceptor;
import ph.com.gs3.loyaltystore.LoyaltyStoreApplication;
import ph.com.gs3.loyaltystore.models.api.ProductsAPI;
import ph.com.gs3.loyaltystore.models.api.ServiceGenerator;
import ph.com.gs3.loyaltystore.models.sqlite.dao.ItemInventory;
import ph.com.gs3.loyaltystore.models.sqlite.dao.ItemInventoryDao;
import ph.com.gs3.loyaltystore.models.sqlite.dao.Product;
import ph.com.gs3.loyaltystore.models.sqlite.dao.ProductDao;
import ph.com.gs3.loyaltystore.models.values.Retailer;
import retrofit2.Call;
import retrofit2.Response;

/**
 * Created by Ervinne Sodusta on 2/3/2016.
 */
public class ProductSynchronizer {

    public static final String TAG = ProductSynchronizer.class.getSimpleName();

    public static List<Product> sync(Context context, String server) {

        ServiceGenerator serviceGenerator = new ServiceGenerator(context, server, HttpLoggingInterceptor.Level.BODY);
        ProductsAPI productsAPI = serviceGenerator.createService(ProductsAPI.class);

        Retailer retailer = Retailer.getDeviceRetailerFromSharedPreferences(context);

        Call<List<Product>> call = productsAPI.getProductsFromFormalistics();

        try {
            Response<List<Product>> response = call.execute();
            List<Product> products = response.body();

            if (products != null && products.size() > 0) {
                //  save products
                ProductDao productDao = LoyaltyStoreApplication.getInstance().getSession().getProductDao();
                ItemInventoryDao itemInventoryDao = LoyaltyStoreApplication.getSession().getItemInventoryDao();
                Log.v(TAG, "========== PRODUCTS INSERTED START ==========");

                for (Product product : products) {
                    long id = productDao.insertOrReplace(product);

                    Log.v(TAG, "Product id: " + id);
                    Log.v(TAG, "Product name: " + product.getName());
                    Log.v(TAG, "Product SKU: " + product.getSku());
                    Log.v(TAG, "Product cost: " + product.getUnit_cost());
                    Log.v(TAG, "Product Quantity to deduct: " + product.getDeduct_product_to_quantity());

                    if("Product for Delivery".equals(product.getType()) ||
                            "For Direct Transactions".equals(product.getType())) {

                        List<ItemInventory> itemInventoryList
                                = itemInventoryDao
                                .queryBuilder()
                                .where(
                                        ItemInventoryDao.Properties.Product_id.eq(product.getId())
                                ).list();

                        if (itemInventoryList.size() <= 0) {
                            ItemInventory itemInventory = new ItemInventory();
                            itemInventory.setProduct_id(product.getId());
                            itemInventory.setName(product.getName());
                            itemInventory.setQuantity((double) 0);
                            itemInventory.setStore_id(retailer.getStoreId());
                            itemInventory.setIs_updated(false);

                            itemInventoryDao.insertOrReplace(itemInventory);
                        }

                    }

                }

                Log.v(TAG, "========== PRODUCTS INSERTED END ==========");

                //  Comment later
                /*List<Product> insertedProducts = productDao.loadAll();
                for (Product product : insertedProducts) {
                    Log.v(TAG, "Product inserted: " + product);
                }*/

                return products;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;

    }

}
