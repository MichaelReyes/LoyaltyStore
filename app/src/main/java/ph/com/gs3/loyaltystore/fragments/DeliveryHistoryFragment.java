package ph.com.gs3.loyaltystore.fragments;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.List;

import ph.com.gs3.loyaltystore.R;
import ph.com.gs3.loyaltystore.adapters.DeliveryByDateListAdapter;
import ph.com.gs3.loyaltystore.adapters.DeliveryListAdapter;
import ph.com.gs3.loyaltystore.models.sqlite.dao.ProductForDelivery;

/**
 * Created by Bryan-PC on 29/04/2016.
 */
public class DeliveryHistoryFragment extends Fragment {

    public static final String TAG = DeliveryHistoryFragment.class.getSimpleName();

    private FragmentActivity activity;
    private Context context;

    private DeliveryByDateListAdapter deliveryByDateListAdapter;
    private DeliveryListAdapter deliveryListAdapter;

    private ListView lvProductDeliveryListByDate;
    private ListView lvProductDeliveryList;

    private DeliveryHistoryFragmentListener listener;

    public DeliveryHistoryFragment createInstance() {

        DeliveryHistoryFragment deliveryHistoryFragment = new DeliveryHistoryFragment();
        return deliveryHistoryFragment;

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;
        this.activity = (FragmentActivity) context;

        if (context instanceof DeliveryHistoryFragmentListener) {
            listener = (DeliveryHistoryFragmentListener) context;
        } else {
            throw new RuntimeException(context.getClass().getSimpleName() + " must implement RetDeliveryHistoryFragmentListenerurnsToCommissaryViewFragmentListener");
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        this.context = activity;
        this.activity = (FragmentActivity) activity;

        if (context instanceof DeliveryHistoryFragmentListener) {
            listener = (DeliveryHistoryFragmentListener) activity;
        } else {
            throw new RuntimeException(context.getClass().getSimpleName() + " must implement RetDeliveryHistoryFragmentListenerurnsToCommissaryViewFragmentListener");
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.activity_delivery_history, container, false);

        deliveryByDateListAdapter = new DeliveryByDateListAdapter(activity);

        lvProductDeliveryListByDate =
                (ListView) rootView.findViewById(R.id.Deliveries_lvDeliveriesByDate);
        lvProductDeliveryListByDate.setAdapter(deliveryByDateListAdapter);
        lvProductDeliveryListByDate.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ProductForDelivery productForDelivery =
                        (ProductForDelivery) deliveryByDateListAdapter.getItem(position);

                listener.onViewDeliveryHistory(productForDelivery);
            }
        });

        deliveryListAdapter = new DeliveryListAdapter(activity);

        lvProductDeliveryList = (ListView) rootView.findViewById(R.id.Deliveries_lvDeliveries);
        lvProductDeliveryList.setAdapter(deliveryListAdapter);

        listener.onDeliveryHistoryViewFragmentReady();

        return rootView;
    }

    public void setDeliveryListByDateAndAgentName(List<ProductForDelivery> productForDeliveryHistoryList) {

        if (deliveryByDateListAdapter != null) {
            deliveryByDateListAdapter.setDeliveryByDateList(productForDeliveryHistoryList);
        }

    }

    public void setProductDeliveryList(List<ProductForDelivery> productForDeliveryList) {

        if (deliveryListAdapter != null) {
            deliveryListAdapter.setDeliveryList(productForDeliveryList);
        }

    }

    public interface DeliveryHistoryFragmentListener {

        void onDeliveryHistoryViewFragmentReady();

        void onViewDeliveryHistory(ProductForDelivery productForDelivery);

    }


}
