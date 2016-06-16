package ph.com.gs3.loyaltystore;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import de.greenrobot.dao.query.QueryBuilder;
import ph.com.gs3.loyaltystore.adapters.DynamicMenuButtonListViewAdapter;
import ph.com.gs3.loyaltystore.adapters.InventoryViewAdapter;
import ph.com.gs3.loyaltystore.adapters.ProductsForDeliveryListAdapter;
import ph.com.gs3.loyaltystore.adapters.SalesProductWithReturnListAdapter;
import ph.com.gs3.loyaltystore.adapters.ViewPagerAdapter;
import ph.com.gs3.loyaltystore.fragments.DeliveriesForConfirmationFragment;
import ph.com.gs3.loyaltystore.fragments.DeliveryFragment;
import ph.com.gs3.loyaltystore.fragments.DeliveryHistoryFragment;
import ph.com.gs3.loyaltystore.fragments.ExpensesFragment;
import ph.com.gs3.loyaltystore.fragments.InventoryFragment;
import ph.com.gs3.loyaltystore.fragments.InvoiceFragment;
import ph.com.gs3.loyaltystore.fragments.ItemInventoryDetailsFragment;
import ph.com.gs3.loyaltystore.fragments.ItemStockCountDetailsFragment;
import ph.com.gs3.loyaltystore.fragments.ReturnsToCommissaryFragment;
import ph.com.gs3.loyaltystore.fragments.RewardViewFragment;
import ph.com.gs3.loyaltystore.fragments.SalesFragment;
import ph.com.gs3.loyaltystore.fragments.SalesProductsViewFragment;
import ph.com.gs3.loyaltystore.fragments.SettingsFragment;
import ph.com.gs3.loyaltystore.fragments.StoreAccountSettingsFragment;
import ph.com.gs3.loyaltystore.fragments.StoreSalesInventoryDetailsFragment;
import ph.com.gs3.loyaltystore.fragments.SyncDataFragment;
import ph.com.gs3.loyaltystore.fragments.SynchronizeOnWebFragment;
import ph.com.gs3.loyaltystore.fragments.SynchronizeViewFragment;
import ph.com.gs3.loyaltystore.fragments.TabMaintenanceSettingsFragment;
import ph.com.gs3.loyaltystore.globals.Constants;
import ph.com.gs3.loyaltystore.models.TabMaintenance;
import ph.com.gs3.loyaltystore.models.User;
import ph.com.gs3.loyaltystore.models.receivers.AlarmBroadcastReceiver;
import ph.com.gs3.loyaltystore.models.receivers.NetworkChangeStatusReceiver;
import ph.com.gs3.loyaltystore.models.receivers.ServerSyncServiceBroadcastReceiver;
import ph.com.gs3.loyaltystore.models.services.GetAvailableStoresOnWebService;
import ph.com.gs3.loyaltystore.models.services.GetProductsForDeliveryService;
import ph.com.gs3.loyaltystore.models.services.RegisterStoreOnWebService;
import ph.com.gs3.loyaltystore.models.services.ServerSyncService;
import ph.com.gs3.loyaltystore.models.services.UpdateInventoryAndSyncOtherDataService;
import ph.com.gs3.loyaltystore.models.sqlite.dao.CashReturn;
import ph.com.gs3.loyaltystore.models.sqlite.dao.CashReturnDao;
import ph.com.gs3.loyaltystore.models.sqlite.dao.DaoManager;
import ph.com.gs3.loyaltystore.models.sqlite.dao.Expenses;
import ph.com.gs3.loyaltystore.models.sqlite.dao.ItemInventory;
import ph.com.gs3.loyaltystore.models.sqlite.dao.ItemInventoryDao;
import ph.com.gs3.loyaltystore.models.sqlite.dao.ItemReturn;
import ph.com.gs3.loyaltystore.models.sqlite.dao.ItemReturnDao;
import ph.com.gs3.loyaltystore.models.sqlite.dao.ItemStockCount;
import ph.com.gs3.loyaltystore.models.sqlite.dao.ItemStockCountDao;
import ph.com.gs3.loyaltystore.models.sqlite.dao.Product;
import ph.com.gs3.loyaltystore.models.sqlite.dao.ProductBreakdown;
import ph.com.gs3.loyaltystore.models.sqlite.dao.ProductBreakdownDao;
import ph.com.gs3.loyaltystore.models.sqlite.dao.ProductDao;
import ph.com.gs3.loyaltystore.models.sqlite.dao.ProductDelivery;
import ph.com.gs3.loyaltystore.models.sqlite.dao.ProductDeliveryDao;
import ph.com.gs3.loyaltystore.models.sqlite.dao.ProductForDelivery;
import ph.com.gs3.loyaltystore.models.sqlite.dao.ProductForDeliveryDao;
import ph.com.gs3.loyaltystore.models.sqlite.dao.Reward;
import ph.com.gs3.loyaltystore.models.sqlite.dao.RewardDao;
import ph.com.gs3.loyaltystore.models.sqlite.dao.Sales;
import ph.com.gs3.loyaltystore.models.sqlite.dao.SalesDao;
import ph.com.gs3.loyaltystore.models.sqlite.dao.SalesProduct;
import ph.com.gs3.loyaltystore.models.sqlite.dao.SalesProductDao;
import ph.com.gs3.loyaltystore.models.sqlite.dao.Store;
import ph.com.gs3.loyaltystore.models.sqlite.dao.StoreDao;
import ph.com.gs3.loyaltystore.models.tasks.AcceptProductsTask;
import ph.com.gs3.loyaltystore.models.tasks.ConfirmProductsForDeliveryTask;
import ph.com.gs3.loyaltystore.models.tasks.SearchAgentTask;
import ph.com.gs3.loyaltystore.models.tasks.SyncWithAgentTask;
import ph.com.gs3.loyaltystore.models.tasks.UserDeviceLogoutTask;
import ph.com.gs3.loyaltystore.models.values.DeviceInfo;
import ph.com.gs3.loyaltystore.models.values.Retailer;
import ph.com.gs3.loyaltystore.presenters.WifiDirectConnectivityDataPresenter;

/**
 * Created by Bryan-PC on 22/04/2016.
 */
