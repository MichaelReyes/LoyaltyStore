package ph.com.gs3.loyaltystore;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import ph.com.gs3.loyaltystore.adapters.DeliveryProductListViewAdapter;
import ph.com.gs3.loyaltystore.models.sqlite.dao.ItemInventory;
import ph.com.gs3.loyaltystore.models.sqlite.dao.ItemInventoryDao;
import ph.com.gs3.loyaltystore.models.sqlite.dao.ProductDao;
import ph.com.gs3.loyaltystore.models.sqlite.dao.ProductDelivery;
import ph.com.gs3.loyaltystore.models.sqlite.dao.ProductDeliveryDao;

/**
 * Created by Bryan-PC on 05/04/2016.
 */
public class ConfirmProductDeliveryActivity extends Activity {

    public static final String TAG = ConfirmProductDeliveryActivity.class.getSimpleName();
    public static final String EXTRA_PRODUCT_DELIVERY_LIST = "product_delivery_list";

    private DeliveryProductListViewAdapter adapter;

    private ProductDeliveryDao productDeliveryDao;

    private ListView lvDeliveryProducts;

    private Button bConfirm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_confirm_product_delivery);

        adapter = new DeliveryProductListViewAdapter(ConfirmProductDeliveryActivity.this);

        productDeliveryDao = LoyaltyStoreApplication.getSession().getProductDeliveryDao();

        lvDeliveryProducts = (ListView) findViewById(R.id.CPD_lvDeliveryProducts);
        lvDeliveryProducts.setAdapter(adapter);

        bConfirm = (Button) findViewById(R.id.CPD_bConfirm);
        bConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                confirmProductDeliveries();
            }
        });

        getExtras();

    }

    @Override
    protected void onResume() {
        super.onResume();
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    private void confirmProductDeliveries() {

        ProductDao productDao
                = LoyaltyStoreApplication.getSession().getProductDao();

        ItemInventoryDao itemInventoryDao
                = LoyaltyStoreApplication.getSession().getItemInventoryDao();

        List<ProductDelivery> productDeliveryList = adapter.getProductDeliveryList();

        for (ProductDelivery productDelivery : productDeliveryList) {

            productDeliveryDao.insertOrReplace(productDelivery);

            if ("ACCEPTED".equals(productDelivery.getStatus().trim().toUpperCase())
                    && "PRODUCT".equals(productDelivery.getDistribution_type().trim().toUpperCase())) {

                List<ItemInventory> itemInventoryList
                        = itemInventoryDao
                            .queryBuilder()
                            .where(
                                ItemInventoryDao.Properties.Product_id.eq(
                                        productDelivery.getProduct_id()
                                )
                            ).limit(1).list();

                if(itemInventoryList.size() > 0){

                    ItemInventory itemInventory = itemInventoryList.get(0);

                    itemInventory.setQuantity(itemInventory.getQuantity() + productDelivery.getQuantity());
                    itemInventoryDao.insertOrReplace(itemInventory);

                }else{
                    ItemInventory itemInventory = new ItemInventory();
                    itemInventory.setProduct_id(productDelivery.getProduct_id());
                    itemInventory.setName(productDelivery.getName());
                    itemInventory.setQuantity(productDelivery.getQuantity());

                    itemInventoryDao.insertOrReplace(itemInventory);
                }

            }

        }

        Log.d(TAG, " ***************************************************");

        List<ItemInventory> itemInventoryList = itemInventoryDao.loadAll();

        for(ItemInventory itemInventory : itemInventoryList){

            Log.d(TAG, "--> Id : " + itemInventory.getId());
            Log.d(TAG, "--> Name : " + itemInventory.getName());
            Log.d(TAG, "--> Product Id : " + itemInventory.getProduct_id());
            Log.d(TAG, "--> Quantity : " + itemInventory.getQuantity());

        }

        Log.d(TAG, " ***************************************************");

        finish();

    }

    private void getExtras() {

        Gson gson = new Gson();

        List<ProductDelivery> productDeliveryList = new ArrayList<>();
        Bundle extras = getIntent().getExtras();

        if (extras != null) {
            if (extras.containsKey(EXTRA_PRODUCT_DELIVERY_LIST)) {
                String stringProductDeliveryList = extras.getString(EXTRA_PRODUCT_DELIVERY_LIST);

                ProductDelivery[] productDeliveries
                        = gson.fromJson(stringProductDeliveryList, ProductDelivery[].class);

                productDeliveryList = Arrays.asList(productDeliveries);
            }
        } else {
            productDeliveryList =
                    productDeliveryDao
                            .queryBuilder()
                            .where(
                                    ProductDeliveryDao.Properties.Is_synced.eq(false)
                            ).list();
        }



        /*
        List<ProductDelivery> productDeliveryList = new ArrayList<>();

        ProductDelivery productDelivery1 = new ProductDelivery();
        productDelivery1.setId((long) 1);
        productDelivery1.setName("Test Product 1");
        productDelivery1.setProduct_id((long) 1);
        productDelivery1.setQuantity(10);
        productDelivery1.setStatus("For Delivery");
        productDelivery1.setDate_delivered(new Date());

        ProductDelivery productDelivery2 = new ProductDelivery();
        productDelivery2.setId((long) 2);
        productDelivery2.setName("Test Product 2");
        productDelivery2.setProduct_id((long) 2);
        productDelivery2.setQuantity(20);
        productDelivery2.setStatus("For Delivery");
        productDelivery2.setDate_delivered(new Date());

        productDeliveryList.add(productDelivery1);
        productDeliveryList.add(productDelivery2);

        //productDeliveryDao.deleteAll();
        productDeliveryDao.insertOrReplaceInTx(productDeliveryList);
        */

        setProductDeliveries(productDeliveryList);

    }


    public void setProductDeliveries(List<ProductDelivery> productDeliveryList) {

        if (adapter != null) {
            adapter.setProductDeliveryList(productDeliveryList);
        }

    }


}
