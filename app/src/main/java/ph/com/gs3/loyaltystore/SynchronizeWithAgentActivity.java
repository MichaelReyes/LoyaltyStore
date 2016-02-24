package ph.com.gs3.loyaltystore;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.wifi.WifiManager;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import ph.com.gs3.loyaltystore.adapters.AgentDeviceListAdapter;
import ph.com.gs3.loyaltystore.models.receivers.NetworkChangeStatusReciever;
import ph.com.gs3.loyaltystore.models.sqlite.dao.Product;
import ph.com.gs3.loyaltystore.models.sqlite.dao.ProductDao;
import ph.com.gs3.loyaltystore.models.sqlite.dao.Reward;
import ph.com.gs3.loyaltystore.models.sqlite.dao.RewardDao;
import ph.com.gs3.loyaltystore.models.sqlite.dao.Sales;
import ph.com.gs3.loyaltystore.models.tasks.SearchAgentTask;
import ph.com.gs3.loyaltystore.models.tasks.SyncWithAgentTask;
import ph.com.gs3.loyaltystore.models.values.DeviceInfo;
import ph.com.gs3.loyaltystore.models.values.Retailer;
import ph.com.gs3.loyaltystore.presenters.WifiDirectConnectivityDataPresenter;

/**
 * Created by Bryan-PC on 15/02/2016.
 */