public class MainActivity2 extends AppCompatActivity implements
        WifiDirectConnectivityDataPresenter.WifiDirectConnectivityPresentationListener,
        SyncWithAgentTask.SyncWithAgentTaskListener,
        NetworkChangeStatusReceiver.NetworkChangeStatusListener,
        ViewPager.OnPageChangeListener,
        InvoiceFragment.InvoiceViewFragmentListener,
        SynchronizeViewFragment.SynchronizeViewFragmentListener,
        StoreSalesInventoryDetailsFragment.StoreSalesInventoryDetailsFragmentListener,
        ItemStockCountDetailsFragment.ItemStockCountDetailsFragmentListener,
        ItemInventoryDetailsFragment.ItemInventoryDetailsFragmentListener,
        ReturnsToCommissaryFragment.ReturnsToCommissaryViewFragmentListener,
        ExpensesFragment.ExpenseFragmentListener,
        SalesFragment.SalesFragmentListener,
        StoreAccountSettingsFragment.StoreAccountSettingsFragmentListener,
        TabMaintenanceSettingsFragment.TabMaintenanceSettingsFragmentListener,
        DeliveryHistoryFragment.DeliveryHistoryFragmentListener,
        DeliveriesForConfirmationFragment.DeliveriesForConfirmationFragmentListener,
        ServerSyncServiceBroadcastReceiver.EventListener,
        SynchronizeOnWebFragment.SynchronizeOnWebEventListener,
        InventoryViewAdapter.InventoryViewAdapterListener,
        SalesProductWithReturnListAdapter.SalesProductWithReturnListAdapterListener,
        DynamicMenuButtonListViewAdapter.DynamicMenuButtonListViewAdapterListener,
        ProductsForDeliveryListAdapter.ProductsForDeliveryListAdapterListener,
        AlarmBroadcastReceiver.AlarmBroadcastReceiverListener{

    public static final String TAG = MainActivity2.class.getSimpleName();

    private ServerSyncServiceBroadcastReceiver serverSyncServiceBroadcastReceiver;
    private NetworkChangeStatusReceiver networkChangeStatusReceiver;
    private AlarmBroadcastReceiver alarmBroadcastReceiver;

    private TabLayout tabLayout;
    private ViewPager viewPager;
    private ViewPagerAdapter viewPagerAdapter;

    private Fragment currentFragment;

    private List<Reward> rewards;
    private float totalDiscount = 0;


    private SearchAgentTask searchAgentTask;

    private WifiManager wifiManager;

    private ProgressDialog searchAgentProgressDialog;
    private ProgressDialog wifiProgressDialog;
    private ProgressDialog storeAccountProgressDialog;

    private WifiDirectConnectivityDataPresenter wifiDirectConnectivityDataPresenter;

    private Retailer retailer;

    private android.os.Handler handler;
    private Runnable runnable;

    private List<ProductDelivery> productDeliveriesforConfirmation;

    private boolean isDeviceRegisterd;

    private int minMenuListCountLimit = 0;
    private int maxMenuListCountLimit = 100;
    private int maxInventoryCountLimit = 100;

    private EditText etQRCodeResult;

    private boolean doubleBackToExitPressedOnce = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getWindow().requestFeature(Window.FEATURE_ACTION_BAR);
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_main_2);

        handler = new android.os.Handler();

        isDeviceRegisterd = false;

        initializeViews();
        initializeConnectivity();
        initializeBroadcastReceiver();

        //  the user must login first
        User currentUser = User.getSavedUser(this);
        if (currentUser == null || (currentUser != null && currentUser.getId() == 0)) {
            Log.d(TAG, "NO USER DETECTED");
            onAuthenticationNeeded();
            return;
        }

        TabMaintenance tabMaintenance = TabMaintenance.getTabMaintenanceFromSharedPreferences(this);
        if (tabMaintenance.isSynchronizeByWebTabActive())
            updateInventory();

        /*ProductForDeliveryDao productForDeliveryDao
                = LoyaltyStoreApplication.getSession().getProductForDeliveryDao();

        for(ProductForDelivery productForDelivery : productForDeliveryDao.loadAll()){

            Log.d(TAG," name >>>>>>>>>" + productForDelivery.getName());
            Log.d(TAG," status >>>>>>>>>" + productForDelivery.getStatus());

        }*/

        alarmBroadcastReceiver = new AlarmBroadcastReceiver();
        alarmBroadcastReceiver.setAlarm(MainActivity2.this);

        /*// Set the alarm to start at approximately 2:00 p.m.
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.set(Calendar.HOUR_OF_DAY, 16);
        calendar.set(Calendar.MINUTE,53);

        AlarmManager am =( AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent i = new Intent(MainActivity2.this, AlarmBroadcastReceiver.class);
        PendingIntent pi = PendingIntent.getBroadcast(MainActivity2.this, 0, i, 0);
        //am.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), 1000 * 60 * 10, pi); // Millisec * Second * Minute

        am.set(AlarmManager.ELAPSED_REALTIME_WAKEUP,
                SystemClock.elapsedRealtime() +
                        60 * 1000, pi);

        am.setInexactRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(),
                AlarmManager.INTERVAL_DAY, pi);
*/
    }

    @Override
    public void onAlarm() {
        showToastMessage("ON ALARM!!!!!", Toast.LENGTH_LONG,"INFORMATION");
    }

    @Override
    protected void onResume() {
        super.onResume();

        /*ItemInventoryDao itemInventoryDao
                = LoyaltyStoreApplication.getSession().getItemInventoryDao();

        List<ItemInventory> itemInventoryList
                = itemInventoryDao.loadAll();

        Log.e(TAG, "---------------------ITEM INVENTORY---------------------");

        for(ItemInventory itemInventory : itemInventoryList){

            Log.e(TAG,"id : " + itemInventory.getProduct_id());
            Log.e(TAG, "name : " + itemInventory.getName());
            Log.e(TAG, "quantity : " + itemInventory.getQuantity());

        }

        Log.e(TAG, "---------------------ITEM INVENTORY---------------------");*/

        wifiDirectConnectivityDataPresenter.onResume();

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

        if (serverSyncServiceBroadcastReceiver != null) {
            unregisterReceiver(serverSyncServiceBroadcastReceiver);
        }


        if (searchAgentProgressDialog != null) {
            searchAgentProgressDialog.dismiss();
        }

        if (storeAccountProgressDialog != null) {
            storeAccountProgressDialog.dismiss();
        }

        if (wifiProgressDialog != null) {
            wifiProgressDialog.dismiss();
        }

    }

    private void initializeConnectivity() {

        wifiManager = (WifiManager) this.getSystemService(Context.WIFI_SERVICE);
        retailer = Retailer.getDeviceRetailerFromSharedPreferences(this);
        wifiDirectConnectivityDataPresenter = new WifiDirectConnectivityDataPresenter(
                this, retailer.getDeviceInfo()
        );

        wifiDirectConnectivityDataPresenter.discoverPeers(DeviceInfo.Type.AGENT);
    }

    private void initializeViews() {

        //Log.d(TAG, "MainActivity2 initializeViews");

        searchAgentProgressDialog = new ProgressDialog(this);
        wifiProgressDialog = new ProgressDialog(this);
        storeAccountProgressDialog = new ProgressDialog(this);

        rewards = new ArrayList<>();

        viewPager = (ViewPager) findViewById(R.id.Main2_viewpager);
        viewPager.addOnPageChangeListener(this);

        setupViewPager(viewPager);

        tabLayout = (TabLayout) findViewById(R.id.Main2_tabs);
        tabLayout.setupWithViewPager(viewPager);
    }


    private void setupViewPager(ViewPager viewPager) {
        Bundle extras = getIntent().getExtras();

        //Log.d(TAG, "MainActivity2 setupViewPager");

        viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager(),
                extras);

        TabMaintenance tabMaintenance = TabMaintenance.getTabMaintenanceFromSharedPreferences(this);

        //Log.d(TAG, "SYNC ON DRIVER ACTIVE " + tabMaintenance.isSynchronizeByDriverTabActive());
        //Log.d(TAG, "SYNC ON WEB ACTIVE " + tabMaintenance.isSynchronizeByWebTabActive());

        if (tabMaintenance.isInvoiceTabActive())
            viewPagerAdapter.
                    addFragment(new InvoiceFragment(), "Invoice");
        if (tabMaintenance.isSynchronizeTabActive())
            if (tabMaintenance.isSynchronizeByDriverTabActive())
                viewPagerAdapter.addFragment(new SynchronizeOnWebFragment(), "Synchronize");
            else if (tabMaintenance.isSynchronizeByWebTabActive())
                viewPagerAdapter.addFragment(new SynchronizeOnWebFragment(), "Synchronize");
        if (tabMaintenance.isInventoryTabActive())
            viewPagerAdapter.addFragment(new InventoryFragment(), "Inventory");
        if (tabMaintenance.isSalesTabActive())
            viewPagerAdapter.addFragment(new SalesFragment(), "Sales");
        if (tabMaintenance.isDeliveryTabActive())
            viewPagerAdapter.addFragment(new DeliveryFragment(), "Delivery");

        viewPagerAdapter.addFragment(new SettingsFragment(), "Settings");

        viewPager.setAdapter(viewPagerAdapter);

        currentFragment = viewPagerAdapter.getItem(0);

        /*if(currentFragment instanceof InvoiceViewFragment){
            ((InvoiceViewFragment) currentFragment).resetView();
        }*/

    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        currentFragment = (Fragment) viewPagerAdapter.instantiateItem(viewPager, position);

        if (currentFragment instanceof InvoiceFragment) {

            Retailer retailer = Retailer.getDeviceRetailerFromSharedPreferences(this);

            ((InvoiceFragment) currentFragment).resetView();
            onRefreshProductMenuList();

            /*((InvoiceFragment) currentFragment).setProducts(
                    getProductsForRetail() == null ? new ArrayList<Product>() : getProductsForRetail()
            );*/

        } else if (currentFragment instanceof SynchronizeViewFragment) {

            //startAgentDeviceSearch();

        } else if (currentFragment instanceof InventoryFragment) {

            maxInventoryCountLimit = 100;

            ((InventoryFragment) currentFragment).initializeViews();
            //((InventoryFragment) currentFragment).loadStoreSalesInventory();

        } else if (currentFragment instanceof SettingsFragment) {

            ((SettingsFragment) currentFragment).initializeViews();
            //((SettingsFragment) currentFragment).loadStoreAccount();

        } else if (currentFragment instanceof DeliveryFragment) {

            ((DeliveryFragment) currentFragment).initializeViews();

        }
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    @Override
    public void onLoadItemInventoryStockCount(Date date) {

        if (currentFragment instanceof InventoryFragment) {
            ((InventoryFragment) currentFragment).loadItemInventoryStockCount();
        }

    }

    @Override
    public List<ItemInventory> getItemInventory() {
        List<ItemInventory> inventories = new ArrayList<>();

        ProductDao productDao
                = LoyaltyStoreApplication.getSession().getProductDao();

        ItemInventoryDao itemInventoryDao
                = LoyaltyStoreApplication.getSession().getItemInventoryDao();

        long productCount = productDao
                .queryBuilder()
                .whereOr(
                        ProductDao.Properties.Type.eq("Product for Delivery"),
                        ProductDao.Properties.Type.eq("For Direct Transactions")
                ).count();

        if (productCount < maxInventoryCountLimit) {
            maxInventoryCountLimit = (int) productCount;
        }

        List<Product> products =
                productDao
                        .queryBuilder()
                        .whereOr(
                                ProductDao.Properties.Type.eq("Product for Delivery"),
                                ProductDao.Properties.Type.eq("For Direct Transactions")
                        ).orderAsc(ProductDao.Properties.Name).limit(maxInventoryCountLimit).offset(0).list();

        for (Product product : products) {

            List<ItemInventory> itemInventoryList
                    = itemInventoryDao
                    .queryBuilder()
                    .where(
                            ItemInventoryDao.Properties.Product_id.eq(
                                    product.getId()
                            )
                    ).list();

            for (ItemInventory itemInventory : itemInventoryList) {
                inventories.add(itemInventory);
            }

        }

        return inventories;
    }

    @Override
    public void onItemClick(final ItemInventory item) {

        currentFragment = viewPagerAdapter.getRegisteredFragment(viewPager.getCurrentItem());

        if (currentFragment instanceof InventoryFragment) {

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle(item.getName());

            StringBuilder sb = new StringBuilder();
            sb.append("Expected Output : " + item.getQuantity());
            sb.append(System.getProperty("line.separator"));
            sb.append(System.getProperty("line.separator"));

            builder.setMessage(sb.toString());

            final EditText inputPhysicalCount = new EditText(MainActivity2.this);
            inputPhysicalCount.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    inputPhysicalCount.setError(null);
                }

                @Override
                public void afterTextChanged(Editable s) {

                }
            });
            inputPhysicalCount.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
            TextView tvPhysicalCount = new TextView(this);
            tvPhysicalCount.setText("Physical Count : ");

            final EditText inputRemarks = new EditText(this);
            inputRemarks.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    inputRemarks.setError(null);
                }

                @Override
                public void afterTextChanged(Editable s) {

                }
            });
            TextView tvRemarks = new TextView(this);
            tvRemarks.setText("Remarks : ");

            LinearLayout layout = new LinearLayout(MainActivity2.this);
            layout.setOrientation(LinearLayout.VERTICAL);

            layout.addView(tvPhysicalCount);
            layout.addView(inputPhysicalCount);
            layout.addView(tvRemarks);
            layout.addView(inputRemarks);

            builder.setView(layout);

            // Set up the buttons
            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                }
            });
            builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            });

            final AlertDialog dialog = builder.create();

            dialog.show();

            dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if ("".equals(inputPhysicalCount.getText().toString().trim())) {

                        inputPhysicalCount.setError("This field is required!");
                        return;

                    }

                    if (item.getQuantity() != Double.parseDouble(inputPhysicalCount.getText().toString())
                            && "".equals(inputRemarks.getText().toString().trim())) {

                        inputRemarks.setError("This field is required if the value" +
                                " of Expected Output and Physical Count is not equal.");

                        return;
                    }

                    double quantity = Double.parseDouble(inputPhysicalCount.getText().toString());

                    Retailer retailer = Retailer.getDeviceRetailerFromSharedPreferences(MainActivity2.this);

                    ItemStockCount itemStockCount = new ItemStockCount();
                    itemStockCount.setProduct_id(item.getProduct_id());
                    itemStockCount.setName(item.getName());
                    itemStockCount.setExpectedQuantity(item.getQuantity());
                    itemStockCount.setQuantity(quantity);
                    itemStockCount.setRemarks(inputRemarks.getText().toString());
                    itemStockCount.setStore_id(retailer.getStoreId());

                    ((InventoryFragment) currentFragment).addItemStockCount(itemStockCount);

                    dialog.dismiss();

                }
            });

        }
    }

    @Override
    public void onLoadMoreInventory() {
        if (currentFragment instanceof InventoryFragment) {
            maxInventoryCountLimit += 100;
            ((InventoryFragment) currentFragment).loadMoreItemInventory();
        }
    }

    @Override
    public void onItemStockCountClicked(ItemStockCount itemStockCount) {
        if (currentFragment instanceof InventoryFragment) {
            if (!"".equals(itemStockCount.getRemarks())) {
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle(itemStockCount.getName());

                StringBuilder sb = new StringBuilder();
                sb.append("Expected Output : " + itemStockCount.getExpectedQuantity());
                sb.append(System.getProperty("line.separator"));
                sb.append("Physical Count : " + itemStockCount.getQuantity());
                sb.append(System.getProperty("line.separator"));
                sb.append("Remarks : " + itemStockCount.getRemarks());

                builder.setMessage(sb.toString());

                builder.setNegativeButton("Close", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

                builder.show();

            }
        }
    }

    @Override
    public void onSaveItemStockCount(List<ItemStockCount> stockCountList) {
        if (currentFragment instanceof InventoryFragment) {
            ItemStockCountDao itemStockCountDao
                    = LoyaltyStoreApplication.getSession().getItemStockCountDao();
            ItemInventoryDao itemInventoryDao
                    = LoyaltyStoreApplication.getSession().getItemInventoryDao();


            Date currDate = new Date();

            for (ItemStockCount itemStockCount : stockCountList) {

                long productId = itemStockCount.getProduct_id();

                itemStockCount.setIs_synced(false);
                itemStockCount.setDate_counted(currDate);
                itemStockCountDao.insertOrReplace(itemStockCount);

                List<ItemInventory> itemInventoryList
                        = itemInventoryDao
                        .queryBuilder()
                        .where(
                                ItemInventoryDao.Properties.Product_id.eq(productId)
                        ).limit(1).list();

                if (itemInventoryList.size() > 0) {

                    ItemInventory itemInventory = itemInventoryList.get(0);

                    itemInventory.setQuantity(itemStockCount.getQuantity());
                    itemInventory.setIs_updated(true);
                    itemInventoryDao.insertOrReplace(itemInventory);

                }

            }

            ((InventoryFragment) currentFragment).clearItemStockCountList();

            updateInventory();
        }
    }

    private List<Product> getProductsForRetail() {

        String searchString = "";
        String searchCategory = "";

        if (currentFragment instanceof InvoiceFragment) {
            searchString = ((InvoiceFragment) currentFragment).getSearchString();
            searchCategory = ((InvoiceFragment) currentFragment).getSelectedCategory();
        }

        ProductDao productDao
                = LoyaltyStoreApplication.getSession().getProductDao();

        List<Product> productList;

        QueryBuilder queryBuilder = productDao.queryBuilder();

        if (!"".equals(searchString)) {

            queryBuilder.whereOr(
                    ProductDao.Properties.Name.like(
                            "%" + searchString + "%"
                    ),
                    ProductDao.Properties.Category.like(
                            "%" + searchString + "%"
                    )
            ).whereOr(
                    ProductDao.Properties.Type.eq("Product for Retail"),
                    ProductDao.Properties.Type.eq("For Direct Transactions")
            );

            /*
            long productCount = productDao
                    .queryBuilder()
                    .whereOr(
                            ProductDao.Properties.Name.like(
                                    "%" + searchString + "%"
                            ),
                            ProductDao.Properties.Category.like(
                                    "%" + searchString + "%"
                            )
                    ).whereOr(
                            ProductDao.Properties.Type.eq("Product for Retail"),
                            ProductDao.Properties.Type.eq("For Direct Transactions")
                    ).count();

            if (productCount < maxMenuListCountLimit) {
                maxMenuListCountLimit = (int) productCount;
            }

            productList = productDao
                    .queryBuilder()
                    .whereOr(
                            ProductDao.Properties.Name.like(
                                    "%" + searchString + "%"
                            ),
                            ProductDao.Properties.Category.like(
                                    "%" + searchString + "%"
                            )
                    ).whereOr(
                            ProductDao.Properties.Type.eq("Product for Retail"),
                            ProductDao.Properties.Type.eq("For Direct Transactions")
                    ).limit(maxMenuListCountLimit).offset(minMenuListCountLimit).list();
            */
        } else {

            queryBuilder.whereOr(
                    ProductDao.Properties.Type.eq("Product for Retail"),
                    ProductDao.Properties.Type.eq("For Direct Transactions")
            );

            /*
            long productCount = productDao
                    .queryBuilder()
                    .whereOr(
                            ProductDao.Properties.Type.eq("Product for Retail"),
                            ProductDao.Properties.Type.eq("For Direct Transactions")
                    ).count();

            if (productCount < maxMenuListCountLimit) {
                maxMenuListCountLimit = (int) productCount;
            }

            productList = productDao
                    .queryBuilder()
                    .whereOr(
                            ProductDao.Properties.Type.eq("Product for Retail"),
                            ProductDao.Properties.Type.eq("For Direct Transactions")
                    ).limit(maxMenuListCountLimit).offset(minMenuListCountLimit).list();
            */
        }

        if (!"None".equals(searchCategory)) {
            queryBuilder.where(
                    ProductDao.Properties.Category.eq(searchCategory)
            );
        }

        long count = queryBuilder.count();

        if (count < maxMenuListCountLimit) {
            maxMenuListCountLimit = (int) count;
        }

        Log.d(TAG, "minMenuListCountLimit : " + minMenuListCountLimit);
        Log.d(TAG, "maxMenuListCountLimit : " + maxMenuListCountLimit);

        productList = queryBuilder.limit(maxMenuListCountLimit)
                .offset(minMenuListCountLimit).list();

        return productList;

    }

    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            /*super.onBackPressed();
            return;*/

            onSignOut();

        }

        this.doubleBackToExitPressedOnce = true;
        //Toast.makeText(this, "Please press BACK again to exit", Toast.LENGTH_SHORT).show();
        showToastMessage("Please press BACK again to exit", Toast.LENGTH_SHORT, "INFORMATION");

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                doubleBackToExitPressedOnce = false;
            }
        }, 2000);
    }

    private void initializeBroadcastReceiver() {
        IntentFilter serverSyncFilter = new IntentFilter();
        serverSyncFilter.addAction(ServerSyncService.ACTION_NEED_AUTHENTICATION);
        serverSyncFilter.addAction(ServerSyncService.ACTION_DONE_PRODUCTS_SYNC);
        serverSyncFilter.addAction(ServerSyncService.ACTION_DONE_PRODUCTS_BREAKDOWN_SYNC);
        serverSyncFilter.addAction(ServerSyncService.ACTION_DONE_REWARDS_SYNC);
        serverSyncFilter.addAction(ServerSyncService.ACTION_DONE_SALES_SYNC);
        serverSyncFilter.addAction(ServerSyncService.ACTION_DONE_RETURNS_SYNC);
        serverSyncFilter.addAction(ServerSyncService.ACTION_DONE_INVENTORY_SYNC);
        serverSyncFilter.addAction(ServerSyncService.ACTION_DONE_EXPENSES_SYNC);
        serverSyncFilter.addAction(ServerSyncService.ACTION_DONE_PRODUCTS_FOR_DELIVERY_SYNC);
        serverSyncFilter.addAction(ServerSyncService.ACTION_ERROR);
        serverSyncFilter.addAction(GetAvailableStoresOnWebService.ACTION_DONE_GET_AVAILABLE_STORES);
        serverSyncFilter.addAction(RegisterStoreOnWebService.ACTION_DONE_REGISTER_STORE);
        serverSyncFilter.addAction(RegisterStoreOnWebService.ACTION_ERROR_REGISTER_STORE);
        serverSyncFilter.addCategory(Intent.CATEGORY_DEFAULT);
        serverSyncServiceBroadcastReceiver = new ServerSyncServiceBroadcastReceiver(this);
        registerReceiver(serverSyncServiceBroadcastReceiver, serverSyncFilter);

        DaoManager.initialize(this);
    }


