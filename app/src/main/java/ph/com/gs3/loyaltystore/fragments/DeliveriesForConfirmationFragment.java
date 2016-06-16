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
import android.widget.Button;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

import ph.com.gs3.loyaltystore.R;
import ph.com.gs3.loyaltystore.adapters.DeliveryProductListViewAdapter;
import ph.com.gs3.loyaltystore.adapters.ProductsForDeliveryListAdapter;
import ph.com.gs3.loyaltystore.adapters.RecievedProductsForDeliveryAdapter;
import ph.com.gs3.loyaltystore.models.sqlite.dao.ProductDelivery;
import ph.com.gs3.loyaltystore.models.sqlite.dao.ProductForDelivery;

/**
 * Created by Bryan-PC on 29/04/2016.
 */
public class DeliveriesForConfirmationFragment extends Fragment {

    public static final String TAG = DeliveriesForConfirmationFragment.class.getSimpleName();

    private FragmentActivity activity;
    private Context context;

    private DeliveriesForConfirmationFragmentListener listener;

    private ProductsForDeliveryListAdapter productsForDeliveryListAdapter;
    private RecievedProductsForDeliveryAdapter recievedProductsForDeliveryAdapter;
    private DeliveryProductListViewAdapter deliveryProductListViewAdapter;

    private ListView lvReceivedProductsForDelivery;
    private ListView lvProductsForDelivery;

    private Button bGetProductsForDelivery;
    private Button bConfirm;

    public DeliveriesForConfirmationFragment createInstance() {

        DeliveriesForConfirmationFragment deliveriesForConfirmationFragment = new DeliveriesForConfirmationFragment();
        return deliveriesForConfirmationFragment;

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;
        this.activity = (FragmentActivity) context;

        if (context instanceof DeliveriesForConfirmationFragmentListener) {
            listener = (DeliveriesForConfirmationFragmentListener) context;
        } else {
            throw new RuntimeException(context.getClass().getSimpleName() + " must implement DeliveriesForConfirmationFragmentListener");
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        this.context = activity;
        this.activity = (FragmentActivity) activity;

        if (context instanceof DeliveriesForConfirmationFragmentListener) {
            listener = (DeliveriesForConfirmationFragmentListener) activity;
        } else {
            throw new RuntimeException(context.getClass().getSimpleName() + " must implement DeliveriesForConfirmationFragmentListener");
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.activity_confirm_product_delivery, container, false);

        deliveryProductListViewAdapter = new DeliveryProductListViewAdapter(context);
        productsForDeliveryListAdapter = new ProductsForDeliveryListAdapter(context);
        recievedProductsForDeliveryAdapter = new RecievedProductsForDeliveryAdapter(context);

        lvProductsForDelivery = (ListView) rootView.findViewById(R.id.ConfirmProductDelivery_lvProductsForDelivery);
        lvProductsForDelivery.setAdapter(productsForDeliveryListAdapter);
        lvProductsForDelivery.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ProductForDelivery productForDelivery = (ProductForDelivery) productsForDeliveryListAdapter.getItem(position);
                listener.onProductForDeliveryClicked(productForDelivery);
            }
        });

        lvReceivedProductsForDelivery = (ListView) rootView.findViewById(R.id.ConfirmProductDelivery_lvReceivedProductsForDelivery);
        lvReceivedProductsForDelivery.setAdapter(recievedProductsForDeliveryAdapter);

        bConfirm = (Button) rootView.findViewById(R.id.ConfirmProductDelivery_bConfirm);
        bConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onConfirmProductDeliveries();
            }
        });

        bGetProductsForDelivery = (Button) rootView.findViewById(R.id.ConfirmProductDelivery_bGetProductsForDelivery);
        bGetProductsForDelivery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onGetProductsForDelivery();
            }
        });


        return rootView;
    }

    public void setProductDeliveryList(List<ProductDelivery> productDeliveryList) {

        for (ProductDelivery productDelivery : productDeliveryList) {
            productDelivery.setIs_synced(false);
        }

        if (deliveryProductListViewAdapter != null) {
            deliveryProductListViewAdapter.setProductDeliveryList(productDeliveryList);
        }

    }

    public List<ProductDelivery> getProductDeliveryList() {

        return deliveryProductListViewAdapter.getProductDeliveryList();

    }

    public void setProductsForDelivery(List<ProductForDelivery> productsForDelivery) {

        if (productsForDeliveryListAdapter != null) {
            productsForDeliveryListAdapter.setProductForDeliveryList(productsForDelivery);
        }
    }

    public void addReceivedProductForDelivery(ProductForDelivery productForDelivery) {

        if (recievedProductsForDeliveryAdapter != null) {

            if (!recordExist(productForDelivery))
                recievedProductsForDeliveryAdapter.addReceivedProductForDelivery(productForDelivery);
            else {
                List<ProductForDelivery> productsForDelivery
                        = recievedProductsForDeliveryAdapter.getReceivedProductsForDelivery();

                for (ProductForDelivery product : productsForDelivery) {
                    if (product.getId() == productForDelivery.getId()) {
                        product.setQuantity_received(productForDelivery.getQuantity_received());
                    }
                }

                recievedProductsForDeliveryAdapter.notifyDataSetChanged();
                //recievedProductsForDeliveryAdapter.setReceivedProductsForDelivery(productsForDelivery);
            }
        }

    }

    private boolean recordExist(ProductForDelivery productForDelivery) {

        boolean found = false;

        List<ProductForDelivery> productsForDelivery
                = recievedProductsForDeliveryAdapter.getReceivedProductsForDelivery();

        for (ProductForDelivery product : productsForDelivery) {
            if (product.getId() == productForDelivery.getId()) {
                found = true;
            }
        }

        return found;
    }

    public List<ProductForDelivery> getReceivedProductsForDelivery(){
        return recievedProductsForDeliveryAdapter.getReceivedProductsForDelivery();
    }

    public void clearDeliveryList(){
        productsForDeliveryListAdapter.setProductForDeliveryList(new ArrayList<ProductForDelivery>());
        recievedProductsForDeliveryAdapter.setReceivedProductsForDelivery(new ArrayList<ProductForDelivery>());
    }

    public interface DeliveriesForConfirmationFragmentListener {

        void onGetProductsForDelivery();

        void onProductForDeliveryClicked(ProductForDelivery productForDelivery);

        void onConfirmProductDeliveries();

    }

}
