package ph.com.gs3.loyaltystore.fragments;

import android.app.Activity;
import android.net.wifi.p2p.WifiP2pDevice;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckedTextView;
import android.widget.EditText;
import android.widget.ListView;

import org.json.JSONException;

import ph.com.gs3.loyaltystore.R;
import ph.com.gs3.loyaltystore.adapters.CustomerDeviceListAdapter;
import ph.com.gs3.loyaltystore.adapters.ViewPagerAdapter;

/**
 * Created by Bryan-PC on 02/02/2016.
 */
public class CheckOutViewFragment extends Fragment {

    public static final String TAG = CheckOutViewFragment.class.getSimpleName();

    private ListView lvDeviceList;
    private CustomerDeviceListAdapter customerDeviceListAdapter;
    private CheckoutViewFragmentEventListener checkoutViewFragmentEventListener;

    private FragmentActivity activity;

    private Button bUpdate;
    private Button bComplete;
    private Button bCompleteWithRewards;
    private Button bCancel;

    private EditText etRemarks;

    private Toolbar toolbar;
    private TabLayout tabLayout;
    private ViewPager viewPager;

    public static CheckOutViewFragment createInstance(CustomerDeviceListAdapter customerDeviceListAdapter) {
        CheckOutViewFragment checkOutViewFragment = new CheckOutViewFragment();
        checkOutViewFragment.customerDeviceListAdapter = customerDeviceListAdapter;
        return checkOutViewFragment;
    }



    public void onAttach(Activity activity) {
        super.onAttach(activity);
        this.activity = (FragmentActivity) activity;

        try {
            checkoutViewFragmentEventListener = (CheckoutViewFragmentEventListener) activity;
        } catch (ClassCastException e) {
            throw new RuntimeException(activity.getClass().getSimpleName() + " must implement ViewPromoViewFragmentEventListener");
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.fragment_checkout_landscape, container, false);

        lvDeviceList = (ListView) rootView.findViewById(R.id.Checkout_lvDeviceList);
        lvDeviceList.setAdapter(customerDeviceListAdapter);
        lvDeviceList.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        lvDeviceList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                CheckedTextView checkedTextView =
                        (CheckedTextView) view.findViewById(R.id.Customer_tvDisplayName);

                if (checkedTextView.isChecked()) {
                    checkedTextView.setChecked(false);
                    checkoutViewFragmentEventListener.onCustomerSelect(
                            (WifiP2pDevice) customerDeviceListAdapter.getItem(position),
                            false
                    );
                } else {
                    checkedTextView.setChecked(true);
                    checkoutViewFragmentEventListener.onCustomerSelect(
                            (WifiP2pDevice) customerDeviceListAdapter.getItem(position),
                            true
                    );
                }

            }
        });

        etRemarks = (EditText) rootView.findViewById(R.id.Checkout_etRemarks);

        bUpdate = (Button) rootView.findViewById(R.id.Checkout_bUpdate);
        bUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkoutViewFragmentEventListener.onUpdateCustomerList();
            }
        });

        bCancel = (Button) rootView.findViewById(R.id.Checkout_bCancel);
        bCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkoutViewFragmentEventListener.onCancel();
            }
        });

        bComplete= (Button) rootView.findViewById(R.id.Checkout_bComplete);
        bComplete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkoutViewFragmentEventListener.onComplete();
            }
        });

        bCompleteWithRewards = (Button) rootView.findViewById(R.id.Checkout_bCompleteWithRewards);
        bCompleteWithRewards.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    checkoutViewFragmentEventListener.onCompleteWithRewards();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });

        viewPager = (ViewPager) rootView.findViewById(R.id.viewpager);

        setupViewPager(viewPager);

        tabLayout = (TabLayout) rootView.findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);

        try {
            checkoutViewFragmentEventListener.onViewReady();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return  rootView;
    }

    private void setupViewPager(ViewPager viewPager) {
        Bundle extras = activity.getIntent().getExtras();

        ViewPagerAdapter adapter = new ViewPagerAdapter(activity.getSupportFragmentManager(),
                extras);

        adapter.addFragment(new SalesProductsViewFragment(), "Receipt");
        adapter.addFragment(new RewardViewFragment(), "Rewards");
        viewPager.setAdapter(adapter);
    }

    public String getRemarks(){

        return etRemarks.getText().toString();

    }

    public interface CheckoutViewFragmentEventListener {

        void onViewReady() throws JSONException;

        void onUpdateCustomerList();

        void onCustomerSelect(WifiP2pDevice customerDevice, boolean selected);

        void onComplete();

        void onCompleteWithRewards() throws JSONException;

        void onCancel();

    }

}
