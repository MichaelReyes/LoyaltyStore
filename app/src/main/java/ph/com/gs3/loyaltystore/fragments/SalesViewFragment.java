package ph.com.gs3.loyaltystore.fragments;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import ph.com.gs3.loyaltystore.R;
import ph.com.gs3.loyaltystore.adapters.SalesListAdapter;
import ph.com.gs3.loyaltystore.adapters.SalesProductListAdapter;
import ph.com.gs3.loyaltystore.models.sqlite.dao.Sales;

/**
 * Created by Bryan-PC on 18/02/2016.
 */
public class SalesViewFragment extends Fragment {

    public static final String TAG = SalesViewFragment.class.getSimpleName();

    private ListView lvSales;
    private ListView lvSalesProducts;

    private Activity mActivity;

    private SalesListAdapter salesListAdapter;
    private SalesProductListAdapter salesProductListAdapter;

    private SalesViewFragmentListener salesViewFragmentListener;

    public static SalesViewFragment createInstance(
            SalesListAdapter salesListAdapter, SalesProductListAdapter salesProductListAdapter
    ){
        SalesViewFragment salesViewFragment = new SalesViewFragment();
        salesViewFragment.salesListAdapter = salesListAdapter;
        salesViewFragment.salesProductListAdapter = salesProductListAdapter;
        return salesViewFragment;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mActivity = activity;
        try {
            salesViewFragmentListener = (SalesViewFragmentListener) activity;
        } catch (ClassCastException e) {
            throw new RuntimeException(activity.getClass().getSimpleName() + " must implement SalesViewFragmentListener");
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_sales, container, false);

        lvSales = (ListView) rootView.findViewById(R.id.Sales_lvSales);
        lvSales.setAdapter(salesListAdapter);
        lvSales.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                salesViewFragmentListener.onViewOrder(
                        (Sales) salesListAdapter.getItem(position)
                );
            }
        });

        lvSalesProducts = (ListView) rootView.findViewById(R.id.Sales_lvSalesProducts);
        lvSalesProducts.setAdapter(salesProductListAdapter);
        lvSalesProducts.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            }
        });

        salesViewFragmentListener.onViewReady();

        return rootView;
    }

    public interface SalesViewFragmentListener {

        void onViewReady();

        void onViewOrder(Sales sales);
    }

}
