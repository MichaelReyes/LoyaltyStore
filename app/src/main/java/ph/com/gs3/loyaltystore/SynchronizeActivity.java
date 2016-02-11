package ph.com.gs3.loyaltystore;

import android.app.Activity;
import android.net.wifi.p2p.WifiP2pDevice;
import android.os.Bundle;

import java.util.ArrayList;
import java.util.List;

import ph.com.gs3.loyaltystore.adapters.CustomerDeviceListAdapter;
import ph.com.gs3.loyaltystore.fragments.SynchronizeViewFragment;
import ph.com.gs3.loyaltystore.models.values.Retailer;
import ph.com.gs3.loyaltystore.presenters.WifiDirectConnectivityDataPresenter;

/**
 * Created by Bryan-PC on 11/02/2016.
 */
public class SynchronizeActivity extends Activity implements
        SynchronizeViewFragment.SynchronizeViewFragmentEventListener,
        WifiDirectConnectivityDataPresenter.WifiDirectConnectivityPresentationListener{

    public static final String TAG = SynchronizeActivity.class.getSimpleName();

    private SynchronizeViewFragment synchronizeViewFragment;

    private Retailer retailer;

    private CustomerDeviceListAdapter customerDeviceListAdapter;

    private WifiDirectConnectivityDataPresenter wifiDirectConnectivityDataPresenter;

    private List<WifiP2pDevice> customerDeviceList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_synchronize);

        retailer = Retailer.getDeviceRetailerFromSharedPreferences(this);

        wifiDirectConnectivityDataPresenter = new WifiDirectConnectivityDataPresenter(
                this, retailer.getDeviceInfo()
        );

        customerDeviceList = new ArrayList<>();
        customerDeviceListAdapter = new CustomerDeviceListAdapter(this, customerDeviceList);

        synchronizeViewFragment = (SynchronizeViewFragment)
                getFragmentManager().findFragmentByTag(SynchronizeViewFragment.TAG);


        if (synchronizeViewFragment == null) {
            synchronizeViewFragment = SynchronizeViewFragment.createInstance(customerDeviceListAdapter);
            getFragmentManager().beginTransaction().add(
                    R.id.container_synchronize,
                    synchronizeViewFragment,
                    SynchronizeViewFragment.TAG
            ).commit();
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        wifiDirectConnectivityDataPresenter.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        wifiDirectConnectivityDataPresenter.onDestroy();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    @Override
    public void onViewReady() {
        wifiDirectConnectivityDataPresenter.discoverPeers();
    }

    @Override
    public void onDiscoverPeersCommand() {
        wifiDirectConnectivityDataPresenter.discoverPeers();
    }

    @Override
    public void onCustomerDeviceClicked(WifiP2pDevice customerDevice) {

    }

    @Override
    public void onNewPeersDiscovered(List<WifiP2pDevice> wifiP2pDevices) {

        this.customerDeviceList.clear();
        this.customerDeviceList.addAll(wifiP2pDevices);
        customerDeviceListAdapter.notifyDataSetChanged();

    }

    @Override
    public void onConnectionEstablished() {

    }

    @Override
    public void onConnectionTerminated() {

    }
}
