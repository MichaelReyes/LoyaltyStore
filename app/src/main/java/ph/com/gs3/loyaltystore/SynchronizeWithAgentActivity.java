package ph.com.gs3.loyaltystore;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.wifi.WifiManager;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pManager;
import android.os.AsyncTask;
import android.os.Build;
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

import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import ph.com.gs3.loyaltystore.adapters.AgentDeviceListAdapter;
import ph.com.gs3.loyaltystore.globals.Constants;
import ph.com.gs3.loyaltystore.models.receivers.NetworkChangeStatusReceiver;
import ph.com.gs3.loyaltystore.models.sqlite.dao.CashReturn;
import ph.com.gs3.loyaltystore.models.sqlite.dao.CashReturnDao;
import ph.com.gs3.loyaltystore.models.sqlite.dao.Expenses;
import ph.com.gs3.loyaltystore.models.sqlite.dao.ItemReturn;
import ph.com.gs3.loyaltystore.models.sqlite.dao.ItemReturnDao;
import ph.com.gs3.loyaltystore.models.sqlite.dao.ItemStockCount;
import ph.com.gs3.loyaltystore.models.sqlite.dao.Product;
import ph.com.gs3.loyaltystore.models.sqlite.dao.ProductBreakdown;
import ph.com.gs3.loyaltystore.models.sqlite.dao.ProductBreakdownDao;
import ph.com.gs3.loyaltystore.models.sqlite.dao.ProductDao;
import ph.com.gs3.loyaltystore.models.sqlite.dao.ProductDelivery;
import ph.com.gs3.loyaltystore.models.sqlite.dao.ProductDeliveryDao;
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
        SyncWithAgentTask.SyncWithAgentTaskListener,
        NetworkChangeStatusReceiver.NetworkChangeStatusListener {

    public static final String TAG = SynchronizeWithAgentActivity.class.getSimpleName();

    public static final String EXTRA_AGENT_DEVICE = "agent_device";

    private TextView tvConnectivity;
    protected ProgressBar pbSyncProducts;
    protected ProgressBar pbSyncRewards;
    protected ProgressBar pbSyncSales;
    protected ProgressBar pbSyncDeliveries;

    protected TextView tvSyncProductsLabel;
    protected TextView tvSyncRewardsLabel;
    protected TextView tvSyncSalesLabel;
    protected TextView tvSyncDeliveriesLabel;

    protected TextView tvSyncProductsResult;
    protected TextView tvSyncRewardsResult;
    protected TextView tvSyncSalesResult;
    protected TextView tvSyncDeliveriesResult;

    private Button bSync;
    private Button bClose;

    private WifiP2pDevice agentDevice;
    private Retailer retailer;
    private WifiDirectConnectivityDataPresenter wifiDirectConnectivityDataPresenter;

    private ProductDao productDao;
    private RewardDao rewardDao;
    private ProductDeliveryDao productDeliveryDao;

    private NetworkChangeStatusReceiver networkChangeStatusReceiver;

    private AgentDeviceListAdapter agentDeviceListAdapter;
    private List<WifiP2pDevice> agentDeviceList;

    private ListView lvAgentDevice;

    private ProgressDialog searchAgnetProgressDialog;
    private ProgressDialog wifiProgressDialog;

    private SearchAgentTask searchAgentTask;

    private WifiManager wifiManager;

    private List<ProductDelivery> productDeliveriesforConfirmation;

    private android.os.Handler handler;
    private Runnable runnable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sync_with_agent);

        handler = new android.os.Handler();

        initializeConnectivity();
        initializeViews();
        initializeDataAccessObjects();

        if (!(agentDeviceList.size() > 0)) {
            startAgentDeviceSearch();
        } else {
            connectToAgent();
        }

    }

    private void initializeConnectivity() {

        wifiManager = (WifiManager) this.getSystemService(Context.WIFI_SERVICE);

        retailer = Retailer.getDeviceRetailerFromSharedPreferences(this);

        wifiDirectConnectivityDataPresenter = new WifiDirectConnectivityDataPresenter(
                this, retailer.getDeviceInfo()
        );

        agentDevice = new WifiP2pDevice();

        agentDeviceList = new ArrayList<>();
        agentDeviceListAdapter = new AgentDeviceListAdapter(this, agentDeviceList);

        lvAgentDevice = (ListView) findViewById(R.id.Sync_lvDeviceList);
        lvAgentDevice.setAdapter(agentDeviceListAdapter);

        wifiDirectConnectivityDataPresenter.discoverPeers(DeviceInfo.Type.AGENT);

    }

    @Override
    public void onChangeNetworkStatus(boolean isWifiEnabled) throws InterruptedException {

        if (isWifiEnabled) {

            if(wifiProgressDialog.isShowing()){
                wifiProgressDialog.cancel();
            }

            if (productDeliveriesforConfirmation.size() > 0) {
                Intent intent = new Intent(SynchronizeWithAgentActivity.this, ConfirmProductDeliveryActivity.class);
                Gson gson = new Gson();
                intent.putExtra(ConfirmProductDeliveryActivity.EXTRA_PRODUCT_DELIVERY_LIST, gson.toJson(productDeliveriesforConfirmation));
                startActivity(intent);
                finish();
            }

        } else {
            wifiProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            wifiProgressDialog.setMessage("Restarting wifi, Please wait...");
            wifiProgressDialog.setIndeterminate(true);
            wifiProgressDialog.setCancelable(false);
            wifiProgressDialog.show();
        }
    }

    private void initializeDataAccessObjects() {

        productDao = LoyaltyStoreApplication.getInstance().getSession().getProductDao();
        rewardDao = LoyaltyStoreApplication.getInstance().getSession().getRewardDao();
        productDeliveryDao = LoyaltyStoreApplication.getSession().getProductDeliveryDao();

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

        if (networkChangeStatusReceiver != null) {
            try {
                unregisterReceiver(networkChangeStatusReceiver);
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
            }
        }

    }

    private void restartWifi() {

        networkChangeStatusReceiver = new NetworkChangeStatusReceiver(this);
        registerReceiver(networkChangeStatusReceiver, networkChangeStatusReceiver.getIntentFilter());

        if (wifiManager.isWifiEnabled()) {
            wifiManager.setWifiEnabled(false);
            wifiManager.setWifiEnabled(true);
        } else {
            wifiManager.setWifiEnabled(true);
        }

    }

    private void disconnectPeers() {
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

        restartWifi();
    }

    private void initializeViews() {

        searchAgnetProgressDialog = new ProgressDialog(this);
        wifiProgressDialog = new ProgressDialog(this);

        tvConnectivity = (TextView) findViewById(R.id.Sync_tvConnectivity);

        pbSyncProducts = (ProgressBar) findViewById(R.id.Sync_pbSyncProductsProgress);
        pbSyncRewards = (ProgressBar) findViewById(R.id.Sync_pbSyncRewardsProgress);
        pbSyncSales = (ProgressBar) findViewById(R.id.Sync_pbSyncSalesProgress);
        pbSyncDeliveries = (ProgressBar) findViewById(R.id.Sync_pbSyncDeliveriesProgress);

        tvSyncProductsLabel = (TextView) findViewById(R.id.Sync_tvSyncProductsLabel);
        tvSyncRewardsLabel = (TextView) findViewById(R.id.Sync_tvSyncRewardsLabel);
        tvSyncSalesLabel = (TextView) findViewById(R.id.Sync_tvSyncSalesLabel);
        tvSyncDeliveriesLabel = (TextView) findViewById(R.id.Sync_tvSyncDeliveriesLabel);

        tvSyncProductsResult = (TextView) findViewById(R.id.Sync_tvSyncProductsResult);
        tvSyncRewardsResult = (TextView) findViewById(R.id.Sync_tvSyncRewardsResult);
        tvSyncSalesResult = (TextView) findViewById(R.id.Sync_tvSyncSalesResult);
        tvSyncDeliveriesResult = (TextView) findViewById(R.id.Sync_tvSyncDeliveriesResult);

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
        //syncWithAgentTask.execute();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB)
            syncWithAgentTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        else
            syncWithAgentTask.execute();
//        Toast.makeText(SynchronizeWithAgentActivity.this, "Synchronizing", Toast.LENGTH_SHORT).show();
        showSyncStarted();
    }

    public void showSyncStarted() {

        tvSyncProductsResult.setText("Synchronizing");
        tvSyncRewardsResult.setText("Synchronizing");
        tvSyncSalesResult.setText("Synchronizing");
        tvSyncDeliveriesResult.setText("Synchronizing");

        pbSyncProducts.setVisibility(View.VISIBLE);
        pbSyncRewards.setVisibility(View.VISIBLE);
        pbSyncSales.setVisibility(View.VISIBLE);
        pbSyncDeliveries.setVisibility(View.VISIBLE);
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

    public void markSyncDeliveriesDone(int syncedDeliveriesCount) {
        pbSyncDeliveries.setVisibility(View.GONE);
        if (syncedDeliveriesCount > 0) {
            tvSyncDeliveriesResult.setText(syncedDeliveriesCount + " deliveries synced");
        } else {
            tvSyncDeliveriesResult.setText("Done");
        }
    }

    @Override
    public void onNewPeersDiscovered(List<WifiP2pDevice> wifiP2pDevices) {

        this.agentDeviceList.clear();
        this.agentDeviceList.addAll(wifiP2pDevices);
        agentDeviceListAdapter.notifyDataSetChanged();

        if (wifiP2pDevices.size() > 0){
            hideDialog();
        }

    }

    private void connectToAgent() {

        agentDevice = agentDeviceList.get(0);

        wifiDirectConnectivityDataPresenter.connectToCustomer(agentDevice, 3002);
        Toast.makeText(SynchronizeWithAgentActivity.this, "Connecting to agent...", Toast.LENGTH_SHORT).show();

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
    public void onProductsAcquired(List<Product> products) {
        markSyncProductsDone(products.size());

        //setProductsAsInActive();

        productDao.deleteAll();

        productDao.insertOrReplaceInTx(products);

        /*
        Log.v(TAG, "========== PRODUCTS ACQUIRED START ==========");

        for (Product product : products) {
            long id = productDao.insertOrReplace(product);

            Log.v(TAG, "Product id: " + id);
            Log.v(TAG, "Product name: " + product.getName());
            Log.v(TAG, "Product SKU: " + product.getSku());
            Log.v(TAG, "Product cost: " + product.getUnit_cost());
            Log.v(TAG, "Product Quantity to deduct: " + product.getDeduct_product_to_quantity());

        }

        Log.v(TAG, "========== PRODUCTS ACQUIRED END ==========");
        */

    }

    @Override
    public void onProductsBreakdownAcquired(List<ProductBreakdown> productBreakdownList) {

        ProductBreakdownDao productBreakdownDao =
                LoyaltyStoreApplication.getSession().getProductBreakdownDao();

        productBreakdownDao.deleteAll();

        productBreakdownDao.insertOrReplaceInTx(productBreakdownList);

        List<ProductBreakdown> allProductBreakdown = productBreakdownDao.loadAll();

        /*
        Log.d(TAG, "======================= PRODUCT BREAKDOWN =======================");

        for (ProductBreakdown productBreakdown : allProductBreakdown) {

            Log.d(TAG, "id : " + productBreakdown.getId());
            Log.d(TAG, "name : " + productBreakdown.getName());
            Log.d(TAG, "product id : " + productBreakdown.getProduct_id());
            Log.d(TAG, "quantity : " + productBreakdown.getQuantity());
            Log.d(TAG, "ts : " + productBreakdown.getTs());

        }
        Log.d(TAG, "==================================================================");
        */
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
    public void onProductDeliveriesAcquired(List<ProductDelivery> productDeliveries) {
        markSyncDeliveriesDone(productDeliveries.size());
        productDeliveryDao.insertOrReplaceInTx(productDeliveries);
        productDeliveriesforConfirmation = productDeliveries;

    }

    @Override
    public void onSalesSent(List<Sales> sales) {
        markSyncSalesDone(sales.size());

    }

    @Override
    public void onItemReturnSentAndAcquiredProcessed(List<ItemReturn> itemReturns, List<ItemReturn> processedItemReturns) {

        ItemReturnDao itemReturnDao =
                LoyaltyStoreApplication.getSession().getItemReturnDao();

        SimpleDateFormat formatter = Constants.SIMPLE_DATE_TIME_FORMAT;

        for (ItemReturn processedItemReturn : processedItemReturns) {

            List<ItemReturn> itemReturnList =
                    itemReturnDao
                            .queryBuilder()
                            .where(
                                    ItemReturnDao.Properties.Product_name.eq(
                                            processedItemReturn.getProduct_name()
                                    )
                            ).list();

            for (ItemReturn itemReturn : itemReturnList) {
                try {
                    Date itemReturnDate = formatter.parse(formatter.format(itemReturn.getDate_created()));
                    Date processedItemReturnDate = formatter.parse(formatter.format(processedItemReturn.getDate_created()));

                    if (itemReturnDate.compareTo(processedItemReturnDate) == 0) {
                        itemReturn.setStatus(processedItemReturn.getStatus());
                        itemReturnDao.insertOrReplace(itemReturn);
                    }
                } catch (ParseException e) {
                    e.printStackTrace();
                }


            }

        }


    }

    @Override
    public void onCashReturnSentAndAcquiredProcessed(List<CashReturn> cashReturns, List<CashReturn> processedCashReturns) {

        CashReturnDao cashReturnDao
                = LoyaltyStoreApplication.getSession().getCashReturnDao();

        cashReturnDao.insertOrReplaceInTx(processedCashReturns);

    }

    @Override
    public void onItemStockCountSent(List<ItemStockCount> itemStockCountList) {

    }

    @Override
    public void onExpensesSent(List<Expenses> expensesList) {


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

            case R.id.action_SyncWithAgent_sync_to_agent:

                if (!(agentDeviceList.size() > 0)) {
                    startAgentDeviceSearch();
                } else {
                    connectToAgent();
                }


                break;

        }

        return super.onOptionsItemSelected(item);
    }

    private void startAgentDeviceSearch() {

        showProgressDialog("Searching for agent...");
        hideDialogLater(10000);

        searchAgentTask = new SearchAgentTask(
                this,
                wifiDirectConnectivityDataPresenter
        );
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB)
            searchAgentTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        else
            searchAgentTask.execute();

    }

    private void showProgressDialog(String message) {

        searchAgnetProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        searchAgnetProgressDialog.setMessage(message);
        searchAgnetProgressDialog.setIndeterminate(true);
        searchAgnetProgressDialog.setCancelable(false);

        searchAgnetProgressDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                checkAgentDeviceList();
            }
        });

        searchAgnetProgressDialog.show();

    }

    private void hideDialog() {

        if (searchAgnetProgressDialog.isShowing()) {

            searchAgnetProgressDialog.cancel();
            searchAgentTask.cancel(true);

            if (runnable != null) {
                handler.removeCallbacks(runnable);
            }

            if (networkChangeStatusReceiver != null) {
                try {
                    unregisterReceiver(networkChangeStatusReceiver);
                } catch (IllegalArgumentException e) {
                    e.printStackTrace();
                }
            }

        }

    }

    protected void hideDialogLater(int hideAfterMillis) {
        handler.postDelayed(
                runnable = new Runnable() {
                    public void run() {
                        hideDialog();
                    }
                },
                hideAfterMillis);
    }

    private void checkAgentDeviceList() {

        if (agentDeviceList.size() <= 0) {

            DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    switch (which) {
                        case DialogInterface.BUTTON_POSITIVE:
                            //Yes button clicked

                            if (!(agentDeviceList.size() > 0)) {
                                startAgentDeviceSearch();
                            } else {
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

        } else {
            connectToAgent();
        }

    }

    @Override
    public void onTaskDone() {

        disconnectPeers();

        bSync.setVisibility(View.GONE);
        bClose.setVisibility(View.VISIBLE);

    }

    @Override
    public void onSocketConnectFailed() {
        synchronize();
    }
}