//<editor-fold desc="Invoice View Fragment Presenter">
    @Override
    public void onDeductSalesProduct(SalesProduct salesProduct) {
        if (currentFragment instanceof InvoiceFragment) {
            float totalAmount = ((InvoiceFragment) currentFragment).getTotalAmount();
            ProductDao productDao = LoyaltyStoreApplication.getSession().getProductDao();
            Product product = productDao.load(salesProduct.getProduct_id());
            float unitCost = product.getUnit_cost();

            salesProduct.setQuantity(salesProduct.getQuantity() - 1);
            salesProduct.setSub_total(salesProduct.getSub_total() - unitCost);
            totalAmount -= unitCost;

            ((InvoiceFragment) currentFragment).setTotalAmount(totalAmount);
            ((InvoiceFragment) currentFragment).updateSalesProductList();

        }

    }

    @Override
    public void onRemoveSalesProduct(SalesProduct salesProduct) {
        if (currentFragment instanceof InvoiceFragment) {

            List<SalesProduct> salesProducts = ((InvoiceFragment) currentFragment).getSalesProducts();

            int itemIndexToRemove = -1;

            for (int i = 0; i < salesProducts.size(); i++) {
                if (salesProducts.get(i) == salesProduct) {
                    itemIndexToRemove = i;
                }
            }

            if (itemIndexToRemove != -1) {

                float totalAmount = ((InvoiceFragment) currentFragment).getTotalAmount();

                totalAmount -= salesProduct.getSub_total();
                salesProducts.remove(itemIndexToRemove);
                ((InvoiceFragment) currentFragment).setSalesProducts(salesProducts);
                ((InvoiceFragment) currentFragment).setTotalAmount(totalAmount);
            }

        }

    }

    @Override
    public void onSearchProduct(String searchString, String searchCategory) {
        ProductDao productDao = LoyaltyStoreApplication.getSession().getProductDao();

        maxMenuListCountLimit = 100;

        List<Product> productList = new ArrayList<>();

        QueryBuilder queryBuilder = productDao.queryBuilder();

        if (!"".equals(searchString)) {

            queryBuilder.whereOr(
                    ProductDao.Properties.Name.like(
                            searchString + "%"
                    ),
                    ProductDao.Properties.Category.like(
                            searchString + "%"
                    )
            ).whereOr(
                    ProductDao.Properties.Type.eq("Product for Retail"),
                    ProductDao.Properties.Type.eq("For Direct Transactions")
            );

        } else {

            queryBuilder.whereOr(
                    ProductDao.Properties.Type.eq("Product for Retail"),
                    ProductDao.Properties.Type.eq("For Direct Transactions")
            ).list();
        }

        if (!"None".equals(searchCategory)) {
            queryBuilder.where(
                    ProductDao.Properties.Category.eq(searchCategory)
            );
        }

        productList = queryBuilder.limit(maxMenuListCountLimit).offset(minMenuListCountLimit).list();

        if (currentFragment instanceof InvoiceFragment) {

            ((InvoiceFragment) currentFragment).setProducts(productList);

        }
    }

    @Override
    public void onCheckOut() {

        if (currentFragment instanceof InvoiceFragment) {

            final List<SalesProduct> salesProductList = ((InvoiceFragment) currentFragment).getSalesProducts();

            if (!salesProductList.isEmpty()) {

                checkout(salesProductList);

            }

        }

    }

    @Override
    public void onClearProductList() {
        if (currentFragment instanceof InvoiceFragment) {

            List<SalesProduct> salesProductList = ((InvoiceFragment) currentFragment).getSalesProducts();
            salesProductList.clear();
            ((InvoiceFragment) currentFragment).setSalesProducts(salesProductList);
            ((InvoiceFragment) currentFragment).setTotalAmount((float) 0);

        }
    }

    @Override
    public void onRefreshProductMenuList() {

        ProductDao productDao = LoyaltyStoreApplication.getSession().getProductDao();

        maxMenuListCountLimit = 100;

        List<Product> productList = productDao
                .queryBuilder()
                .whereOr(
                        ProductDao.Properties.Type.eq(
                                "Product for Retail"
                        ),
                        ProductDao.Properties.Type.eq(
                                "For Direct Transactions"
                        )
                ).limit(maxMenuListCountLimit)
                .offset(minMenuListCountLimit).list();

        if (currentFragment instanceof InvoiceFragment) {

            ((InvoiceFragment) currentFragment).setProducts(productList);

        }

    }

    private void onReadQRCode() {
        IntentIntegrator integrator = new IntentIntegrator(MainActivity2.this);
        integrator.setDesiredBarcodeFormats(IntentIntegrator.ALL_CODE_TYPES);
        integrator.setPrompt("Scan QR Code");
        integrator.setCameraId(0);  // Use a specific camera of the device
        integrator.setBeepEnabled(false);
        integrator.initiateScan();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (result != null) {
            if (result.getContents() == null) {
                //Toast.makeText(this, "Cancelled", Toast.LENGTH_LONG).show();
            } else {
                if (etQRCodeResult != null) {
                    etQRCodeResult.setText(result.getContents());
                }

            }
        } else {
            Log.d(TAG, "Weird");
            // This is important, otherwise the result will not be passed to the fragment
            super.onActivityResult(requestCode, resultCode, data);
        }

    }

    @Override
    public void onSeniorChecked(boolean isChecked) {

        if (currentFragment instanceof InvoiceFragment) {

            float totalAmount = 0;

            if (isChecked) {

                float genericProductsTotalAmount = 0;
                float genericProductsDiscountAmount = 0;

                List<SalesProduct> allSalesProducts =
                        ((InvoiceFragment) currentFragment).getSalesProducts();

                List<SalesProduct> genericSalesProducts = new ArrayList<>();

                ProductDao productDao
                        = LoyaltyStoreApplication.getSession().getProductDao();

                for (SalesProduct salesProduct : allSalesProducts) {

                    Product product = productDao.load(salesProduct.getProduct_id());

                    if ("GENERICS".trim().equals(product.getCategory().trim().toUpperCase())
                            || !"For Direct Transactions".equals(product.getType())) {
                        genericSalesProducts.add(salesProduct);
                        genericProductsTotalAmount += salesProduct.getSub_total();
                    }
                }

                genericProductsDiscountAmount = (float) (genericProductsTotalAmount * 0.20);

                totalAmount = ((InvoiceFragment) currentFragment).getTotalAmount() - genericProductsDiscountAmount;
                ((InvoiceFragment) currentFragment).setTotalAmount(totalAmount);

            } else {

                List<SalesProduct> allSalesProducts =
                        ((InvoiceFragment) currentFragment).getSalesProducts();

                for (SalesProduct salesProduct : allSalesProducts) {

                    totalAmount += salesProduct.getSub_total();

                }

                ((InvoiceFragment) currentFragment).setTotalAmount(totalAmount);

            }
        }
    }

    @Override
    public void onInvoiceFragmentViewReady() {
        currentFragment = viewPagerAdapter.getRegisteredFragment(viewPager.getCurrentItem());

        if (currentFragment instanceof InvoiceFragment) {

            List<Product> products = getProductsForRetail();

            ((InvoiceFragment) currentFragment).setProducts(
                    products == null ? new ArrayList<Product>() : products
            );
        }
    }

    @Override
    public void onLoadMoreProducts() {

        maxMenuListCountLimit += 100;

        List<Product> products = getProductsForRetail();

        ((InvoiceFragment) currentFragment).setProducts(
                products == null ? new ArrayList<Product>() : products
        );
    }
    //</editor-fold>