public class SynchronizeWithAgentActivity extends AppCompatActivity implements
        WifiDirectConnectivityDataPresenter.WifiDirectConnectivityPresentationListener,
        SyncWithAgentTask.SyncWithAgentTaskListener {

    public static final String TAG = SynchronizeWithAgentActivity.class.getSimpleName();

    public static final String EXTRA_AGENT_DEVICE = "agent_device";

    private TextView tvConnectivity;
    protected ProgressBar pbSyncProducts;
    protected ProgressBar pbSyncRewards;
    protected ProgressBar pbSyncSales;

    protected TextView tvSyncProductsLabel;
    protected TextView tvSyncRewardsLabel;
    protected TextView tvSyncSalesLabel;

    protected TextView tvSyncProductsResult;
    protected TextView tvSyncRewardsResult;
    protected TextView tvSyncSalesResult;

    private Button bSync;
    private Button bClose;

    private WifiP2pDevice agentDevice;
    private Retailer retailer;
    private WifiDirectConnectivityDataPresenter wifiDirectConnectivityDataPresenter;

    private ProductDao productDao;
    private RewardDao rewardDao;

    private NetworkChangeStatusReciever networkChangeStatusReciever;

    private AgentDeviceListAdapter agentDeviceListAdapter;
    private List<WifiP2pDevice> agentDeviceList;

    private ListView lvAgentDevice;

    private ProgressDialog progressDialog;

    private SearchAgentTask searchAgentTask;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sync_with_agent);

        initializeConnectivity();
        initializeViews();
        initializeDataAccessObjects();

    }


    private void initializeConnectivity() {

        networkChangeStatusReciever = new NetworkChangeStatusReciever();

        retailer = Retailer.getDeviceRetailerFromSharedPreferences(this);

        wifiDirectConnectivityDataPresenter = new WifiDirectConnectivityDataPresenter(
                this, retailer.getDeviceInfo()
        );

        agentDevice = new WifiP2pDevice();

        /*
        agentDevice = (WifiP2pDevice) getIntent().getExtras().get(EXTRA_AGENT_DEVICE);

        if (agentDevice != null) {
            retailer = Retailer.getDeviceRetailerFromSharedPreferences(this);
            wifiDirectConnectivityDataPresenter = new WifiDirectConnectivityDataPresenter(
                    this, retailer.getDeviceInfo()
            );
        } else {
            Toast.makeText(this, "Agent Device Unavailable", Toast.LENGTH_LONG).show();
            finish();
        }
        */

        agentDeviceList = new ArrayList<>();
        agentDeviceListAdapter = new AgentDeviceListAdapter(this,agentDeviceList);

        lvAgentDevice = (ListView) findViewById(R.id.Sync_lvDeviceList);
        lvAgentDevice.setAdapter(agentDeviceListAdapter);

        wifiDirectConnectivityDataPresenter.discoverPeers(DeviceInfo.Type.AGENT);

    }

    private void initializeDataAccessObjects() {

        productDao = LoyaltyStoreApplication.getInstance().getSession().getProductDao();
        rewardDao = LoyaltyStoreApplication.getInstance().getSession().getRewardDao();

    }

    @Override
    protected void onResume() {
        super.onResume();
        wifiDirectConnectivityDataPresenter.onResume();
        //registerReceiver(networkChangeStatusReciever, networkChangeStatusReciever.getIntentFilter());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        wifiDirectConnectivityDataPresenter.onDestroy();
        //unregisterReceiver(networkChangeStatusReciever);

        wifiDirectConnectivityDataPresenter.disconnect(new WifiP2pManager.ActionListener() {
            @Override
            public void onSuccess() {
                Log.v(TAG, "Disconnected");
            }

            @Override
            public void onFailure(int reason) {
                Log.v(TAG, "Disconnection failed: " + reason);
            }
        });
    }

    private void initializeViews() {

        progressDialog = new ProgressDialog(this);

        tvConnectivity = (TextView) findViewById(R.id.Sync_tvConnectivity);

        pbSyncProducts = (ProgressBar) findViewById(R.id.Sync_pbSyncProductsProgress);
        pbSyncRewards = (ProgressBar) findViewById(R.id.Sync_pbSyncRewardsProgress);
        pbSyncSales = (ProgressBar) findViewById(R.id.Sync_pbSyncSalesProgress);

        tvSyncProductsLabel = (TextView) findViewById(R.id.Sync_tvSyncProductsLabel);
        tvSyncRewardsLabel = (TextView) findViewById(R.id.Sync_tvSyncRewardsLabel);
        tvSyncSalesLabel = (TextView) findViewById(R.id.Sync_tvSyncSalesLabel);

        tvSyncProductsResult = (TextView) findViewById(R.id.Sync_tvSyncProductsResult);
        tvSyncRewardsResult = (TextView) findViewById(R.id.Sync_tvSyncRewardsResult);
        tvSyncSalesResult = (TextView) findViewById(R.id.Sync_tvSyncSalesResult);

        bSync = (Button) findViewById(R.id.Sync_bSync);
        bSync.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                wifiDirectConnectivityDataPresenter.connectToCustomer(agentDevice, 3002);
                Toast.makeText(SynchronizeWithAgentActivity.this, "Connecting", Toast.LENGTH_SHORT).show();
                bSync.setEnabled(false);
            }
        });

        bClose = (Button) findViewById(R.id.Sync_bClose);
        bClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }

    private void synchronize() {
        SyncWithAgentTask syncWithAgentTask = new SyncWithAgentTask(this, 3002, this);
        syncWithAgentTask.execute();
//        Toast.makeText(SynchronizeWithAgentActivity.this, "Synchronizing", Toast.LENGTH_SHORT).show();
        showSyncStarted();
    }

    public void showSyncStarted() {

        tvSyncProductsResult.setText("Synchronizing");
        tvSyncRewardsResult.setText("Synchronizing");
        tvSyncSalesResult.setText("Synchronizing");

        pbSyncProducts.setVisibility(View.VISIBLE);
        pbSyncRewards.setVisibility(View.VISIBLE);
        pbSyncSales.setVisibility(View.VISIBLE);
    }

    public void markSyncProductsDone(int synchedProductCount) {
        pbSyncProducts.setVisibility(View.GONE);
        if (synchedProductCount > 0) {
            tvSyncProductsResult.setText(synchedProductCount + " products synced");
        } else {
            tvSyncProductsResult.setText("Done");
        }
    }

    public void markSyncRewardsDone(int syncedRewardsCount) {
        pbSyncRewards.setVisibility(View.GONE);
        if (syncedRewardsCount > 0) {
            tvSyncRewardsResult.setText(syncedRewardsCount + " rewards synced");
        } else {
            tvSyncRewardsResult.setText("Done");
        }
    }

    public void markSyncSalesDone(int syncedSalesCount) {
        pbSyncSales.setVisibility(View.GONE);
        if (syncedSalesCount > 0) {
            tvSyncSalesResult.setText(syncedSalesCount + " sales transactions synced");
        } else {
            tvSyncSalesResult.setText("Done");
        }
    }

    @Override
    public void onNewPeersDiscovered(List<WifiP2pDevice> wifiP2pDevices) {

        this.agentDeviceList.clear();
        this.agentDeviceList.addAll(wifiP2pDevices);
        agentDeviceListAdapter.notifyDataSetChanged();

        /*
        if(agentDeviceList.size() > 0){
            hideDialog();
            searchAgentTask.cancel(true);

            connectToAgent();

        }
        */

    }

    private void connectToAgent(){

        agentDevice = agentDeviceList.get(0);

        wifiDirectConnectivityDataPresenter.connectToCustomer(agentDevice, 3002);
        Toast.makeText(SynchronizeWithAgentActivity.this, "Connecting", Toast.LENGTH_SHORT).show();

    }

    @Override
    public void onConnectionEstablished() {
        synchronize();
        try {
            JSONObject json = new JSONObject(agentDevice.deviceName);
            tvConnectivity.setText(json.getString("O"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onConnectionTerminated() {
        //finish();
    }

    @Override
    public void onSyncComplete() {
        WifiManager wifiManager = (WifiManager) this.getSystemService(Context.WIFI_SERVICE);

        wifiManager.setWifiEnabled(false);

        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        wifiManager.setWifiEnabled(true);

    }

    @Override
    public void onProductsAcquired(List<Product> products) {
        markSyncProductsDone(products.size());

        //setProductsAsInActive();

        productDao.deleteAll();

        productDao.insertOrReplaceInTx(products);
    }

    private void setProductsAsInActive() {

        List<Product> productList = productDao.loadAll();

        for (Product product : productList) {

            product.setIs_active(false);
            productDao.update(product);

        }


    }

    @Override
    public void onRewardsAcquired(List<Reward> rewards) {
        markSyncRewardsDone(rewards.size());

        for (Reward reward : rewards) {

            Log.d(TAG, "ACQUIRED REWARDS :" + reward.getReward());

        }

        rewardDao.deleteAll();

        rewardDao.insertOrReplaceInTx(rewards);
    }

    @Override
    public void onSalesSent(List<Sales> sales) {
        markSyncSalesDone(sales.size());

        bSync.setVisibility(View.GONE);
        bClose.setVisibility(View.VISIBLE);

//        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_sync_with_agent, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        switch (id) {

            case R.id.action_SWA_sync_to_agent :

                if(!(agentDeviceList.size() > 0)){
                    startAgentDeviceSearch();
                }else{
                    connectToAgent();
                }


                break;

        }

        return super.onOptionsItemSelected(item);
    }

    private void startAgentDeviceSearch(){

        showProgressDialog();
        hideDialogLater(10000);

        searchAgentTask = new SearchAgentTask(
                this,
                wifiDirectConnectivityDataPresenter
        );
        searchAgentTask.execute();

    }

    private void showProgressDialog(){

        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setMessage("Searching for agent...");
        progressDialog.setIndeterminate(true);
        progressDialog.setCancelable(true);
        progressDialog.show();

    }

    private void hideDialog(){

        if(progressDialog.isShowing()){

            progressDialog.hide();

        }

    }

    protected void hideDialogLater(int hideAfterMillis) {
        new android.os.Handler().postDelayed(
                new Runnable() {
                    public void run() {

                        searchAgentTask.cancel(true);

                        if (progressDialog.isShowing()) {
                            hideDialog();

                            if(agentDeviceList.size() <= 0){

                                DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        switch (which){
                                            case DialogInterface.BUTTON_POSITIVE:
                                                //Yes button clicked

                                                if(!(agentDeviceList.size() > 0)){
                                                    startAgentDeviceSearch();
                                                }else{
                                                    connectToAgent();
                                                }

                                                break;

                                            case DialogInterface.BUTTON_NEGATIVE:
                                                //No button clicked
                                                break;
                                        }
                                    }
                                };

                                AlertDialog.Builder builder = new AlertDialog.Builder(SynchronizeWithAgentActivity.this);
                                builder
                                        .setMessage("No agent device found. Would you like to search again?")
                                        .setPositiveButton("Yes", dialogClickListener)
                                        .setNegativeButton("No", dialogClickListener).show();

                            }else{
                                connectToAgent();
                            }

                        }
                    }
                },
                hideAfterMillis);
    }


}
