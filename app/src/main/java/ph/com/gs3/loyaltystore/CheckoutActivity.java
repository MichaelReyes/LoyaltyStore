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
import android.view.View;
import android.widget.Toast;

import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import ph.com.gs3.loyaltystore.adapters.CustomerDeviceListAdapter;
import ph.com.gs3.loyaltystore.fragments.CheckOutViewFragment;
import ph.com.gs3.loyaltystore.fragments.RewardViewFragment;
import ph.com.gs3.loyaltystore.fragments.SalesProductsViewFragment;
import ph.com.gs3.loyaltystore.globals.Constants;
import ph.com.gs3.loyaltystore.models.WifiDirectConnectivityState;
import ph.com.gs3.loyaltystore.models.sqlite.dao.ItemInventory;
import ph.com.gs3.loyaltystore.models.sqlite.dao.ItemInventoryDao;
import ph.com.gs3.loyaltystore.models.sqlite.dao.Product;
import ph.com.gs3.loyaltystore.models.sqlite.dao.ProductBreakdown;
import ph.com.gs3.loyaltystore.models.sqlite.dao.ProductBreakdownDao;
import ph.com.gs3.loyaltystore.models.sqlite.dao.ProductDao;
import ph.com.gs3.loyaltystore.models.sqlite.dao.Reward;
import ph.com.gs3.loyaltystore.models.sqlite.dao.RewardDao;
import ph.com.gs3.loyaltystore.models.sqlite.dao.Sales;
import ph.com.gs3.loyaltystore.models.sqlite.dao.SalesDao;
import ph.com.gs3.loyaltystore.models.sqlite.dao.SalesHasReward;
import ph.com.gs3.loyaltystore.models.sqlite.dao.SalesHasRewardDao;
import ph.com.gs3.loyaltystore.models.sqlite.dao.SalesProduct;
import ph.com.gs3.loyaltystore.models.sqlite.dao.SalesProductDao;
import ph.com.gs3.loyaltystore.models.tasks.AcquireClaimedRewardsTask;
import ph.com.gs3.loyaltystore.models.tasks.SendPurchaseInfoAndRewardsTask;
import ph.com.gs3.loyaltystore.models.tasks.SendPurchaseInfoForValidationTask;
import ph.com.gs3.loyaltystore.models.values.DeviceInfo;
import ph.com.gs3.loyaltystore.models.values.Retailer;
import ph.com.gs3.loyaltystore.presenters.WifiDirectConnectivityDataPresenter;

/**
 * Created by Bryan-PC on 02/02/2016.
 */