//<editor-fold desc="Wifi Direct Connectivity Data Presenter">
    @Override
    public void onNewPeersDiscovered(List<WifiP2pDevice> wifiP2pDevices) {
        if (currentFragment instanceof SynchronizeViewFragment) {
            ((SynchronizeViewFragment) currentFragment).setAgentDeviceList(wifiP2pDevices);

            if (wifiP2pDevices.size() > 0) {
                hideSearchAgentProgressDialog();
            }
        }
    }

    @Override
    public void onConnectionEstablished() {
        if (currentFragment instanceof SynchronizeViewFragment) {
            synchronize();
            try {

                List<WifiP2pDevice> agentDeviceList = ((SynchronizeViewFragment) currentFragment).getAgentDeviceList();

                JSONObject json = new JSONObject(agentDeviceList.get(0).deviceName);
                ((SynchronizeViewFragment) currentFragment).setConnectivity(json.getString("O"));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onConnectionTerminated() {

    }
    //</editor-fold>

//<editor-fold desc="Sync With Agent Task Presenter">
    @Override
    public void onProductsAcquired(List<Product> products) {

        ProductDao productDao
                = LoyaltyStoreApplication.getSession().getProductDao();

        if (currentFragment instanceof SynchronizeViewFragment) {

            ((SynchronizeViewFragment) currentFragment).markSyncProductsDone(products.size());

            productDao.deleteAll();

            productDao.insertOrReplaceInTx(products);

        }

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

    @Override
    public void onRewardsAcquired(List<Reward> rewards) {

        RewardDao rewardDao
                = LoyaltyStoreApplication.getSession().getRewardDao();

        if (currentFragment instanceof SynchronizeViewFragment) {
            ((SynchronizeViewFragment) currentFragment).markSyncRewardsDone(rewards.size());

            for (Reward reward : rewards) {

                //Log.d(TAG, "ACQUIRED REWARDS :" + reward.getReward());

            }

            rewardDao.deleteAll();

            rewardDao.insertOrReplaceInTx(rewards);
        }

    }

    @Override
    public void onProductDeliveriesAcquired(List<ProductDelivery> productDeliveries) {

        ProductDeliveryDao productDeliveryDao
                = LoyaltyStoreApplication.getSession().getProductDeliveryDao();

        if (currentFragment instanceof SynchronizeViewFragment) {
            ((SynchronizeViewFragment) currentFragment).markSyncDeliveriesDone(productDeliveries.size());

            productDeliveryDao.insertOrReplaceInTx(productDeliveries);
            productDeliveriesforConfirmation = productDeliveries;
        }

    }

    @Override
    public void onSalesSent(List<Sales> sales) {
        if (currentFragment instanceof SynchronizeViewFragment) {
            ((SynchronizeViewFragment) currentFragment).markSyncSalesDone(sales.size());
        }
    }

    @Override
    public void onItemReturnSentAndAcquiredProcessed(List<ItemReturn> itemReturns, List<ItemReturn> processedItemReturns) {

        ItemReturnDao itemReturnDao =
                LoyaltyStoreApplication.getSession().getItemReturnDao();

        ProductDao productDao
                = LoyaltyStoreApplication.getSession().getProductDao();

        ItemInventoryDao itemInventoryDao
                = LoyaltyStoreApplication.getSession().getItemInventoryDao();

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

            List<ItemInventory> itemInventoryList
                    = itemInventoryDao
                    .queryBuilder()
                    .where(
                            ItemInventoryDao.Properties.Name.eq(
                                    processedItemReturn.getProduct_name()
                            )
                    ).list();

            if (itemInventoryList.size() > 0) {

                for (ItemInventory itemInventory : itemInventoryList) {
                    itemInventory.setQuantity(itemInventory.getQuantity() - processedItemReturn.getQuantity());
                    itemInventoryDao.insertOrReplace(itemInventory);
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
    public void onTaskDone() {
        if (currentFragment instanceof SynchronizeViewFragment) {
            disconnectPeers();

            ((SynchronizeViewFragment) currentFragment).setSyncButtonVisibility(View.GONE);
            ((SynchronizeViewFragment) currentFragment).setCloseButtonVisibility(View.VISIBLE);

        }
    }

    @Override
    public void onSocketConnectFailed() {
        synchronize();
    }
    //</editor-fold>

//<editor-fold desc="On Network Change Broadcast Reciever Presenter">
    @Override
    public void onChangeNetworkStatus(boolean isWifiEnabled) throws InterruptedException {

        if (isWifiEnabled) {

            if (wifiProgressDialog.isShowing()) {
                wifiProgressDialog.cancel();
            }

            if (productDeliveriesforConfirmation.size() > 0) {
                Intent intent = new Intent(MainActivity2.this, ConfirmProductDeliveryActivity.class);
                Gson gson = new Gson();
                intent.putExtra(ConfirmProductDeliveryActivity.EXTRA_PRODUCT_DELIVERY_LIST, gson.toJson(productDeliveriesforConfirmation));
                startActivity(intent);
                //finish();
            }

        } else {
            wifiProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            wifiProgressDialog.setMessage("Restarting wifi, Please wait...");
            wifiProgressDialog.setIndeterminate(true);
            wifiProgressDialog.setCancelable(false);
            wifiProgressDialog.show();
        }

    }
//</editor-fold>

//<editor-fold desc="Synchronize View Fragment Presenter">
    @Override
    public void onSynchronizeViewReady() {
        if (currentFragment instanceof SynchronizeViewFragment) {

        }
    }

    @Override
    public void onSync() {
        if (currentFragment instanceof SynchronizeViewFragment) {

            List<WifiP2pDevice> agentDeviceList
                    = ((SynchronizeViewFragment) currentFragment).getAgentDeviceList();

            if (agentDeviceList.size() > 0)
                connectToAgent();
            else
                startAgentDeviceSearch();
        }

    }

    @Override
    public void onClose() {
        if (currentFragment instanceof SynchronizeViewFragment) {

        }
    }
    //</editor-fold>

//<editor-fold desc="Store Sales Inventory Details Fragment Presenter">
    @Override
    public void onLoadStoreSalesInventory(Date transactionDate) {

        currentFragment = viewPagerAdapter.getRegisteredFragment(viewPager.getCurrentItem());

        if (currentFragment instanceof InventoryFragment)
            ((InventoryFragment) currentFragment).loadStoreSalesInventory();

    }
    //</editor-fold>

//<editor-fold desc="Returns to Commissary View Fragment Presenter">
    @Override
    public void onReturnToCommissaryViewFragmentReady() {

        currentFragment = viewPagerAdapter.getRegisteredFragment(viewPager.getCurrentItem());

        if (currentFragment instanceof InventoryFragment) {
            ((InventoryFragment) currentFragment).loadReturnsToCommissary();
        }
    }

    @Override
    public void onAddReturns() {
        Intent intent = new Intent(MainActivity2.this, AddItemToReturnActivity.class);
        startActivity(intent);
    }

    @Override
    public void onEditItemReturn(ItemReturn itemReturn) {
        if (!itemReturn.getIs_synced()) {

            Intent intent = new Intent(MainActivity2.this, AddItemToReturnActivity.class);
            intent.putExtra(
                    AddItemToReturnActivity.EXTRA_ITEM_RETURN_ID,
                    itemReturn.getId()
            );
            intent.putExtra(
                    AddItemToReturnActivity.EXTRA_ITEM_RETURN_VALUE,
                    itemReturn.getType()
            );

            startActivity(intent);

        }
    }

    @Override
    public void onEditCashReturn(CashReturn cashReturn) {

        if (!cashReturn.getIs_synced()) {

            //Log.d(TAG, " NOT SYNCED ");

            Intent intent = new Intent(MainActivity2.this, AddItemToReturnActivity.class);
            intent.putExtra(
                    AddItemToReturnActivity.EXTRA_ITEM_RETURN_ID,
                    cashReturn.getId()
            );
            intent.putExtra(
                    AddItemToReturnActivity.EXTRA_ITEM_RETURN_VALUE,
                    cashReturn.getType()
            );

            startActivity(intent);

        }

    }
    //</editor-fold>

//<editor-fold desc="Expenses Fragment Presenter">
    @Override
    public void onExpenseViewReady() {

        currentFragment = viewPagerAdapter.getRegisteredFragment(viewPager.getCurrentItem());

        if (currentFragment instanceof InventoryFragment) {
            ((InventoryFragment) currentFragment).loadExpenses();
        }
    }

    @Override
    public void onAddExpense(long expenseId) {
        if (currentFragment instanceof InventoryFragment) {
            ((InventoryFragment) currentFragment).addExpense(expenseId);
        }
    }
    //</editor-fold>

//<editor-fold desc="Sales Fragment Presenter">
    @Override
    public void onSalesFragmentViewReady() {

        currentFragment = viewPagerAdapter.getRegisteredFragment(viewPager.getCurrentItem());

        if (currentFragment instanceof SalesFragment) {
            SimpleDateFormat formatter = Constants.SIMPLE_DATE_FORMAT;

            Date dateFilter = ((SalesFragment) currentFragment).getFilterDate();

            SalesDao salesDao
                    = LoyaltyStoreApplication.getSession().getSalesDao();

            List<Sales> salesListFilteredByDate = new ArrayList<>();

            float totalSalesAmount = 0;

            for (Sales sales : salesDao.queryBuilder().orderDesc(SalesDao.Properties.Transaction_date).list()) {

                if (java.sql.Date.valueOf(formatter.format(sales.getTransaction_date())).compareTo(dateFilter) == 0) {
                    salesListFilteredByDate.add(sales);
                    totalSalesAmount += sales.getAmount();
                }

            }

            ((SalesFragment) currentFragment).setTotalSalesAmount(totalSalesAmount);
            ((SalesFragment) currentFragment).setSalesList(salesListFilteredByDate);


        }
    }

    @Override
    public void onClickAllRecords() {

        currentFragment = viewPagerAdapter.getRegisteredFragment(viewPager.getCurrentItem());

        if (currentFragment instanceof SalesFragment) {
            SalesDao salesDao
                    = LoyaltyStoreApplication.getSession().getSalesDao();

            List<Sales> allSales = salesDao.loadAll();

            float totalSalesAmount = 0;

            for (Sales sales : allSales) {
                totalSalesAmount += sales.getAmount();
            }

            ((SalesFragment) currentFragment).setTotalSalesAmount(totalSalesAmount);
            ((SalesFragment) currentFragment).setSalesList(allSales);
        }
    }

    @Override
    public void onClickSales(Sales sales) {

        currentFragment = viewPagerAdapter.getRegisteredFragment(viewPager.getCurrentItem());

        if (currentFragment instanceof SalesFragment) {

            SalesProductDao salesProductDao
                    = LoyaltyStoreApplication.getSession().getSalesProductDao();

            QueryBuilder qBuilder = salesProductDao.queryBuilder();
            qBuilder.where(SalesProductDao.Properties.Sales_transaction_number.eq(sales.getTransaction_number()));
            List<SalesProduct> salesProducts = qBuilder.list();

            ((SalesFragment) currentFragment).setSalesProductList(salesProducts);

        }
    }
    //</editor-fold>

//<editor-fold desc="Store Account Settings Fragment Presenter">
    @Override
    public void onStoreSettingsFragmentViewReady() {

        currentFragment = viewPagerAdapter.getRegisteredFragment(viewPager.getCurrentItem());

        if (storeAccountProgressDialog != null) {
            storeAccountProgressDialog.dismiss();
        }

        if (currentFragment instanceof SettingsFragment) {
            ((SettingsFragment) currentFragment).loadStoreAccount();

            Retailer retailer = Retailer.getDeviceRetailerFromSharedPreferences(MainActivity2.this);

            if (!"".equals(retailer.getStoreName())) {
                isDeviceRegisterd = true;
            }

        }
    }

    @Override
    public void onSaveStoreAccountSettings(String serverURL, String storeName) {
        if (currentFragment instanceof SettingsFragment) {

            //Log.d(TAG, "onSaveStoreAccountSettings");

            Retailer retailer = Retailer.getDeviceRetailerFromSharedPreferences(MainActivity2.this);
            retailer.setStoreName(storeName);
            retailer.save(MainActivity2.this);

            User user = User.getSavedUser(MainActivity2.this);
            user.setFormalisticsServer(prependHttp(serverURL));
            user.save(MainActivity2.this);

            wifiDirectConnectivityDataPresenter.resetDeviceInfo(retailer.getDeviceInfo());

            if (!isDeviceRegisterd)
                registerDeviceInFormalistics(serverURL, storeName);

        }
    }

    @Override
    public void onRegisterStore(String serverURL) {

        if (currentFragment instanceof SettingsFragment) {

            if (!isNetworkAvailable()) {
                //Toast.makeText(MainActivity2.this, "Network unavailable.", Toast.LENGTH_SHORT).show();
                showToastMessage("Network unavailable.", Toast.LENGTH_SHORT, "ERROR");
                return;

            }

            //Log.d(TAG, " SERVER URL : " + serverURL);
            if (!isDeviceRegisterd)
                onGetAvailableBranchesFromFormalistics(serverURL);
            else
                //Toast.makeText(MainActivity2.this, "Device already registered.", Toast.LENGTH_SHORT).show();
                showToastMessage("Device already registered.", Toast.LENGTH_SHORT, "ERROR");

        }

    }

    private void onGetAvailableBranchesFromFormalistics(String serverURL) {

        if (!"".equals(serverURL.trim())) {

            showStoreAccountProgressDialog("Please wait while getting available branches...");

            Retailer retailer = Retailer.getDeviceRetailerFromSharedPreferences(this);
            retailer.setServerUrl(prependHttp(serverURL));
            retailer.save(this);

            Intent intent = new Intent(MainActivity2.this, GetAvailableStoresOnWebService.class);
            startService(intent);

        } else {
            /*
            Toast.makeText(
                    MainActivity2.this,
                    "Please provide a server url to continue.",
                    Toast.LENGTH_LONG
            ).show();
            */

            showToastMessage("Please provide a server url to continue.", Toast.LENGTH_SHORT, "ERROR");
        }

    }

    private void showStoreAccountProgressDialog(String message) {

        //Log.d(TAG, "showStoreAccountProgressDialog");

        storeAccountProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        storeAccountProgressDialog.setMessage(message);
        storeAccountProgressDialog.setIndeterminate(true);
        storeAccountProgressDialog.setCancelable(false);
        storeAccountProgressDialog.show();

    }

    private void hideStoreAccountProgressDialog() {

        //Log.d(TAG, "hideStoreAccountProgressDialog");

        storeAccountProgressDialog.cancel();
        storeAccountProgressDialog.dismiss();

    }

    private void setBranchChoices(final ArrayAdapter<String> retailerNameListAdapter) {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity2.this);
        builder.setTitle("Select desired branch");

        builder.setNegativeButton(
                "cancel",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

        builder.setAdapter(
                retailerNameListAdapter,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        ((SettingsFragment) currentFragment).setStoreName(retailerNameListAdapter.getItem(which));
                    }
                });
        builder.show();

    }

    private void registerDeviceInFormalistics(String serverURL, String storeName) {

        if (!"".equals(serverURL)) {

            Retailer retailer = Retailer.getDeviceRetailerFromSharedPreferences(this);
            retailer.setServerUrl(prependHttp(serverURL));
            retailer.save(this);

            Intent intent = new Intent(MainActivity2.this, RegisterStoreOnWebService.class);
            intent.putExtra(RegisterStoreOnWebService.EXTRA_STORE_NAME, storeName);
            startService(intent);

            hideStoreAccountProgressDialog();

        }
    }

    private void registrationSuccessfulDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Device successfully registered. \n");

        builder.setNegativeButton("Close", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.show();
    }

    @Override
    public void onSignOut() {
        //finish();
        //user.save(MainActivity2.this);

        UserDeviceLogoutTask.UserDeviceLogoutTaskListener listener = new UserDeviceLogoutTask.UserDeviceLogoutTaskListener() {
            @Override
            public void onUserLoggedOut() {
                User.clear(MainActivity2.this);
                finish();
            }

            @Override
            public void onUserLogoutFailed() {
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity2.this);
                builder.setTitle("Error");
                builder.setMessage("Sign out failed, press ok to continue closing the application.");
                builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        User.clear(MainActivity2.this);
                        finish();
                    }
                });

                builder.setNegativeButton("Close", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

                builder.show();
            }

            @Override
            public void onUserLogoutTaskDone() {

            }
        };

        UserDeviceLogoutTask userDeviceLogoutTask
                = new UserDeviceLogoutTask(this, listener);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB)
            userDeviceLogoutTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        else
            userDeviceLogoutTask.execute();



    }
    //</editor-fold>

//<editor-fold desc="Tab Maintenance Settings Fragment Presenter">
    @Override
    public void onSaveTabMaintenance() {
        if (currentFragment instanceof SettingsFragment) {
            ((SettingsFragment) currentFragment).onSaveTabMaintenance();
        }

        restartApp();
    }
    //</editor-fold>

//<editor-fold desc="Delivery History Fragment Presenter">
    @Override
    public void onDeliveryHistoryViewFragmentReady() {
        if (currentFragment instanceof DeliveryFragment) {
            ((DeliveryFragment) currentFragment).setDistinctDeliveryList();
        }
    }

    @Override
    public void onViewDeliveryHistory(ProductForDelivery productForDelivery) {
        if (currentFragment instanceof DeliveryFragment) {
            ((DeliveryFragment) currentFragment).setDeliveryListByDateAndAgentName(productForDelivery);
        }
    }
    //</editor-fold>

//<editor-fold desc="Deliveries For Confirmation Fragment Presenter">
    @Override
    public void onGetProductsForDelivery() {
        Intent intent = new Intent(MainActivity2.this, GetProductsForDeliveryService.class);
        startService(intent);

        showSynchronizing(true, null, 0);
    }

    @Override
    public void onProductForDeliveryClicked(ProductForDelivery productForDelivery) {
        //showToastMessage("On Product For Delivery Clicked", Toast.LENGTH_SHORT, "INFORMATION");
        if (currentFragment instanceof DeliveryFragment) {

            if ((productForDelivery.getQuantity_received() + 1) <= productForDelivery.getQuantity()) {

                productForDelivery.setQuantity_received(productForDelivery.getQuantity_received() + 1);
                ((DeliveryFragment) currentFragment).addRecievedProductForDelivery(productForDelivery);

            }
        }
    }

    @Override
    public void onConfirmProductDeliveries() {
        if (currentFragment instanceof DeliveryFragment) {

            ProductForDeliveryDao productForDeliveryDao
                    = LoyaltyStoreApplication.getSession().getProductForDeliveryDao();

            for(ProductForDelivery productForDelivery : productForDeliveryDao.loadAll()) {

            }

                final List<ProductForDelivery> productsForDelivery
                    = productForDeliveryDao
                        .queryBuilder()
                        .where(
                                ProductForDeliveryDao.Properties.Status.eq("For Delivery")
                        ).list();

            Date currDate = new Date();

            for(ProductForDelivery productForDelivery : productsForDelivery){
                productForDelivery.setStatus("Received");
                productForDelivery.setDate_received(currDate);
                productForDeliveryDao.insertOrReplace(productForDelivery);
            }

            AcceptProductsTask.AcceptProductsTaskListener listener = new AcceptProductsTask.AcceptProductsTaskListener() {
                @Override
                public void onNeedsAuthentication() {
                    onAuthenticationNeeded();
                }

                @Override
                public void onFinish() {
                    //  reload list of products
                    showToastMessage("Products Received", Toast.LENGTH_SHORT, "INFORMATION");

                   addDeliveredProductsToInventory(productsForDelivery);

                }
            };

            ConfirmProductsForDeliveryTask confirmProductsForDeliveryTask =
                    new ConfirmProductsForDeliveryTask(this, productsForDelivery, listener);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB)
                confirmProductsForDeliveryTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            else
                confirmProductsForDeliveryTask.execute();


            ((DeliveryFragment) currentFragment).clearDeliveryList();
        }
    }

    private void addDeliveredProductsToInventory(List<ProductForDelivery> productsForDelivery){

        ProductDao productDao = LoyaltyStoreApplication.getSession().getProductDao();
        ItemInventoryDao itemInventoryDao = LoyaltyStoreApplication.getSession().getItemInventoryDao();

        for(ProductForDelivery productForDelivery : productsForDelivery){
            Product product = productDao.load(productForDelivery.getProduct_id());

            List<ItemInventory> itemInventoryList
                    = itemInventoryDao
                        .queryBuilder()
                        .where(
                                ItemInventoryDao.Properties.Product_id.eq(product.getId())
                        ).list();

            if(itemInventoryList.size() > 0){
                for(ItemInventory itemInventory : itemInventoryList){

                    itemInventory.setQuantity(itemInventory.getQuantity() + productForDelivery.getQuantity_received());
                    itemInventoryDao.insertOrReplace(itemInventory);

                }
            }else{

                ItemInventory itemInventory = new ItemInventory();
                itemInventory.setName(product.getName());
                itemInventory.setQuantity(productForDelivery.getQuantity_received());
                itemInventory.setStore_id(retailer.getStoreId());
                itemInventory.setProduct_id(product.getId());
                itemInventory.setIs_updated(true);
                itemInventoryDao.insertOrReplace(itemInventory);

            }

        }

    }

    @Override
    public void onProductForDeliveryReceiveAll(ProductForDelivery productForDelivery) {
        if (currentFragment instanceof DeliveryFragment) {

            productForDelivery.setQuantity_received(productForDelivery.getQuantity());
            ((DeliveryFragment) currentFragment).addRecievedProductForDelivery(productForDelivery);

        }
    }
    //</editor-fold>

