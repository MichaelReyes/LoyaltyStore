package ph.com.gs3.loyaltystore;

import android.app.Activity;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import ph.com.gs3.loyaltystore.adapters.DeliveryByDateListAdapter;
import ph.com.gs3.loyaltystore.adapters.DeliveryListAdapter;
import ph.com.gs3.loyaltystore.models.sqlite.dao.ProductDelivery;
import ph.com.gs3.loyaltystore.models.sqlite.dao.ProductDeliveryDao;

/**
 * Created by Bryan-PC on 29/03/2016.
 */
public class ProductDeliveryActivity extends Activity {

    public static final String TAG = ProductDeliveryActivity.class.getSimpleName();

    private DeliveryByDateListAdapter deliveryByDateListAdapter;
    private DeliveryListAdapter deliveryListAdapter;

    private List<ProductDelivery> productDeliveryListByDate;
    private List<ProductDelivery> productDeliveries;

    private ListView lvProductDeliveryListByDate;
    private ListView lvProductDeliveryList;

    private ProductDeliveryDao productDeliveryDao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_delivery);

        productDeliveryListByDate = new ArrayList<>();
        deliveryByDateListAdapter = new DeliveryByDateListAdapter(
                ProductDeliveryActivity.this, productDeliveryListByDate
        );

        productDeliveries = new ArrayList<>();
        deliveryListAdapter = new DeliveryListAdapter(
                this, productDeliveries
        );

        productDeliveryDao = LoyaltyStoreApplication.getSession().getProductDeliveryDao();

        initializeViews();
        setDistinctDeliveryList();

    }

    private void initializeViews() {

        lvProductDeliveryListByDate = (ListView) findViewById(R.id.Deliveries_lvDeliveriesByDate);
        lvProductDeliveryListByDate.setAdapter(deliveryByDateListAdapter);
        lvProductDeliveryListByDate.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ProductDelivery productDelivery =
                        (ProductDelivery) deliveryByDateListAdapter.getItem(position);

                setDeliveryListByDateAndAgentName(productDelivery);
            }
        });

        lvProductDeliveryList = (ListView) findViewById(R.id.Deliveries_lvDeliveries);
        lvProductDeliveryList.setAdapter(deliveryListAdapter);

        List<ProductDelivery> productDeliveryList = productDeliveryDao.loadAll();

        Log.d(TAG, "================ PRODUCT DELIVERY START ================");

        for(ProductDelivery productDelivery : productDeliveryList){

            Log.d(TAG, "--------------------------------> START");

            Log.d(TAG, "Name :" + productDelivery.getName());
            Log.d(TAG, "Status :" + productDelivery.getStatus());
            Log.d(TAG, "Agent Id :" + productDelivery.getDelivered_by_agent_id());
            Log.d(TAG, "Agent Name :" + productDelivery.getDelivered_by_agent_name());
            Log.d(TAG, "Store Id :" + productDelivery.getDelivered_to_store_id());
            Log.d(TAG, "Store Name :" + productDelivery.getDelivered_to_store_name());
            Log.d(TAG, "Quantity :" + productDelivery.getQuantity());
            Log.d(TAG, "Date Delivered :" + productDelivery.getDate_delivered());

            Log.d(TAG, "--------------------------------> END");


        }

        Log.d(TAG, "================ PRODUCT DELIVERY END ================");

    }

    private void setDeliveryListByDateAndAgentName(ProductDelivery productDelivery) {

        List<ProductDelivery> productDeliveryList
                = productDeliveryDao
                .queryBuilder()
                .where(
                        ProductDeliveryDao.Properties.Date_delivered.eq(
                                productDelivery.getDate_delivered()
                        ),
                        ProductDeliveryDao.Properties.Delivered_by_agent_name.eq(
                                productDelivery.getDelivered_by_agent_name()
                        )
                ).list();

        Gson gson =new Gson();

        Log.d("MIKE",  gson.toJson(productDeliveryList));

        productDeliveries.clear();
        productDeliveries.addAll(productDeliveryList);
        deliveryListAdapter.notifyDataSetChanged();

    }

    private void setDistinctDeliveryList() {

        productDeliveryListByDate.clear();

        String query =
                "SELECT DISTINCT "
                        + ProductDeliveryDao.Properties.Date_delivered.columnName + "," +
                        ProductDeliveryDao.Properties.Delivered_by_agent_name.columnName +
                        " FROM " + ProductDeliveryDao.TABLENAME;

        Cursor c = LoyaltyStoreApplication.getSession().getDatabase().rawQuery(query, null);
        try {
            if (c.moveToFirst()) {
                do {

                    ProductDelivery productDelivery = new ProductDelivery();

                    Date date = new Date(c.getLong(0));
                    productDelivery.setDate_delivered(date);
                    productDelivery.setDelivered_by_agent_name(c.getString(1));

                    productDeliveryListByDate.add(productDelivery);

                    Log.d(TAG, "**************************> START");

                    Log.d(TAG, "Date Delivered :" + productDelivery.getDate_delivered());
                    Log.d(TAG, "Agent Name :" + productDelivery.getDelivered_by_agent_name());

                    Log.d(TAG, "**************************> END");

                } while (c.moveToNext());
            }
        } finally {
            c.close();
        }

        deliveryByDateListAdapter.notifyDataSetChanged();

    }

}
