package ph.com.gs3.loyaltystore;

import android.app.Activity;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import ph.com.gs3.loyaltystore.models.sqlite.dao.Product;
import ph.com.gs3.loyaltystore.models.sqlite.dao.ProductDao;
import ph.com.gs3.loyaltystore.models.sqlite.dao.Reward;
import ph.com.gs3.loyaltystore.models.sqlite.dao.RewardDao;
import ph.com.gs3.loyaltystore.models.sqlite.dao.Sales;
import ph.com.gs3.loyaltystore.models.tasks.SyncWithAgentTask;
import ph.com.gs3.loyaltystore.models.values.Retailer;
import ph.com.gs3.loyaltystore.presenters.WifiDirectConnectivityDataPresenter;

/**
 * Created by Bryan-PC on 15/02/2016.
 */
public class SynchronizeWithAgentActivity extends Activity implements WifiDirectConnectivityDataPresenter.WifiDirectConnectivityPresentationListener, SyncWithAgentTask.SyncWithAgentTaskListener {

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

    private WifiP2pDevice agentDevice;
    private Retailer retailer;
    private WifiDirectConnectivityDataPresenter wifiDirectConnectivityDataPresenter;

    private ProductDao productDao;
    private RewardDao rewardDao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sync_with_agent);

        initializeConnectivity();
        initializeViews();
        initializeDataAccessObjects();

    }


    private void initializeConnectivity() {
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
    }

    private void initializeDataAccessObjects(){

        productDao = LoyaltyStoreApplication.getInstance().getSession().getProductDao();
        rewardDao = LoyaltyStoreApplication.getInstance().getSession().getRewardDao();

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
            }
        });
    }

    private void synchronize() {
        SyncWithAgentTask syncWithAgentTask = new SyncWithAgentTask(3002, this);
        syncWithAgentTask.execute();
        Toast.makeText(SynchronizeWithAgentActivity.this, "Synchronizing", Toast.LENGTH_SHORT).show();
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
            tvSyncProductsResult.setText(tvSyncSalesResult + " sales transactions synced");
        } else {
            tvSyncProductsResult.setText("Done");
        }
    }

    @Override
    public void onNewPeersDiscovered(List<WifiP2pDevice> wifiP2pDevices) {

        if (wifiDirectConnectivityDataPresenter.getLastConnectivityState().isConnectedToDevice()) {
            synchronize();
        }

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
        finish();
    }

    @Override
    public void onProductsAcquired(List<Product> products) {
        markSyncProductsDone(products.size());

        productDao.insertInTx(products);
    }

    @Override
    public void onRewardsAcquired(List<Reward> rewards) {
        markSyncRewardsDone(rewards.size());

        rewardDao.insertInTx(rewards);
    }

    @Override
    public void onSalesSent(List<Sales> sales) {
        markSyncSalesDone(sales.size());
    }
}