//<editor-fold desc="Synchronize on Web Fragment Presenter">
    @Override
    public void onSynchronizeOnWeb() {

        User user = User.getSavedUser(this);

        //Log.d(TAG, "Formalistics Server : " + user.getFormalisticsServer());

        if (("".equals(user.getEmail()) && "".equals(user.getFormalisticsServer()))
                || (user.getFormalisticsServer() == null && user.getEmail() == null)) {
            onAuthenticationNeeded();
        } else {
            startServerSync();
        }

    }

    private void startServerSync() {
        Intent intent = new Intent(MainActivity2.this, ServerSyncService.class);
        startService(intent);

        showSynchronizing(true, null, 0);
    }

    private void updateInventory() {
        Intent intent = new Intent(MainActivity2.this, UpdateInventoryAndSyncOtherDataService.class);
        startService(intent);
    }

    private void showSynchronizing(boolean show, String action, int syncCount) {

        if (currentFragment instanceof SynchronizeOnWebFragment) {
            if (show) {
                ((SyncDataFragment) currentFragment).showSyncStarted();
            } else {
                if (ServerSyncService.ACTION_DONE_PRODUCTS_SYNC.equals(action)) {
                    ((SyncDataFragment) currentFragment).markSyncProductsDone(syncCount);
                } else if (ServerSyncService.ACTION_DONE_REWARDS_SYNC.equals(action)) {
                    ((SyncDataFragment) currentFragment).markSyncRewardsDone(syncCount);
                } else if (ServerSyncService.ACTION_DONE_SALES_SYNC.equals(action)) {
                    ((SyncDataFragment) currentFragment).markSyncSalesDone(syncCount);
                } else if (ServerSyncService.ACTION_DONE_RETURNS_SYNC.equals(action)) {
                    ((SyncDataFragment) currentFragment).markSyncReturnsDone(syncCount);
                } else if (ServerSyncService.ACTION_DONE_INVENTORY_SYNC.equals(action)) {
                    ((SyncDataFragment) currentFragment).markSyncInventoryDone(syncCount);
                    ((SyncDataFragment) currentFragment).enableSyncButton(true);
                } else if (ServerSyncService.ACTION_DONE_EXPENSES_SYNC.equals(action)) {

                }
            }
        } else if (currentFragment instanceof SettingsFragment) {

            if (GetAvailableStoresOnWebService.ACTION_DONE_GET_AVAILABLE_STORES.equals(action)) {
                showAvailableStores();
            } else if (RegisterStoreOnWebService.ACTION_DONE_REGISTER_STORE.equals(action)) {
                registrationSuccessfulDialog();
            } else if (RegisterStoreOnWebService.ACTION_ERROR_REGISTER_STORE.equals(action)) {

                /*
                Toast.makeText(
                        MainActivity2.this,
                        "Failed to register device.",
                        Toast.LENGTH_LONG
                ).show();
                */

                showToastMessage("Failed to register device.", Toast.LENGTH_LONG, "ERROR");

            }

        } else if (currentFragment instanceof DeliveryFragment) {

            if (GetProductsForDeliveryService.ACTION_DONE_PRODUCTS_FOR_DELIVERY_SYNC.equals(action)) {

                if(syncCount <= 0){
                    showToastMessage("No items for delivery", Toast.LENGTH_SHORT,"INFORMATION");
                }

                ProductForDeliveryDao productForDeliveryDao
                        = LoyaltyStoreApplication.getSession().getProductForDeliveryDao();

                List<ProductForDelivery> productForDeliveryList
                        = productForDeliveryDao
                        .queryBuilder()
                        .where(
                                ProductForDeliveryDao.Properties.Status.eq("For Delivery")
                        ).list();

                ((DeliveryFragment) currentFragment).setProductsForDelivery(productForDeliveryList);

            }
        }

    }

    private void showAvailableStores() {

        StoreDao storeDao = LoyaltyStoreApplication.getSession().getStoreDao();

        List<Store> storeList = storeDao.loadAll();

        List<String> retailerNameList = new ArrayList<>();
        ArrayAdapter<String> retailerNameListAdapter;

        retailerNameListAdapter = new ArrayAdapter<>(
                MainActivity2.this, android.R.layout.simple_spinner_item, retailerNameList);

        retailerNameListAdapter.setDropDownViewResource(
                android.R.layout.simple_spinner_dropdown_item
        );

        for (Store store : storeList) {

            retailerNameList.add(store.getName());

        }

        retailerNameListAdapter.notifyDataSetChanged();

        hideStoreAccountProgressDialog();
        setBranchChoices(retailerNameListAdapter);

    }
    //</editor-fold>

