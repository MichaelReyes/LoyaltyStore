package ph.com.gs3.loyaltystore.fragments;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import ph.com.gs3.loyaltystore.R;
import ph.com.gs3.loyaltystore.adapters.ViewPagerAdapter;
import ph.com.gs3.loyaltystore.models.TabMaintenance;
import ph.com.gs3.loyaltystore.models.values.Retailer;

/**
 * Created by Bryan-PC on 28/04/2016.
 */
public class SettingsFragment extends Fragment implements
        ViewPager.OnPageChangeListener{

    public static final String TAG = SettingsFragment.class.getSimpleName();

    private TabLayout tabLayout;
    private ViewPager viewPager;
    private ViewPagerAdapter viewPagerAdapter;

    private Context context;
    private FragmentActivity activity;

    private View v;

    private Fragment currentFragment;

    public SettingsFragment createInstance() {
        SettingsFragment settingsFragment = new SettingsFragment();
        return settingsFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;
        this.activity = (FragmentActivity) context;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        this.activity = (FragmentActivity) activity;
        this.context = activity;

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_settings, container, false);
        v = rootView;

        Log.d(TAG, "SettingsFragment created");

        initializeViews();

        return rootView;
    }

    public void initializeViews() {
        viewPager = (ViewPager) v.findViewById(R.id.SettingsFragment_viewpager);
        viewPager.addOnPageChangeListener(this);

        setupViewPager(viewPager);

        tabLayout = (TabLayout) v.findViewById(R.id.SettingsFragment_tabs);
        tabLayout.setupWithViewPager(viewPager);
    }

    private void setupViewPager(ViewPager viewPager) {
        Bundle extras = activity.getIntent().getExtras();

        viewPagerAdapter = new ViewPagerAdapter(activity.getSupportFragmentManager(),
                extras);


        viewPagerAdapter.addFragment(new StoreAccountSettingsFragment(), "Store Account");
        viewPagerAdapter.addFragment(new TabMaintenanceSettingsFragment(), "Tabs Maintenance");

        viewPager.setAdapter(viewPagerAdapter);

    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        currentFragment = (Fragment) viewPagerAdapter.instantiateItem(viewPager, position);

        if(currentFragment instanceof StoreAccountSettingsFragment){

        }else if(currentFragment instanceof TabMaintenanceSettingsFragment){

        }

    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    /*
    ------------------------------------------------------------------------------------------------
    Store Account
    ------------------------------------------------------------------------------------------------
     */

    public void onSaveStoreAccount(){

        Retailer retailer = Retailer.getDeviceRetailerFromSharedPreferences(activity);



    }

    public void loadStoreAccount(){

        currentFragment = viewPagerAdapter.getRegisteredFragment(viewPager.getCurrentItem());

        Log.d(TAG, "loadStoreAccount : " +  (currentFragment instanceof StoreAccountSettingsFragment));

        if(currentFragment instanceof StoreAccountSettingsFragment){
            Retailer retailer = Retailer.getDeviceRetailerFromSharedPreferences(activity);

            Log.d(TAG, " name : " + retailer.getStoreName());
            Log.d(TAG, " url : " + retailer.getServerUrl());

            ((StoreAccountSettingsFragment) currentFragment).setStoreName(retailer.getStoreName());
            ((StoreAccountSettingsFragment) currentFragment).setUrl(retailer.getServerUrl());

        }
    }

    public void setStoreName(String storeName){
        if(currentFragment instanceof StoreAccountSettingsFragment){
            ((StoreAccountSettingsFragment) currentFragment).setStoreName(storeName);
        }
    }

    /*
    ------------------------------------------------------------------------------------------------
     */

    /*
    ------------------------------------------------------------------------------------------------
    Tab Maintenance
    ------------------------------------------------------------------------------------------------
     */

    public void onSaveTabMaintenance(){

        if(currentFragment instanceof TabMaintenanceSettingsFragment) {

            TabMaintenance tabMaintenance = TabMaintenance.getTabMaintenanceFromSharedPreferences(activity);

            tabMaintenance.setInvoiceTabActive(
                    ((TabMaintenanceSettingsFragment) currentFragment).tglInvoice.isChecked()
            );

            tabMaintenance.setInvoiceMenuAsButtonsTabActive(
                    ((TabMaintenanceSettingsFragment) currentFragment).tglInvoiceMenuAsButtons.isChecked()
            );

            tabMaintenance.setInvoiceMenuAsListTabActive(
                    ((TabMaintenanceSettingsFragment) currentFragment).tglInvoiceMenuAsList.isChecked()
            );

            tabMaintenance.setSynchronizeTabActive(
                    ((TabMaintenanceSettingsFragment) currentFragment).tglSynchronize.isChecked()
            );

            tabMaintenance.setSynchronizeByWebTabActive(
                    ((TabMaintenanceSettingsFragment) currentFragment).tglSynchronizeByWeb.isChecked()
            );

            tabMaintenance.setSynchronizeByDriverTabActive(
                    ((TabMaintenanceSettingsFragment) currentFragment).tglSynchronizeByDriver.isChecked()
            );

            tabMaintenance.setInventoryTabActive(
                    ((TabMaintenanceSettingsFragment) currentFragment).tglInventory.isChecked()
            );

            tabMaintenance.setInventoryOfSalesTabActive(
                    ((TabMaintenanceSettingsFragment) currentFragment).tglInventoryOfSales.isChecked()
            );

            tabMaintenance.setInventoryOfStocksTabActive(
                    ((TabMaintenanceSettingsFragment) currentFragment).tglInventoryOfStocks.isChecked()
            );

            tabMaintenance.setInventoryItemStockCountTabActive(
                    ((TabMaintenanceSettingsFragment) currentFragment).tglInventoryItemStockCount.isChecked()
            );

            tabMaintenance.setInventoryReturnsToCommissaryTabActive(
                    ((TabMaintenanceSettingsFragment) currentFragment).tglInventoryReturnsToCommissary.isChecked()
            );

            tabMaintenance.setInventoryExpensesTabActive(
                    ((TabMaintenanceSettingsFragment) currentFragment).tglInventoryExpenses.isChecked()
            );

            tabMaintenance.setSalesTabActive(
                    ((TabMaintenanceSettingsFragment) currentFragment).tglSales.isChecked()
            );

            tabMaintenance.setDeliveryTabActive(
                    ((TabMaintenanceSettingsFragment) currentFragment).tglDelivery.isChecked()
            );

            tabMaintenance.setDeliveryHistoryTabActive(
                    ((TabMaintenanceSettingsFragment) currentFragment).tglDeliveryHistory.isChecked()
            );

            tabMaintenance.setDeliveryForConfirmationTabActive(
                    ((TabMaintenanceSettingsFragment) currentFragment).tglDeliveriesForConfirmation.isChecked()
            );

            tabMaintenance.save(context);

            Toast.makeText(context,"Save successful!", Toast.LENGTH_SHORT);

        }

    }

    /*
    ------------------------------------------------------------------------------------------------
     */


}
