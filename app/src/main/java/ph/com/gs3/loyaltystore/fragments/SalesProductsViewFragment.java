package ph.com.gs3.loyaltystore.fragments;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.ArrayList;

import ph.com.gs3.loyaltystore.CheckoutActivity;
import ph.com.gs3.loyaltystore.R;
import ph.com.gs3.loyaltystore.adapters.SalesProductListAdapter;
import ph.com.gs3.loyaltystore.globals.Constants;
import ph.com.gs3.loyaltystore.models.sqlite.dao.SalesProduct;
import ph.com.gs3.loyaltystore.models.sqlite.dao.SalesProductDao;

/**
 * Created by Bryan-PC on 02/02/2016.
 */
public class SalesProductsViewFragment extends Fragment {

    public static final String TAG = SalesProductsViewFragment.class.getSimpleName();
    public static final String EXTRA_SALES_PRODUCT_LIST = "sales_product_list";

    private ListView lvSalesProducts;
    private SalesProductListAdapter salesProductListAdapter;

    private Activity activity;
    private ArrayList<SalesProduct> salesProducts;

    private TextView tvAmount;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle args =  this.getArguments();
        String dataJsonString = args.getString(CheckoutActivity.EXTRA_DATA_JSON_STRING);

        salesProducts = new ArrayList<>();

        try {
            JSONObject jsonObject = new JSONObject(dataJsonString);
            JSONArray salesProductsJsonArray = jsonObject.getJSONArray(SalesProduct.class.getSimpleName());

            for(int i=0;i<salesProductsJsonArray.length();i++){

                JSONObject salesProductJSONObject = salesProductsJsonArray.getJSONObject(i);

                SalesProduct salesProduct = new SalesProduct();
                salesProduct.setProduct_id(salesProductJSONObject.getLong(SalesProductDao.Properties.Product_id.columnName));
                salesProduct.setQuantity(salesProductJSONObject.getInt(SalesProductDao.Properties.Quantity.columnName));
                salesProduct.setSub_total(Float.valueOf(
                        salesProductJSONObject.get(SalesProductDao.Properties.Sub_total.columnName).toString())
                );
                salesProduct.setSale_type(salesProductJSONObject.getString(SalesProductDao.Properties.Sale_type.columnName));

                salesProducts.add(salesProduct);

            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.fragment_sales_products, container, false);

        Log.d(TAG,"ON CREATE TRANSACTIONS VIEW");

        float total = 0;
        for(int i =0;i<salesProducts.size();i++){
            total  += salesProducts.get(i).getSub_total();
        }

        Constants constants = new Constants();
        DecimalFormat decimalFormat = constants.DECIMAL_FORMAT;

        tvAmount = (TextView) rootView.findViewById(R.id.Transaction_tvAmount);
        tvAmount.setText(decimalFormat.format(total));

        salesProductListAdapter = new SalesProductListAdapter(getActivity(),salesProducts);
        salesProductListAdapter.notifyDataSetChanged();

        lvSalesProducts = (ListView) rootView.findViewById(R.id.Transaction_lvTransactionList);
        lvSalesProducts.setAdapter(salesProductListAdapter);
        lvSalesProducts.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            }
        });

        return  rootView;

    }

}
