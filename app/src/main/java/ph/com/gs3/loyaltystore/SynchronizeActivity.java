package ph.com.gs3.loyaltystore;

import android.app.Activity;
import android.content.Intent;
import android.net.wifi.p2p.WifiP2pDevice;
import android.os.Bundle;

import java.util.ArrayList;
import java.util.List;

import ph.com.gs3.loyaltystore.adapters.AgentDeviceListAdapter;
import ph.com.gs3.loyaltystore.fragments.SynchronizeViewFragment;
import ph.com.gs3.loyaltystore.models.values.DeviceInfo;
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

    private AgentDeviceListAdapter agentDeviceListAdapter;

    private WifiDirectConnectivityDataPresenter wifiDirectConnectivityDataPresenter;

    private List<WifiP2pDevice> agentDeviceList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_synchronize);

        retailer = Retailer.getDeviceRetailerFromSharedPreferences(this);

        wifiDirectConnectivityDataPresenter = new WifiDirectConnectivityDataPresenter(
                this, retailer.getDeviceInfo()
        );

        agentDeviceList = new ArrayList<>();
        agentDeviceListAdapter = new AgentDeviceListAdapter(this, agentDeviceList);

        synchronizeViewFragment = (SynchronizeViewFragment)
                getFragmentManager().findFragmentByTag(SynchronizeViewFragment.TAG);


        if (synchronizeViewFragment == null) {
            synchronizeViewFragment = SynchronizeViewFragment.createInstance(agentDeviceListAdapter);
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
        wifiDirectConnectivityDataPresenter.discoverPeers(DeviceInfo.Type.AGENT);
    }

    @Override
    public void onDiscoverPeersCommand() {
        wifiDirectConnectivityDataPresenter.discoverPeers(DeviceInfo.Type.AGENT);
    }

    @Override
    public void onCustomerDeviceClicked(WifiP2pDevice customerDevice) {

        wifiDirectConnectivityDataPresenter.connectToCustomer(customerDevice,3001);

    }

    @Override
    public void onNewPeersDiscovered(List<WifiP2pDevice> wifiP2pDevices) {

        this.agentDeviceList.clear();
        this.agentDeviceList.addAll(wifiP2pDevices);
        agentDeviceListAdapter.notifyDataSetChanged();

    }

    @Override
    public void onConnectionEstablished() {
        Intent intent = new Intent(this,SynchronizeWithAgentActivity.class);
        startActivity(intent);
    }

    @Override
    public void onConnectionTerminated() {

    }
}