//<editor-fold desc="Server Sync Service Broadcast Receiver Presenter">
    @Override
    public void onAuthenticationNeeded() {
        /*
        Toast.makeText(
                MainActivity2.this,
                "Failed logging in to web, Kindly check your login credentials.",
                Toast.LENGTH_LONG
        ).show();
        */

        showToastMessage(
                "Failed logging in to web, Kindly check your login credentials.",
                Toast.LENGTH_LONG,
                "ERROR");

        if (currentFragment instanceof SynchronizeOnWebFragment) {
            ((SynchronizeOnWebFragment) currentFragment).markSyncInventoryDone(0);
            ((SynchronizeOnWebFragment) currentFragment).markSyncReturnsDone(0);
            ((SynchronizeOnWebFragment) currentFragment).markSyncProductsDone(0);
            ((SynchronizeOnWebFragment) currentFragment).markSyncRewardsDone(0);
            ((SynchronizeOnWebFragment) currentFragment).markSyncSalesDone(0);
        }else{
            finish();
            Intent intent = new Intent(MainActivity2.this, LoginActivity.class);
            startActivity(intent);
        }


    }

    @Override
    public void onSyncDone(String actionName, int syncCount) {
        showSynchronizing(false, actionName, syncCount);
    }

    @Override
    public void onError(String errorMessage) {

    }
    //</editor-fold>

//<editor-fold desc="DynamicMenuButtonListViewAdapter Presenter Methods">
    @Override
    public void onMenuItemClicked(Product product) {
        onProductClicked(product);
    }
    //</editor-fold>

//<editor-fold desc="Inventory View Adapter Presenter Methods">
    @Override
    public void onItemInventoryClicked(ItemInventory itemInventory) {
        onItemClick(itemInventory);
    }
    //</editor-fold>

