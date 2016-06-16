package ph.com.gs3.loyaltystore.fragments;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.google.gson.Gson;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import ph.com.gs3.loyaltystore.CheckoutActivity;
import ph.com.gs3.loyaltystore.R;
import ph.com.gs3.loyaltystore.adapters.SalesProductListViewAdapter;
import ph.com.gs3.loyaltystore.globals.Constants;
import ph.com.gs3.loyaltystore.models.sqlite.dao.SalesProduct;

/**
 * Created by Bryan-PC on 02/02/2016.
 */
public class SalesProductsViewFragment extends Fragment {

    public static final String TAG = SalesProductsViewFragment.class.getSimpleName();
    public static final String EXTRA_SALES_PRODUCT_LIST = "sales_product_list";

    private ListView lvSalesProducts;
    private SalesProductListViewAdapter adapter;

    private Activity activity;
    private List<SalesProduct> salesProducts;

    private TextView tvAmount;

    private float total = 0;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle args =  this.getArguments();
        String dataJsonString = args.getString(CheckoutActivity.EXTRA_DATA_JSON_STRING);

        salesProducts = new ArrayList<>();
        Gson gson = new Gson();

        String salesProductJsonString = args.getString(EXTRA_SALES_PRODUCT_LIST);
        SalesProduct[] salesProductsArray = gson.fromJson(salesProductJsonString, SalesProduct[].class);
        salesProducts = Arrays.asList(salesProductsArray);

        /*try {
            JSONObject jsonObject = new JSONObject(dataJsonString);

            SalesProduct[] salesProductArray = gson.fromJson(String.valueOf(jsonObject.getJSONArray(SalesProduct.class.getSimpleName())),SalesProduct[].class);
            salesProducts = Arrays.asList(salesProductArray);

            *//*JSONArray salesProductsJsonArray = jsonObject.getJSONArray(SalesProduct.class.getSimpleName());

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

            }*//*
        } catch (JSONException e) {
            e.printStackTrace();
        }*/

        total =(float) args.get(CheckoutActivity.EXTRA_TOTAL_AMOUNT);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.fragment_sales_products, container, false);

        /*for(int i =0;i<salesProducts.size();i++){
            total  += salesProducts.get(i).getSub_total();
        }*/

        Constants constants = new Constants();
        DecimalFormat decimalFormat = constants.DECIMAL_FORMAT;

        tvAmount = (TextView) rootView.findViewById(R.id.Transaction_tvAmount);
        tvAmount.setText(decimalFormat.format(total));

        adapter = new SalesProductListViewAdapter(getActivity());
        adapter.setSalesProducts(salesProducts);

        lvSalesProducts = (ListView) rootView.findViewById(R.id.Transaction_lvTransactionList);
        lvSalesProducts.setAdapter(adapter);
        lvSalesProducts.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            }
        });



        return  rootView;

    }

}