public class CheckoutActivity extends AppCompatActivity implements
        CheckOutViewFragment.CheckoutViewFragmentEventListener,
        WifiDirectConnectivityDataPresenter.WifiDirectConnectivityPresentationListener,
        SendPurchaseInfoForValidationTask.SendPurchaseInfoForValidationTaskEventListener,
        SendPurchaseInfoAndRewardsTask.SendPurchaseInfoAndRewardsTaskListener,
        AcquireClaimedRewardsTask.AcquireClaimedRewardsTaskListener {

    public static final String TAG = CheckoutActivity.class.getSimpleName();
    public static final String EXTRA_DATA_JSON_STRING = "data_json_string";
    public static final String EXTRA_TOTAL_AMOUNT = "total_amount";
    public static final String EXTRA_IS_SENIOR = "is_senior";
    public static final String DATA_TYPE_JSON_SALES = "sales";
    public static final String DATA_TYPE_JSON_SALES_PRODUCT = "sales_product";

    private enum ActionType {
        SEND_SALES, ACQUIRE_CLAIMED_REWARDS
    }

    private ActionType actionType = ActionType.SEND_SALES;

    private CheckOutViewFragment checkOutViewFragment;

    private WifiDirectConnectivityDataPresenter wifiDirectConnectivityDataPresenter;

    private Retailer retailer;

    private List<WifiP2pDevice> customerDeviceList;
    private CustomerDeviceListAdapter customerDeviceListAdapter;

    private List<SalesProduct> salesProducts;
    private List<Reward> rewards;

    private ProductDao productDao;
    private SalesProductDao salesProductDao;
    private SalesDao salesDao;
    private RewardDao rewardDao;
    private SalesHasRewardDao salesHasRewardDao;

    private float totalAmount;
    private float totalDiscounts;

    private WifiP2pDevice customerDevice;

    private ProgressDialog progressDialog;

    private String salesTransactionNumber;

    private WifiManager wifiManager;

    private long customerDeviceId;

    private boolean isFirstUse = false;
    private boolean hideProgressDialogLater = false;
    private boolean isSenior = false;

    private static final SimpleDateFormat formatter = new SimpleDateFormat(
            "EEE MMM d HH:mm:ss zzz yyyy");

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        salesTransactionNumber = "";

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checkout);

        retailer = Retailer.getDeviceRetailerFromSharedPreferences(this);

        progressDialog = new ProgressDialog(this);

        wifiDirectConnectivityDataPresenter = new WifiDirectConnectivityDataPresenter(
                this, retailer.getDeviceInfo()
        );

        customerDeviceList = new ArrayList<>();
        customerDeviceListAdapter = new CustomerDeviceListAdapter(this, customerDeviceList);

        salesProducts = new ArrayList<>();
        rewards = new ArrayList<>();

        checkOutViewFragment = (CheckOutViewFragment) getSupportFragmentManager()
                .findFragmentByTag(CheckOutViewFragment.TAG);

        if (checkOutViewFragment == null) {
            checkOutViewFragment = CheckOutViewFragment.createInstance(customerDeviceListAdapter);

            getSupportFragmentManager().beginTransaction().add(
                    R.id.container_checkout,
                    checkOutViewFragment,
                    CheckOutViewFragment.TAG).commit();
        }

        wifiManager = (WifiManager) this.getSystemService(Context.WIFI_SERVICE);

        wifiDirectConnectivityDataPresenter.discoverPeers(DeviceInfo.Type.CUSTOMER);

        initializeDAO();

        try {
            getExtras();
        } catch (JSONException | ParseException e) {
            e.printStackTrace();
        }

    }

    private void initializeDAO() {

        productDao = LoyaltyStoreApplication.getInstance().getSession().getProductDao();
        rewardDao = LoyaltyStoreApplication.getInstance().getSession().getRewardDao();
        salesProductDao = LoyaltyStoreApplication.getInstance().getSession().getSalesProductDao();
        salesDao = LoyaltyStoreApplication.getInstance().getSession().getSalesDao();
        salesHasRewardDao = LoyaltyStoreApplication.getInstance().getSession().getSalesHasRewardDao();

    }

    public void getExtras() throws JSONException, ParseException {

        Intent intent = this.getIntent();
        Bundle bundle = intent.getExtras();

        Gson gson = new Gson();

        String jsonStringSalesProducts = bundle.getString(SalesProductsViewFragment.EXTRA_SALES_PRODUCT_LIST);
        SalesProduct[] salesProductsArray = gson.fromJson(jsonStringSalesProducts, SalesProduct[].class);
        salesProducts = Arrays.asList(salesProductsArray);

        String jsonStringRewards = bundle.getString(RewardViewFragment.EXTRA_REWARDS_LIST);
        Reward[] rewardsArray = gson.fromJson(jsonStringRewards, Reward[].class);
        rewards = Arrays.asList(rewardsArray);

        /*String dataJsonString = bundle.getString(CheckoutActivity.EXTRA_DATA_JSON_STRING);
        JSONObject dataJsonObject = new JSONObject(dataJsonString);

        JSONArray salesProductsJsonArray = dataJsonObject.getJSONArray(SalesProduct.class.getSimpleName());

        for (int i = 0; i < salesProductsJsonArray.length(); i++) {

            JSONObject salesProductJSONObject = salesProductsJsonArray.getJSONObject(i);

            SalesProduct salesProduct = new SalesProduct();
            salesProduct.setProduct_id(salesProductJSONObject.getLong(SalesProductDao.Properties.Product_id.columnName));
            salesProduct.setQuantity(salesProductJSONObject.getInt(SalesProductDao.Properties.Quantity.columnName));
            salesProduct.setSub_total(Float.valueOf(
                            salesProductJSONObject.get(SalesProductDao.Properties.Sub_total.columnName).toString())
            );
            salesProduct.setSale_type(salesProductJSONObject.getString(SalesProductDao.Properties.Sale_type.columnName));

            salesProducts.add(salesProduct);
        }

        JSONArray rewardsJsonArray = dataJsonObject.getJSONArray(Reward.class.getSimpleName());
        for (int i = 0; i < rewardsJsonArray.length(); i++) {

            JSONObject rewardJsonObject = rewardsJsonArray.getJSONObject(i);

            Reward reward = new Reward();
            reward.setId(rewardJsonObject.getLong(RewardDao.Properties.Id.columnName));
            reward.setReward_condition(rewardJsonObject.getString(RewardDao.Properties.Reward_condition.columnName));
            reward.setCondition_product_id(rewardJsonObject.getInt(RewardDao.Properties.Condition_product_id.columnName));
            reward.setCondition(rewardJsonObject.getString(RewardDao.Properties.Condition.columnName));
            reward.setCondition_value(Float.valueOf(rewardJsonObject.get(RewardDao.Properties.Condition_value.columnName).toString()));
            reward.setReward_type(rewardJsonObject.getString(RewardDao.Properties.Reward_type.columnName));
            reward.setReward(rewardJsonObject.getString(RewardDao.Properties.Reward.columnName));
            reward.setReward_value(rewardJsonObject.getString(RewardDao.Properties.Reward_value.columnName));
            reward.setValid_from(formatter.parse(
                            rewardJsonObject.get(RewardDao.Properties.Valid_from.columnName).toString())
            );
            reward.setValid_until(formatter.parse(
                            rewardJsonObject.get(RewardDao.Properties.Valid_until.columnName).toString())
            );
            reward.setCreated_at(formatter.parse(
                            rewardJsonObject.get(RewardDao.Properties.Created_at.columnName).toString())
            );

            rewards.add(reward);
        }*/


        /*salesProducts = (ArrayList<SalesProduct>) bundle.get(SalesProductsViewFragment.EXTRA_SALES_PRODUCT_LIST);
        rewards = (ArrayList<Reward>) bundle.get(RewardViewFragment.EXTRA_REWARDS_LIST);*/
        totalAmount = bundle.getFloat(EXTRA_TOTAL_AMOUNT);

        totalDiscounts = bundle.getFloat(RewardViewFragment.EXTRA_TOTAL_DISCOUNT);

        isSenior = bundle.getBoolean(EXTRA_IS_SENIOR);

    }

    @Override
    protected void onResume() {
        super.onResume();
        this.setTitle(retailer.getStoreName());
        wifiDirectConnectivityDataPresenter.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        wifiDirectConnectivityDataPresenter.onDestroy();
        progressDialog.dismiss();
        //resetWifi();
    }

    private void resetWifi() {
        if (wifiManager.isWifiEnabled()) {
            wifiManager.setWifiEnabled(false);
            wifiManager.setWifiEnabled(true);
        } else {
            wifiManager.setWifiEnabled(true);
        }
    }


    @Override
    public void onViewReady() throws JSONException {

        if(isSenior){
            checkOutViewFragment.setWithSenior();
        }

        checkOutViewFragment.setTotalAmount(String.valueOf(totalAmount));

    }

    @Override
    public void onUpdateCustomerList() {
        wifiDirectConnectivityDataPresenter.discoverPeers(DeviceInfo.Type.CUSTOMER);

    }

    @Override
    public void onCustomerSelect(WifiP2pDevice customerDevice, boolean selected) {

        if (selected) {

            this.customerDevice = customerDevice;

        } else {

            if (this.customerDevice == customerDevice) {
                this.customerDevice = null;
            }

        }

    }

    @Override
    public void onComplete() {

        if((totalAmount > 0 && checkOutViewFragment.getAmountRecieved() <= 0)
                || checkOutViewFragment.getChange() < 0){
            checkOutViewFragment.setErrorAmountRecieved("Invalid value.");
            return;
        }

        setSalesIdToProductsAndRewards();
        Intent intent = new Intent(CheckoutActivity.this, MainActivity2.class);
        startActivity(intent);

        finish();


    }

    @Override
    public void onCompleteWithRewards() {

        if((totalAmount > 0 && checkOutViewFragment.getAmountRecieved() <= 0)
                || checkOutViewFragment.getChange() < 0){
            checkOutViewFragment.setErrorAmountRecieved("Invalid value.");
            return;
        }

        setSalesIdToProductsAndRewards();

        if (customerDevice != null) {
            wifiDirectConnectivityDataPresenter.connectToCustomer(customerDevice, 3001);
            showProgressDialog("Waiting for user ...");
            setProgressDialogCancelButtonVisibility(View.INVISIBLE);
            hideDialogLater(30000, "User did not respond after 30 seconds, please try again later.", true);
            hideProgressDialogLater = true;
        }


    }

    @Override
    public void onCancel() {

        removeSalesRecord();

        finish();

    }

    //In case the connection to customer device failed and store decided to cancel it.
    private void removeSalesRecord() {

        if (salesTransactionNumber != "") {

            List<Sales> salesList = salesDao.queryRaw(
                    "WHERE " +
                            SalesDao.Properties.Id.columnName +
                            "=?",
                    new String[]{salesTransactionNumber + ""}
            );

            for (Sales sales : salesList) {

                salesDao.delete(sales);

            }

            List<SalesProduct> salesProductList = salesProductDao.queryRaw(
                    "WHERE " +
                            SalesProductDao.Properties.Sales_transaction_number.columnName +
                            "=?",
                    new String[]{salesTransactionNumber + ""}
            );

            for (SalesProduct salesProduct : salesProductList) {

                salesProduct.setSales_transaction_number("");
                salesProductDao.update(salesProduct);

                revertDeductProductQuantity(salesProduct);

            }

            List<SalesHasReward> salesHasRewardList = salesHasRewardDao.queryRaw(
                    "WHERE " +
                            SalesHasRewardDao.Properties.Sales_transaction_number.columnName +
                            "=?",
                    new String[]{salesTransactionNumber + ""});

            for (SalesHasReward salesHasReward : salesHasRewardList) {

                salesHasRewardDao.delete(salesHasReward);

            }

        }

    }

    private void revertDeductProductQuantity(SalesProduct salesProduct) {

        List<Product> forRetailerProducts
                = productDao
                .queryBuilder()
                .where(
                        ProductDao.Properties.Id.eq(salesProduct.getProduct_id())
                ).limit(1).list();

        ItemInventoryDao itemInventoryDao
                = LoyaltyStoreApplication.getSession().getItemInventoryDao();

        if (forRetailerProducts.size() > 0) {

            Product forRetailerProduct = forRetailerProducts.get(0);

            if (!"For Direct Transactions".equals(forRetailerProduct.getType())) {

                List<Product> forDeliveryProducts
                        = productDao
                        .queryBuilder()
                        .where(
                                ProductDao.Properties.Id.eq(
                                        forRetailerProduct.getDeduct_product_to_id()
                                )
                        ).limit(1).list();

                if (forDeliveryProducts.size() > 0) {
                    Product product = forDeliveryProducts.get(0);

                    double quantityDeducted = forRetailerProduct.getDeduct_product_to_quantity() * salesProduct.getQuantity();

                    List<ItemInventory> itemInventoryList
                            = itemInventoryDao
                            .queryBuilder()
                            .where(
                                    ItemInventoryDao.Properties.Product_id.eq(
                                            product.getId()
                                    )
                            ).limit(1).list();

                    if (itemInventoryList.size() > 0) {

                        ItemInventory itemInventory = itemInventoryList.get(0);

                        itemInventory.setQuantity(itemInventory.getQuantity() + quantityDeducted);

                        itemInventoryDao.insertOrReplace(itemInventory);
                        itemInventoryDao.refresh(itemInventory);
                    }


                }

            } else {

                ItemInventory itemInventory = getItemInventory(forRetailerProduct.getId());
                if (itemInventory != null) {
                    itemInventory.setQuantity(itemInventory.getQuantity() + salesProduct.getQuantity());
                    itemInventoryDao.insertOrReplace(itemInventory);
                    itemInventoryDao.refresh(itemInventory);
                }

            }


        }

    }

    @Override
    public void onNewPeersDiscovered(List<WifiP2pDevice> wifiP2pDevices) {
        this.customerDeviceList.clear();
        this.customerDeviceList.addAll(wifiP2pDevices);
        customerDeviceListAdapter.notifyDataSetChanged();
    }

    @Override
    public void onConnectionEstablished() {
        Toast.makeText(CheckoutActivity.this, "Connection established.", Toast.LENGTH_SHORT).show();

        String jsonStringPurchaseInfo = null;

        try {
            /*jsonStringPurchaseInfo = generateDataToJsonString();

            SendPurchaseInfoForValidationTask sendPurchaseInfoForValidationTask =
                    new SendPurchaseInfoForValidationTask(
                            3001,
                            jsonStringPurchaseInfo, this
                    );
            sendPurchaseInfoForValidationTask.execute();*/

            Log.d(TAG, "ACTION TYPE : " + actionType);

            if (actionType.equals(ActionType.SEND_SALES)) {
                SendPurchaseInfoAndRewardsTask sendPurchaseInfoAndRewardsTask =
                        new SendPurchaseInfoAndRewardsTask(
                                CheckoutActivity.this,
                                3001,
                                generateSalesToJsonObject(),
                                this
                        );
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB)
                    sendPurchaseInfoAndRewardsTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                else
                    sendPurchaseInfoAndRewardsTask.execute();
            } else if (actionType.equals(ActionType.ACQUIRE_CLAIMED_REWARDS)) {
                AcquireClaimedRewardsTask acquireClaimedRewardsTask =
                        new AcquireClaimedRewardsTask(
                                3003, this
                        );

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB)
                    acquireClaimedRewardsTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                else
                    acquireClaimedRewardsTask.execute();

                //acquireClaimedRewardsTask.execute();

            }


        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onConnectionTerminated() {

    }

    private JSONObject generateSalesToJsonObject() throws JSONException {

        JSONObject jsonSalesObject = new JSONObject();

        List<Sales> salesList = salesDao.queryRaw(" WHERE " + SalesDao.Properties.Transaction_number.columnName + "=?",
                new String[]{salesProducts.get(0).getSales_transaction_number() + ""});

        for (Sales sales : salesList) {

            jsonSalesObject.put("id", sales.getId());
            jsonSalesObject.put("transaction_number", sales.getTransaction_number());
            jsonSalesObject.put("store_id", sales.getStore_id());
            jsonSalesObject.put("store_name", retailer.getStoreName());
            jsonSalesObject.put("customer_id", sales.getCustomer_id());
            jsonSalesObject.put("amount", sales.getAmount());
            jsonSalesObject.put("total_discount", sales.getTotal_discount());
            jsonSalesObject.put("transaction_date", sales.getTransaction_date());

        }

        return jsonSalesObject;


    }

    private String generateDataToJsonString() throws JSONException {

        JSONArray jsonArray = new JSONArray();
        JSONObject jsonObject = new JSONObject();
        JSONObject jsonSalesProductsObject = new JSONObject();
        JSONObject jsonSalesObject = new JSONObject();

        for (SalesProduct salesProduct : salesProducts) {

            jsonSalesProductsObject = new JSONObject();

            String sql = " WHERE " + ProductDao.Properties.Id.columnName + "=?";

            List<Product> products = productDao.queryRaw(sql,
                    new String[]{salesProduct.getProduct_id() + ""});

            for (Product product : products) {

                jsonSalesProductsObject.put("sales_transaction_number", salesProduct.getSales_transaction_number());
                jsonSalesProductsObject.put("product_id", salesProduct.getProduct_id());
                jsonSalesProductsObject.put("product_name", product.getName());
                jsonSalesProductsObject.put("unit_cost", product.getUnit_cost());
                jsonSalesProductsObject.put("sku", product.getSku());
                jsonSalesProductsObject.put("quantity", salesProduct.getQuantity());
                jsonSalesProductsObject.put("sub_total", salesProduct.getSub_total());
                jsonSalesProductsObject.put("sale_type", salesProduct.getSale_type());

                jsonArray.put(jsonSalesProductsObject);
            }
        }

        jsonObject.put(DATA_TYPE_JSON_SALES_PRODUCT, jsonArray);

        List<Sales> salesList = salesDao.queryRaw(" WHERE " + SalesDao.Properties.Transaction_number.columnName + "=?",
                new String[]{salesProducts.get(0).getSales_transaction_number() + ""});

        for (Sales sales : salesList) {

            jsonSalesObject.put("id", sales.getId());
            jsonSalesObject.put("transaction_number", sales.getTransaction_number());
            jsonSalesObject.put("store_id", sales.getStore_id());
            jsonSalesObject.put("store_name", retailer.getStoreName());
            jsonSalesObject.put("customer_id", sales.getCustomer_id());
            jsonSalesObject.put("amount", sales.getAmount());
            jsonSalesObject.put("total_discount", sales.getTotal_discount());
            jsonSalesObject.put("transaction_date", sales.getTransaction_date());

        }

        jsonObject.put(DATA_TYPE_JSON_SALES, jsonSalesObject);

        return jsonObject.toString();

    }

    private Sales generateSales() {

        SimpleDateFormat formatter = new SimpleDateFormat(
                "yyyyMMdd_HHmmss", Locale.ENGLISH);
        Date date = new Date();
        String stringDateTime = formatter.format(date);

        Retailer retailer = Retailer.getDeviceRetailerFromSharedPreferences(this);

        Log.d(TAG, " retailer store id : " + retailer.getStoreId());

        Sales sales = new Sales();
        sales.setTransaction_number(retailer.getStoreId() + "_" + stringDateTime);
        sales.setStore_id(retailer.getStoreId());
        sales.setAmount(totalAmount);
        sales.setTransaction_date(date);
        sales.setRemarks(checkOutViewFragment.getRemarks());
        sales.setAmount_received(checkOutViewFragment.getAmountRecieved());
        sales.setChange(checkOutViewFragment.getChange());
        sales.setIs_synced(false);

        long salesId = salesDao.insert(sales);
        this.salesTransactionNumber = retailer.getStoreId() + "_" + stringDateTime;

        return sales;
    }

    private void setSalesIdToProductsAndRewards() {

        if (salesTransactionNumber == "") {

            Sales sales = generateSales();

            for (SalesProduct salesProduct : salesProducts) {

                salesProduct.setSales_transaction_number(sales.getTransaction_number());
                salesProduct.setIs_returned(false);
                salesProduct.setTransaction_date(sales.getTransaction_date());
                salesProductDao.insert(salesProduct);

                deductProductQuantity(salesProduct);
                deductProductParticularsQuantity(salesProduct);


            }

            setSalesHasReward(sales);

        }

    }

    private void deductProductQuantity(SalesProduct salesProduct) {

        List<Product> forRetailerProducts
                = productDao
                .queryBuilder()
                .where(
                        ProductDao.Properties.Id.eq(salesProduct.getProduct_id())
                ).limit(1).list();

        ItemInventoryDao itemInventoryDao
                = LoyaltyStoreApplication.getSession().getItemInventoryDao();

        if (forRetailerProducts.size() > 0) {

            Product forRetailerProduct = forRetailerProducts.get(0);

            if (!"For Direct Transactions".equals(forRetailerProduct.getType())) {

                List<Product> forDeliveryProducts
                        = productDao
                        .queryBuilder()
                        .where(
                                ProductDao.Properties.Id.eq(
                                        forRetailerProduct.getDeduct_product_to_id()
                                )
                        ).limit(1).list();

                if (forDeliveryProducts.size() > 0 && forRetailerProduct.getDeduct_product_to_quantity() != null) {
                    Product product = forDeliveryProducts.get(0);

                    double quantityToDeduct = forRetailerProduct.getDeduct_product_to_quantity() * salesProduct.getQuantity();

                    ItemInventory itemInventory = getItemInventory(product.getId());

                    if (itemInventory != null) {
                        itemInventory.setQuantity(itemInventory.getQuantity() - quantityToDeduct);
                        itemInventory.setIs_updated(true);
                        itemInventoryDao.insertOrReplace(itemInventory);
                        itemInventoryDao.refresh(itemInventory);
                    }

                }
            } else {
                ItemInventory itemInventory = getItemInventory(forRetailerProduct.getId());
                if (itemInventory != null) {
                    itemInventory.setQuantity(itemInventory.getQuantity() - salesProduct.getQuantity());
                    itemInventory.setIs_updated(true);
                    itemInventoryDao.insertOrReplace(itemInventory);
                    itemInventoryDao.refresh(itemInventory);
                }
            }

        }

    }

    private void deductProductParticularsQuantity(SalesProduct salesProduct) {

        ProductBreakdownDao productBreakdownDao
                = LoyaltyStoreApplication.getSession().getProductBreakdownDao();

        ItemInventoryDao itemInventoryDao
                = LoyaltyStoreApplication.getSession().getItemInventoryDao();

        List<Product> productList
                = productDao
                .queryBuilder()
                .where(
                        ProductDao.Properties.Id.eq(
                                salesProduct.getProduct_id()
                        )
                ).list();

        for (Product product : productList) {

            List<ProductBreakdown> productBreakdownList
                    = productBreakdownDao
                    .queryBuilder()
                    .where(
                            ProductBreakdownDao.Properties.Ts.eq(
                                    product.getTs()
                            )
                    ).list();

            for (ProductBreakdown productBreakdown : productBreakdownList) {

                ItemInventory itemInventory
                        = getItemInventory(productBreakdown.getProduct_id());

                if (itemInventory != null) {
                    itemInventory.setQuantity(itemInventory.getQuantity() - (productBreakdown.getQuantity() * salesProduct.getQuantity()));
                    itemInventory.setIs_updated(true);
                    itemInventoryDao.insertOrReplace(itemInventory);
                    itemInventoryDao.refresh(itemInventory);
                }

            }

        }


    }

    private ItemInventory getItemInventory(long productId) {
        ItemInventoryDao itemInventoryDao
                = LoyaltyStoreApplication.getSession().getItemInventoryDao();

        List<ItemInventory> itemInventoryList
                = itemInventoryDao
                .queryBuilder()
                .where(
                        ItemInventoryDao.Properties.Product_id.eq(
                                productId
                        )
                ).limit(1).list();

        if (itemInventoryList.size() > 0)
            return itemInventoryList.get(0);
        else
            return null;

    }

    private void setSalesHasReward(Sales sales) {

        for (Reward reward : rewards) {

            SalesHasReward salesHasReward = new SalesHasReward();
            salesHasReward.setReward_id(reward.getId());
            salesHasReward.setSales_transaction_number(sales.getTransaction_number());

            salesHasRewardDao.insert(salesHasReward);

        }

    }


    @Override
    public void onPurchaseInfoSent(String customerDeviceId, final boolean isFirstUse) {

        List<Sales> salesList = salesDao.queryRaw(
                "WHERE " + SalesDao.Properties.Id.columnName + "=?",
                new String[]{salesTransactionNumber + ""}
        );

        for (Sales sales : salesList) {

            sales.setCustomer_id(Long.valueOf(customerDeviceId));
            salesDao.update(sales);

        }

        wifiDirectConnectivityDataPresenter.disconnect(new WifiP2pManager.ActionListener() {

            @Override
            public void onSuccess() {
                hideProgressDialog();

                if (isFirstUse) {

                    onClientFirstUse();

                } else {
                    CheckoutActivity.this.finish();
                    MainActivity.mainActivity.finish();
                    Intent intent = new Intent(CheckoutActivity.this, MainActivity2.class);
                    startActivity(intent);
                }

                Toast.makeText(CheckoutActivity.this, "Purchase Information Sent", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(int reason) {
                hideProgressDialog();
                if (isFirstUse) {

                    onClientFirstUse();

                } else {
                    CheckoutActivity.this.finish();

                    MainActivity.mainActivity.finish();
                    Intent intent = new Intent(CheckoutActivity.this, MainActivity2.class);
                    startActivity(intent);
                }

                Toast.makeText(CheckoutActivity.this, "Purchase information sent, but failed to disconnect to peer, please restart your wifi", Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void onClientFirstUse() {

        List<Reward> rewardList = rewardDao.queryBuilder()
                .where(RewardDao.Properties.Reward_condition.eq("first_use")).list();

        if (rewardList.size() > 0) {

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("CUSTOMER HAS REWARD(S) FOR FIRST USE \n");

            String message = "";

            for (Reward reward : rewardList) {

                message += reward.getReward() + "\n";

            }

            builder.setMessage(message);

            builder.setNegativeButton("Close", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                    CheckoutActivity.this.finish();

                    MainActivity.mainActivity.finish();
                    Intent intent = new Intent(CheckoutActivity.this, MainActivity2.class);
                    startActivity(intent);
                }
            });

            builder.show();

        }
    }

    protected void showProgressDialog(String message) {

        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setMessage(message);
        progressDialog.setIndeterminate(true);
        progressDialog.setCancelable(false);

        progressDialog.setButton(DialogInterface.BUTTON_NEGATIVE, "Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                onClaimedRewardsAcquired(new ArrayList<SalesHasReward>());
            }
        });

        progressDialog.show();

    }

    protected void hideProgressDialog() {
        progressDialog.cancel();
        progressDialog.dismiss();
    }

    protected void updateProgressDialogMessage(String message) {
        progressDialog.setMessage(message);
    }

    protected void setProgressDialogCancelButtonVisibility(int visible) {
        progressDialog.getButton(ProgressDialog.BUTTON_NEGATIVE).setVisibility(visible);
    }

    protected void hideDialogLater(int hideAfterMillis, final String onHideToastMessage, final boolean disconnectWhenReached) {
        new android.os.Handler().postDelayed(
                new Runnable() {
                    public void run() {
                        if (progressDialog.isShowing() && hideProgressDialogLater) {
                            hideProgressDialog();
                            if (onHideToastMessage != null) {
                                Toast.makeText(CheckoutActivity.this, onHideToastMessage, Toast.LENGTH_LONG).show();
                                wifiDirectConnectivityDataPresenter.cancelconnect();
                            }

                            if (disconnectWhenReached && WifiDirectConnectivityState.getInstance().isConnectedToDevice()) {
//                                customerPurchasesViewEventListener.onCancel();

                            }

                            disconnectPeers();

                        }
                    }
                },
                hideAfterMillis);
    }

    @Override
    public void onCustomerIdAcquired(long customerDeviceId) {
        this.customerDeviceId = customerDeviceId;
    }

    @Override
    public void onCustomerTransactionRecordsAcquired(boolean isFirstUse) {
        this.isFirstUse = isFirstUse;
    }

    @Override
    public void onSalesSent() {
        Log.d(TAG, ">>> SALES SENT");
    }

    @Override
    public void onRewardsSent() {
        Log.d(TAG, ">>> REWARD SENT");

        List<Sales> salesList = salesDao.queryRaw(
                "WHERE " + SalesDao.Properties.Id.columnName + "=?",
                new String[]{salesTransactionNumber + ""}
        );

        for (Sales sales : salesList) {

            sales.setCustomer_id(Long.valueOf(customerDeviceId));
            salesDao.update(sales);

        }

        actionType = ActionType.ACQUIRE_CLAIMED_REWARDS;
        onSendTransactionDone();

    }

    private void onSendTransactionDone() {

        disconnectPeers();

        List<SalesHasReward> salesHasRewards =
                salesHasRewardDao
                        .queryBuilder()
                        .where(
                                SalesHasRewardDao.Properties.Sales_transaction_number.eq(
                                        salesTransactionNumber
                                )
                        ).list();

        if (salesHasRewards.size() > 0) {

            StringBuilder message = new StringBuilder(); // Using default 16 character size

            message.append("Waiting for the customer to claim....");
            message.append(System.getProperty("line.separator"));
            message.append(System.getProperty("line.separator"));

            message.append("CUSTOMER IS QUALIFIED FOR THE FOLLOWING REWARDS.");
            message.append(System.getProperty("line.separator"));
            message.append(System.getProperty("line.separator"));

            for (SalesHasReward salesHasReward : salesHasRewards) {

                List<Reward> rewardList =
                        rewardDao
                                .queryBuilder()
                                .where(
                                        RewardDao.Properties.Id.eq(
                                                salesHasReward.getReward_id()
                                        )
                                ).list();

                for (Reward reward : rewardList) {
                    message.append("*");
                    message.append(reward.getReward());
                    message.append(System.getProperty("line.separator"));
                }
            }

            showProgressDialog(message.toString());
            setProgressDialogCancelButtonVisibility(View.VISIBLE);
            hideProgressDialogLater = false;

        } else {
            closeCheckoutActivity();
        }

    }

    private void disconnectPeers() {

        hideProgressDialog();

        wifiDirectConnectivityDataPresenter.disconnect(new WifiP2pManager.ActionListener() {

            @Override
            public void onSuccess() {

                //closeCheckoutActivity();
                //Toast.makeText(CheckoutActivity.this, "Purchase Information Sent", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(int reason) {

                //closeCheckoutActivity();
                //Toast.makeText(CheckoutActivity.this, "Purchase information sent, but failed to disconnect to peer, please restart your wifi", Toast.LENGTH_SHORT).show();
            }
        });

    }

    @Override
    public void onClaimedRewardsAcquired(final List<SalesHasReward> claimedRewardsList) {

        hideProgressDialog();
        disconnectPeers();

        if (claimedRewardsList.size() > 0) {

            Log.d(TAG, " ========== CLAIMED REWARDS START ========== ");

            for (SalesHasReward salesHasReward : claimedRewardsList) {

                Log.d(TAG, "TRANSACTION NUMBER : " + salesHasReward.getSales_transaction_number());
                Log.d(TAG, "REWARD ID : " + salesHasReward.getReward_id());

            }

            Log.d(TAG, " ========== CLAIMED REWARDS END ========== ");

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("CUSTOMER REWARDS CLAIMED \n");

            String message = "";

            for (SalesHasReward salesHasReward : claimedRewardsList) {

                List<Reward> rewardList =
                        rewardDao
                                .queryBuilder()
                                .where(
                                        RewardDao.Properties.Id.eq(
                                                salesHasReward.getReward_id()
                                        )
                                ).list();

                for (Reward reward : rewardList) {

                    message += reward.getReward() + "\n";

                }

            }

            builder.setMessage(message);

            builder.setNegativeButton("Close", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                    addClaimedRewardsToSalesTransactionRecords(claimedRewardsList);
                    closeCheckoutActivity();
                }
            });

            builder.show();

        } else {
            closeCheckoutActivity();
        }


    }

    private void addClaimedRewardsToSalesTransactionRecords(List<SalesHasReward> salesHasRewards) {


        Log.d(TAG, "claimedRewardListCount : " + salesHasRewards.size());

        SimpleDateFormat formatter = new SimpleDateFormat(
                "yyyyMMdd_HHmmss", Locale.ENGLISH);
        Date date = new Date();
        String stringDateTime = formatter.format(date);

        String rewardTransactionNumber = retailer.getStoreId() + "_" + stringDateTime;

        Sales sales = new Sales();
        sales.setCustomer_id(customerDeviceId);
        sales.setTransaction_number(rewardTransactionNumber);
        sales.setRemarks("Free Product(s)");
        sales.setAmount(Float.valueOf(0));
        sales.setStore_id(retailer.getStoreId());
        sales.setTotal_discount(Float.valueOf(0));
        sales.setTransaction_date(new Date());
        sales.setIs_synced(false);
        salesDao.insert(sales);

        for (SalesHasReward salesHasReward : salesHasRewards) {

            List<Reward> rewardList
                    = rewardDao
                    .queryBuilder()
                    .where(
                            RewardDao.Properties.Id.eq(
                                    salesHasReward.getReward_id()
                            )
                    ).list();

            for (Reward reward : rewardList) {

                List<Product> products =
                        productDao
                                .queryBuilder()
                                .where(
                                        ProductDao.Properties.Id.eq(
                                                reward.getCondition_product_id()
                                        )
                                ).list();

                for (Product product : products) {

                    SalesProduct salesProduct = new SalesProduct();
                    salesProduct.setSale_type("FREEBIE");
                    salesProduct.setSales_transaction_number(rewardTransactionNumber);
                    salesProduct.setSub_total(Float.valueOf(0));
                    salesProduct.setProduct_id(product.getId());
                    salesProduct.setQuantity(1);

                    salesProductDao.insert(salesProduct);

                }


            }


        }


    }

    @Override
    public void onEnteredAmountReceived(String amountReceivedString) {
        if (!"".equals(amountReceivedString.trim())) {

            float cashEntered = Float.valueOf(amountReceivedString);

            float change = cashEntered - totalAmount;

            DecimalFormat decimalFormat = Constants.DECIMAL_FORMAT;

            checkOutViewFragment.setChange(decimalFormat.format(change));

        }
    }

    private void closeCheckoutActivity() {

        resetWifi();
        CheckoutActivity.this.finish();
        MainActivity.mainActivity.finish();
        Intent intent = new Intent(CheckoutActivity.this, MainActivity.class);
        startActivity(intent);

    }

}