//<editor-fold desc="Sales Product With Return List Adapter Presenter Methods">
    @Override
    public void onProductReturn(final SalesProduct salesProduct) {

        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity2.this);
        builder.setTitle(salesProduct.getName());

        ProductDao productDao
                = LoyaltyStoreApplication.getSession().getProductDao();

        Product product = productDao.load(salesProduct.getProduct_id());

        float costPerUnit = product.getUnit_cost();
        final float quantityPurhcased = salesProduct.getQuantity();

        LinearLayout.LayoutParams params =
                new LinearLayout.LayoutParams((ViewGroup.MarginLayoutParams)
                        new LinearLayout.LayoutParams(
                                LinearLayout.LayoutParams.WRAP_CONTENT,
                                LinearLayout.LayoutParams.WRAP_CONTENT
                        )
                );

        params.setMargins(0, 0, 20, 0);
        params.gravity = Gravity.RIGHT;

        TextView tvCostPerUnit = new TextView(this);
        tvCostPerUnit.setGravity(Gravity.RIGHT);
        tvCostPerUnit.setLayoutParams(params);
        tvCostPerUnit.setText("Cost per unit : " + costPerUnit);

        TextView tvQuantityPurhcased = new TextView(this);
        tvQuantityPurhcased.setGravity(Gravity.RIGHT);
        tvQuantityPurhcased.setLayoutParams(params);
        tvQuantityPurhcased.setText("Purchased : " + quantityPurhcased);

        TextView tvQuantityLabel = new TextView(MainActivity2.this);
        tvQuantityLabel.setText("Quantity to return : ");
        final EditText etQuantityToReturn = new EditText(MainActivity2.this);
        etQuantityToReturn.setInputType(InputType.TYPE_CLASS_NUMBER);
        etQuantityToReturn.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                etQuantityToReturn.setError(null);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);

        layout.addView(tvQuantityPurhcased);
        layout.addView(tvCostPerUnit);
        layout.addView(tvQuantityLabel);
        layout.addView(etQuantityToReturn);

        builder.setView(layout);

        builder.setPositiveButton("Return", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        final AlertDialog dialog = builder.create();

        dialog.show();

        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if ("".equals(etQuantityToReturn.getText().toString())) {
                    etQuantityToReturn.setError("This field is required to continue.");
                    return;
                }

                int quantityToReturn = Integer.valueOf(etQuantityToReturn.getText().toString());

                if (quantityToReturn > quantityPurhcased) {
                    etQuantityToReturn.setError("Quantity to return must be less than quantity purchased.");
                    return;
                }

                returnProduct(salesProduct, quantityToReturn);

                dialog.dismiss();

            }
        });

    }

    private void returnProduct(SalesProduct salesProduct, int quantityToReturn) {

        ItemInventoryDao itemInventoryDao
                = LoyaltyStoreApplication.getSession().getItemInventoryDao();

        List<ItemInventory> itemInventoryList
                = itemInventoryDao
                .queryBuilder()
                .where(
                        ItemInventoryDao.Properties.Product_id.eq(salesProduct.getProduct_id())
                ).list();

        for (ItemInventory itemInventory : itemInventoryList) {
            itemInventory.setQuantity(itemInventory.getQuantity() + quantityToReturn);
            itemInventoryDao.insertOrReplace(itemInventory);
            itemInventoryDao.refresh(itemInventory);
        }

        SalesProductDao salesProductDao
                = LoyaltyStoreApplication.getSession().getSalesProductDao();

        ProductDao productDao
                = LoyaltyStoreApplication.getSession().getProductDao();

        Product product = productDao.load(salesProduct.getProduct_id());

        List<SalesProduct> returnedSalesProducts
                = salesProductDao
                .queryBuilder()
                .where(
                        SalesProductDao.Properties.Sales_transaction_number.eq(
                                salesProduct.getSales_transaction_number()
                        ),
                        SalesProductDao.Properties.Product_id.eq(
                                salesProduct.getProduct_id()
                        ),
                        SalesProductDao.Properties.Is_returned.eq(true)
                ).list();

        if (returnedSalesProducts.size() > 0) {
            for (SalesProduct returnedProduct : returnedSalesProducts) {
                returnedProduct.setSub_total(product.getUnit_cost() * (returnedProduct.getQuantity() + quantityToReturn));
                returnedProduct.setQuantity(returnedProduct.getQuantity() + quantityToReturn);
                salesProductDao.insertOrReplace(returnedProduct);
            }
        } else {

            SalesProduct returnedSalesProduct = new SalesProduct();
            returnedSalesProduct.setProduct_id(salesProduct.getProduct_id());
            returnedSalesProduct.setName(salesProduct.getName());
            returnedSalesProduct.setQuantity(quantityToReturn);
            returnedSalesProduct.setSale_type(salesProduct.getSale_type());
            returnedSalesProduct.setPromo_code(salesProduct.getPromo_code());
            returnedSalesProduct.setSales_transaction_number(salesProduct.getSales_transaction_number());
            returnedSalesProduct.setSub_total(product.getUnit_cost() * quantityToReturn);
            returnedSalesProduct.setIs_returned(true);

            salesProductDao.insertOrReplace(returnedSalesProduct);
        }

        if (salesProduct.getQuantity() - quantityToReturn <= 0)
            salesProductDao.delete(salesProduct);
        else {
            salesProduct.setQuantity(salesProduct.getQuantity() - quantityToReturn);
            salesProductDao.insertOrReplace(salesProduct);
        }

        /*
        if (quantityToReturn < salesProduct.getQuantity() && returnedSalesProducts.size() > 0) {
            salesProduct.setQuantity(salesProduct.getQuantity() - quantityToReturn);
            salesProductDao.insertOrReplace(salesProduct);

        } else {
            salesProduct.setIs_returned(true);
            salesProductDao.insertOrReplace(salesProduct);
        }
        */

        List<SalesProduct> allSalesProduct
                = salesProductDao
                .queryBuilder()
                .where(
                        SalesProductDao.Properties.Sales_transaction_number.eq(
                                salesProduct.getSales_transaction_number()
                        )
                ).list();

        if (currentFragment instanceof SalesFragment) {
            ((SalesFragment) currentFragment).setSalesProductList(allSalesProduct);
        }


    }
    //</editor-fold>


//<editor-fold desc="Other Private Methods">
    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    private double getQuantityRemaining(Product product) {

        ProductDao productDao
                = LoyaltyStoreApplication.getSession().getProductDao();

        double quantityRemaining = 0;

        ItemInventoryDao itemInventoryDao
                = LoyaltyStoreApplication.getSession().getItemInventoryDao();

        if ("For Direct Transactions".trim().equals(product.getType().trim())) {

            List<ItemInventory> itemInventoryList
                    = itemInventoryDao
                    .queryBuilder()
                    .where(
                            ItemInventoryDao.Properties.Product_id.eq(product.getId())
                    ).list();

            for (ItemInventory itemInventory : itemInventoryList) {

                quantityRemaining = itemInventory.getQuantity();

            }

        } else if ("Product for Retail".trim().equals(product.getType().trim())) {

            List<ItemInventory> itemInventoryList
                    = itemInventoryDao
                    .queryBuilder()
                    .where(
                            ItemInventoryDao.Properties.Product_id.eq(product.getDeduct_product_to_id())
                    ).list();

            for (ItemInventory itemInventory : itemInventoryList) {

                quantityRemaining = itemInventory.getQuantity() / product.getDeduct_product_to_quantity();

            }

        }

        if (currentFragment instanceof InvoiceFragment) {

            List<SalesProduct> salesProductList = ((InvoiceFragment) currentFragment).getSalesProducts();

            for (int i = 0; i < salesProductList.size(); i++) {

                Product currentProduct = productDao.load(salesProductList.get(i).getProduct_id());

                if (salesProductList.get(i).getProduct_id() == product.getId()) {
                    quantityRemaining -= salesProductList.get(i).getQuantity();
                }else if(product.getDeduct_product_to_id() == currentProduct.getDeduct_product_to_id()){
                    quantityRemaining -= (salesProductList.get(i).getQuantity() * currentProduct.getDeduct_product_to_quantity()) / product.getDeduct_product_to_quantity();
                }

            }

        }

        return quantityRemaining;
    }

    private void onProductClicked(final Product product) {

        final float costPerUnit = product.getUnit_cost();
        final double quantityRemaining = getQuantityRemaining(product);
        final double quantityNeeded = product.getDeduct_product_to_quantity();

        if (quantityRemaining > 0 && quantityRemaining >= quantityNeeded) {
            onAddSalesProduct(product, 1, 0, "");
        } else {
            showToastMessage("Insufficient inventory stocks.", Toast.LENGTH_LONG, "ERROR");
        }

        /*
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(product.getName());

        LinearLayout.LayoutParams params =
                new LinearLayout.LayoutParams(
                        (ViewGroup.MarginLayoutParams)
                                new LinearLayout.LayoutParams(
                                        LinearLayout.LayoutParams.WRAP_CONTENT,
                                        LinearLayout.LayoutParams.WRAP_CONTENT
                                )
                );

        params.setMargins(0, 0, 20, 0);
        params.gravity = Gravity.RIGHT;

        TextView tvCostPerUnit = new TextView(this);
        tvCostPerUnit.setGravity(Gravity.RIGHT);
        tvCostPerUnit.setLayoutParams(params);
        tvCostPerUnit.setText("Cost per unit : " + costPerUnit);

        TextView tvRemaining = new TextView(this);
        tvRemaining.setGravity(Gravity.RIGHT);
        tvRemaining.setLayoutParams(params);
        tvRemaining.setText("Remaining : " + quantityRemaining);
        if (quantityRemaining < 100) {
            //tvRemaining.setTextColor(Color.RED);
        }

        TextView tvQuantity = new TextView(this);
        tvQuantity.setTextColor(Color.BLACK);
        tvQuantity.setText("Quantity : ");

        final EditText etQuantity = new EditText(this);

        TextView tvDiscountByAmount = new TextView(this);
        tvDiscountByAmount.setTextColor(Color.BLACK);
        tvDiscountByAmount.setText("Amount Discount : ");

        final EditText etDiscountByAmount = new EditText(this);

        TextView tvDiscountByPercent = new TextView(this);
        tvDiscountByPercent.setTextColor(Color.BLACK);
        tvDiscountByPercent.setText("Percent Discount : (%) ");

        final EditText etDiscountByPercent = new EditText(this);

        TextView tvPromoCode = new TextView(this);
        tvPromoCode.setTextColor(Color.BLACK);
        tvPromoCode.setText("Promo Code : ");

        etQRCodeResult = new EditText(this);
        etQRCodeResult.setLayoutParams(
                new LinearLayout.LayoutParams(0,
                        ViewGroup.LayoutParams.WRAP_CONTENT, 1.0f)
        );

        Button bScanQRCode = new Button(this);
        bScanQRCode.setText("Scan");
        bScanQRCode.setLayoutParams(
                new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT)
        );


        bScanQRCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onReadQRCode();
            }
        });

        etQuantity.setInputType(InputType.TYPE_CLASS_NUMBER);
        etDiscountByAmount.setInputType(InputType.TYPE_CLASS_NUMBER);
        etDiscountByPercent.setInputType(InputType.TYPE_CLASS_NUMBER);

        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);

        layout.addView(tvCostPerUnit);
        layout.addView(tvRemaining);
        layout.addView(tvQuantity);
        layout.addView(etQuantity);
        //layout.addView(tvPromoCode);

        LinearLayout qrCodeLayout = new LinearLayout(this);
        qrCodeLayout.setOrientation(LinearLayout.HORIZONTAL);

        qrCodeLayout.addView(etQRCodeResult);
        qrCodeLayout.addView(bScanQRCode);

        //layout.addView(qrCodeLayout);
        layout.addView(tvDiscountByAmount);
        layout.addView(etDiscountByAmount);
        layout.addView(tvDiscountByPercent);
        layout.addView(etDiscountByPercent);

        builder.setView(layout);

        // Set up the buttons
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                if (!etQuantity.getText().toString().equals("")) {

                    float discount = 0;

                    if (!"".equals(etDiscountByAmount.getText().toString().trim())) {
                        discount = Float.valueOf(etDiscountByAmount.getText().toString());
                    }

                    int quantity = Integer.parseInt(etQuantity.getText().toString());

                    if (!"".equals(etDiscountByPercent.getText().toString().trim())) {
                        discount =
                                (costPerUnit * quantity) *
                                        (Float.valueOf(etDiscountByPercent.getText().toString()) / 100);
                    }

                    String promoCode = etQRCodeResult.getText().toString();

                    if (quantity != 0) {

                        if (quantity > 9999) {

                            Toast.makeText(MainActivity2.this, "Invalid quantity", Toast.LENGTH_SHORT).show();

                        } else {

                            if (quantityRemaining >= quantity)
                                onAddSalesProduct(product, quantity, discount, promoCode);
                            else
                                Toast.makeText(
                                        MainActivity2.this,
                                        "Insufficient inventory stocks.",
                                        Toast.LENGTH_LONG).show();

                        }

                    }

                    InputMethodManager inputMgr = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    inputMgr.hideSoftInputFromWindow(etQuantity.getWindowToken(), 0);

                }

            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();

                InputMethodManager inputMgr = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                inputMgr.hideSoftInputFromWindow(etQuantity.getWindowToken(), 0);

            }
        });

        builder.show();
        */

    }

    private int productExistInList(Product product) {

        int index = -1;

        if (currentFragment instanceof InvoiceFragment) {

            List<SalesProduct> salesProductList = ((InvoiceFragment) currentFragment).getSalesProducts();


            for (int i = 0; i < salesProductList.size(); i++) {
                if (salesProductList.get(i).getProduct_id() == product.getId()) {
                    index = i;
                }
            }


        }

        return index;

    }


    private void onAddSalesProduct(Product product, int quantity, float discount, String promoCode) {

        if (currentFragment instanceof InvoiceFragment) {

            float totalAmount = ((InvoiceFragment) currentFragment).getTotalAmount();

            float subTotal = product.getUnit_cost() * quantity - discount;
            totalAmount += subTotal;

            int productIndexInList = productExistInList(product);

            if (productIndexInList == -1) {

                SalesProduct salesProduct = new SalesProduct();
                salesProduct.setProduct_id(product.getId());
                salesProduct.setQuantity(quantity);
                salesProduct.setSub_total(subTotal);
                salesProduct.setName(product.getName());
                salesProduct.setSale_type("SALE");
                salesProduct.setPromo_code(promoCode);

                ((InvoiceFragment) currentFragment).addSalesProduct(salesProduct);

            } else {
                SalesProduct salesProduct = ((InvoiceFragment) currentFragment).getSalesProduct(productIndexInList);
                salesProduct.setQuantity(quantity + salesProduct.getQuantity());
                salesProduct.setSub_total(subTotal + salesProduct.getSub_total());

                ((InvoiceFragment) currentFragment).updateSalesProductList();
            }

            ((InvoiceFragment) currentFragment).setTotalAmount(totalAmount);

        }

    }

    private void checkout(List<SalesProduct> salesProducts) {

        if (currentFragment instanceof InvoiceFragment) {

            checkForRewards(salesProducts);

            Intent intent = new Intent(this, CheckoutActivity.class);
            Gson gson = new Gson();

            intent.putExtra(SalesProductsViewFragment.EXTRA_SALES_PRODUCT_LIST, gson.toJson(salesProducts));
            intent.putExtra(RewardViewFragment.EXTRA_REWARDS_LIST, gson.toJson(rewards));

            //Log.d(TAG, " totalDISCOUNT : " + totalDiscount);

            intent.putExtra(
                    CheckoutActivity.EXTRA_TOTAL_AMOUNT,
                    ((InvoiceFragment) currentFragment).getTotalAmount() - totalDiscount
            );
            intent.putExtra(RewardViewFragment.EXTRA_TOTAL_DISCOUNT, totalDiscount);
            intent.putExtra(CheckoutActivity.EXTRA_IS_SENIOR, ((InvoiceFragment) currentFragment).isSenior());

            startActivity(intent);
        }
    }

    private void checkForRewards(List<SalesProduct> salesProducts) {

        checkProductRewards(salesProducts);

        checkForPurchaseAmountRewards(((InvoiceFragment) currentFragment).getTotalAmount());

    }

    private void checkProductRewards(List<SalesProduct> salesProducts) {

        RewardDao rewardDao =
                LoyaltyStoreApplication.getSession().getRewardDao();

        for (int i = 0; i < salesProducts.size(); i++) {

            SalesProduct salesProduct = salesProducts.get(i);

            long productId = salesProduct.getProduct_id();

            //Log.d(TAG, "PRODUCT ID : " + productId);

            /*
            List<Reward> allRewards = rewardDao.loadAll();

            for (Reward reward : allRewards) {

                Log.d(TAG, "Condition_product_id :  " + reward.getCondition_product_id());

            }
            */

            String sql = " WHERE " + RewardDao.Properties.Condition_product_id.columnName +
                    " = ?";

            List<Reward> rewardsForProductList =
                    rewardDao.queryRaw(sql, new String[]{productId + ""});

            //Log.d(TAG, "FOUND REWARD SIZE : " + rewardsForProductList.size());

            for (Reward reward : rewardsForProductList) {

                if (!isRewardValid(reward.getValid_from(), reward.getValid_until())) {
                    return;
                }

                if (isBetweenCondition(
                        reward.getCondition(),
                        reward.getCondition_value(),
                        salesProduct.getQuantity())) {

                    if (reward.getCondition().toUpperCase().equals("EQUAL TO") || reward.getCondition().equals("=")) {

                        for (int rewardCount = 1;
                             rewardCount <= salesProduct.getQuantity() / reward.getCondition_value();
                             rewardCount++) {

                            setReward(reward);
                            rewards.add(reward);

                        }

                    } else {
                        setReward(reward);
                        rewards.add(reward);
                    }

                }

            }

        }

    }

    private void checkForPurchaseAmountRewards(float totalPurchaseAmount) {

        Date date = new Date();
        SimpleDateFormat formatter = Constants.SIMPLE_DATE_FORMAT;
        String formattedDate = formatter.format(date);

        RewardDao rewardDao = LoyaltyStoreApplication.getSession().getRewardDao();

        String sql = " WHERE " + RewardDao.Properties.Reward_condition.columnName +
                " =? ";

        List<Reward> rewardsForProductList =
                rewardDao.queryRaw(sql, new String[]{"purchase_amount"});

        for (Reward reward : rewardsForProductList) {

            if (isRewardValid(reward.getValid_from(), reward.getValid_until())) {

                if (isBetweenCondition(
                        reward.getCondition(),
                        reward.getCondition_value(),
                        totalPurchaseAmount)) {

                    if (reward.getCondition().toUpperCase().equals("EQUAL TO") || reward.getCondition().equals("=")) {

                        for (int rewardCount = 1;
                             rewardCount <= totalPurchaseAmount / reward.getCondition_value();
                             rewardCount++) {

                            setReward(reward);
                            rewards.add(reward);
                        }

                    } else {
                        setReward(reward);
                        rewards.add(reward);
                    }

                }
            }

        }

    }

    private boolean isBetweenCondition(String condition, float conditionValue, float value) {

        boolean result = false;

        //Log.d(TAG, "CONDITION : " + condition + " : CONDITION VALUE : " + conditionValue + " : VALUE : " + value);

        switch (condition.toUpperCase()) {

            case "GREATER THAN":
                result = (value > conditionValue) ? true : false;
                break;
            case ">":
                result = (value > conditionValue) ? true : false;
                break;
            case "GREATER THAN OR EQUAL TO":
                result = (value >= conditionValue) ? true : false;
                break;
            case ">=":
                result = (value >= conditionValue) ? true : false;
                break;
            case "LESS THAN":
                result = (value < conditionValue) ? true : false;
                break;
            case "<":
                result = (value < conditionValue) ? true : false;
                break;
            case "LESS THAN OR EQUAL TO":
                result = (value <= conditionValue) ? true : false;
                break;
            case "<=":
                result = (value <= conditionValue) ? true : false;
                break;
            case "EQUAL TO":
                result = (value >= conditionValue) ? true : false;
                break;
            case "=":
                result = (value >= conditionValue) ? true : false;
                break;

        }

        return result;

    }

    private boolean isRewardValid(Date validFrom, Date validUntil) {

        boolean isValid = false;

        SimpleDateFormat formatter = Constants.SIMPLE_DATE_FORMAT;

        Date dtValidFrom = java.sql.Date.valueOf(formatter.format(validFrom));
        Date dtValidUntil = java.sql.Date.valueOf(formatter.format(validUntil));
        Date currDate = java.sql.Date.valueOf(formatter.format(new Date()));

        if (currDate.compareTo(dtValidFrom) != -1 && currDate.compareTo(dtValidUntil) != 1) {
            isValid = true;
        }

        return isValid;

    }

    private void setReward(Reward reward) {

        ProductDao productDao
                = LoyaltyStoreApplication.getSession().getProductDao();

        switch (reward.getReward_type().toUpperCase()) {

            case "FREE_PRODUCT":

                String sql = " WHERE " + ProductDao.Properties.Id.columnName + "=?";

                List<Product> products = productDao.queryRaw(sql,
                        new String[]{reward.getReward_value()});

                for (Product productFreebie : products) {
                    SalesProduct salesProductFreebie = new SalesProduct();
                    salesProductFreebie.setProduct_id(productFreebie.getId());
                    salesProductFreebie.setQuantity(1);
                    salesProductFreebie.setSub_total((float) 0);
                    salesProductFreebie.setSale_type("FREEBIE");

                    ((InvoiceFragment) currentFragment).addSalesProduct(salesProductFreebie);

                }

                break;
            case "DISCOUNT":

                totalDiscount += Float.parseFloat(reward.getReward_value());

                break;

        }
    }

    private void connectToAgent() {

        List<WifiP2pDevice> agentDeviceList = ((SynchronizeViewFragment) currentFragment).getAgentDeviceList();

        if (agentDeviceList.size() > 0) {
            wifiDirectConnectivityDataPresenter.connectToCustomer(agentDeviceList.get(0), 3002);
            Toast.makeText(MainActivity2.this, "Connecting to agent...", Toast.LENGTH_SHORT).show();
        }

    }

    private void synchronize() {
        SyncWithAgentTask syncWithAgentTask = new SyncWithAgentTask(this, 3002, this);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB)
            syncWithAgentTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        else
            syncWithAgentTask.execute();

        ((SynchronizeViewFragment) currentFragment).showSnycStarted();
    }

    private void startAgentDeviceSearch() {

        showSeacrhAgentProgressDialog("Searching for agent...", true);
        hideSearchAgentProgressDialogLater(10000);

        searchAgentTask = new SearchAgentTask(
                this,
                wifiDirectConnectivityDataPresenter
        );
        //searchAgentTask.execute();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB)
            searchAgentTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        else
            searchAgentTask.execute();

    }

    private void showSeacrhAgentProgressDialog(String message, boolean cancellable) {

        searchAgentProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        searchAgentProgressDialog.setMessage(message);
        searchAgentProgressDialog.setIndeterminate(true);
        searchAgentProgressDialog.setCancelable(cancellable);

        searchAgentProgressDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                checkAgentDeviceList();
            }
        });

        searchAgentProgressDialog.show();

    }

    private void checkAgentDeviceList() {

        final List<WifiP2pDevice> agentDeviceList =
                ((SynchronizeViewFragment) currentFragment).getAgentDeviceList();

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

            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity2.this);
            builder
                    .setMessage("No agent device found. Would you like to search again?")
                    .setPositiveButton("Yes", dialogClickListener)
                    .setNegativeButton("No", dialogClickListener).show();

        } else {
            connectToAgent();
        }

    }

    private void hideSearchAgentProgressDialog() {

        if (searchAgentProgressDialog.isShowing()) {

            searchAgentProgressDialog.cancel();
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

    protected void hideSearchAgentProgressDialogLater(int hideAfterMillis) {
        handler.postDelayed(
                runnable = new Runnable() {
                    public void run() {
                        hideSearchAgentProgressDialog();
                    }
                },
                hideAfterMillis);
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

    private void restartApp() {

        Intent intentRestart = getBaseContext().getPackageManager()
                .getLaunchIntentForPackage(getBaseContext().getPackageName());
        intentRestart.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intentRestart);

    }

    private String prependHttp(String server) {

        if (!server.toLowerCase().contains("http://") && !server.toLowerCase().contains("https://")) {
            return "http://" + server;
        } else {
            return server;
        }
    }

    private void showToastMessage(String message, int duration, String type) {

        LayoutInflater inflater = getLayoutInflater();
        View layout = inflater.inflate(R.layout.custom_toast,
                (ViewGroup) findViewById(R.id.custom_toast));

        switch (type.toUpperCase()) {

            case "ERROR":
                layout.setBackgroundResource(R.drawable.custom_toast_background_error);
                break;
            case "SUCCESS":
                layout.setBackgroundResource(R.drawable.custom_toast_background_success);
                break;
            case "INFORMATION":
                layout.setBackgroundResource(R.drawable.custom_toast_background_information);
                break;


        }

        TextView text = (TextView) layout.findViewById(R.id.CustomToast_tvMessage);
        text.setText(message);

        Toast toast = new Toast(getApplicationContext());
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.setDuration(duration);
        toast.setView(layout);
        toast.show();

    }
    //</editor-fold>

}
