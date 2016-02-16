package ph.com.gs3.loyaltystore;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import ph.com.gs3.loyaltystore.adapters.CustomerDeviceListAdapter;
import ph.com.gs3.loyaltystore.fragments.CheckOutViewFragment;
import ph.com.gs3.loyaltystore.fragments.RewardViewFragment;
import ph.com.gs3.loyaltystore.models.WifiDirectConnectivityState;
import ph.com.gs3.loyaltystore.models.sqlite.dao.Product;
import ph.com.gs3.loyaltystore.models.sqlite.dao.ProductDao;
import ph.com.gs3.loyaltystore.models.sqlite.dao.Reward;
import ph.com.gs3.loyaltystore.models.sqlite.dao.RewardDao;
import ph.com.gs3.loyaltystore.models.sqlite.dao.Sales;
import ph.com.gs3.loyaltystore.models.sqlite.dao.SalesDao;
import ph.com.gs3.loyaltystore.models.sqlite.dao.SalesHasReward;
import ph.com.gs3.loyaltystore.models.sqlite.dao.SalesHasRewardDao;
import ph.com.gs3.loyaltystore.models.sqlite.dao.SalesProduct;
import ph.com.gs3.loyaltystore.models.sqlite.dao.SalesProductDao;
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
        SendPurchaseInfoForValidationTask.SendPurchaseInfoForValidationTaskEventListener {

    public static final String TAG = CheckoutActivity.class.getSimpleName();
    public static final String EXTRA_DATA_JSON_STRING = "data_json_string";
    public static final String EXTRA_TOTAL_AMOUNT = "total_amount";
    public static final String DATA_TYPE_JSON_SALES = "sales";
    public static final String DATA_TYPE_JSON_SALES_PRODUCT = "sales_product";

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

    private long salesId;

    private static final SimpleDateFormat formatter = new SimpleDateFormat(
            "EEE MMM d HH:mm:ss zzz yyyy");

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        salesId = -1;

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

        String dataJsonString = bundle.getString(CheckoutActivity.EXTRA_DATA_JSON_STRING);
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
        }


        /*salesProducts = (ArrayList<SalesProduct>) bundle.get(SalesProductsViewFragment.EXTRA_SALES_PRODUCT_LIST);
        rewards = (ArrayList<Reward>) bundle.get(RewardViewFragment.EXTRA_REWARDS_LIST);*/
        totalAmount = bundle.getFloat(EXTRA_TOTAL_AMOUNT);
        totalDiscounts = bundle.getFloat(RewardViewFragment.EXTRA_TOTAL_DISCOUNT);


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
    }


    @Override
    public void onViewReady() throws JSONException {

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

        setSalesIdToProductsAndRewards();
        finish();


    }

    @Override
    public void onCompleteWithRewards() {

        setSalesIdToProductsAndRewards();

        if (customerDevice != null) {
            wifiDirectConnectivityDataPresenter.connectToCustomer(customerDevice, 3001);
            showSubmitDocumentDialog("Waiting for user ...");
            hideDialogLater(30000, "User did not respond after 30 seconds, please try again later.", true);
        }

    }

    @Override
    public void onCancel() {

        removeSalesRecord();

        finish();

    }


    //In case the connection to customer device failed and store decided to cancel it.
    private void removeSalesRecord(){

        if(salesId != -1){

            List<Sales> salesList = salesDao.queryRaw(
                    "WHERE " +
                    SalesDao.Properties.Id.columnName + 
                    "=?",
                    new String[]{salesId + ""}
            );
            
            for(Sales sales : salesList){
                
                salesDao.delete(sales);
                
            }
            
            List<SalesProduct> salesProductList = salesProductDao.queryRaw(
                    "WHERE " +
                    SalesProductDao.Properties.Sales_id.columnName +
                    "=?",
                    new String[]{salesId + ""}
            );

            for(SalesProduct salesProduct : salesProductList){

                salesProduct.setSales_id(null);
                salesProductDao.update(salesProduct);

            }

            List<SalesHasReward> salesHasRewardList = salesHasRewardDao.queryRaw(
                    "WHERE " +
                    SalesHasRewardDao.Properties.Sales_id.columnName +
                    "=?",
                    new String[]{salesId + ""});

            for(SalesHasReward salesHasReward : salesHasRewardList){

                salesHasRewardDao.delete(salesHasReward);

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
            jsonStringPurchaseInfo = generateDataToJsonString();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        SendPurchaseInfoForValidationTask sendPurchaseInfoForValidationTask =
                new SendPurchaseInfoForValidationTask(
                        3001,
                        jsonStringPurchaseInfo, this
                );
        sendPurchaseInfoForValidationTask.execute();
    }

    @Override
    public void onConnectionTerminated() {

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

                jsonSalesProductsObject.put("sales_id", salesProduct.getSales_id());
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

        List<Sales> salesList = salesDao.queryRaw(" WHERE " + SalesDao.Properties.Id.columnName + "=?",
                new String[]{salesProducts.get(0).getSales_id() + ""});

        for (Sales sales : salesList) {

            jsonSalesObject.put("id", sales.getId());
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

    private long generateSalesID() {


        SimpleDateFormat formatter = new SimpleDateFormat(
                "yyyy-MM-dd HH:mm:ss", Locale.ENGLISH);
        Date date = new Date();
        String currDateTime = formatter.format(date);

        Sales sales = new Sales();
        sales.setStore_id(retailer.getStoreId());
        sales.setAmount(totalAmount);
        sales.setTransaction_date(date);

        long salesId = salesDao.insert(sales);
        this.salesId = salesId;

        return salesId;
    }

    private void setSalesIdToProductsAndRewards() {

        if (salesId == -1) {

            long salesId = generateSalesID();

            for (SalesProduct salesProduct : salesProducts) {

                salesProduct.setSales_id(salesId);
                salesProductDao.insert(salesProduct);

            }

            setSalesHasReward(salesId);

        }


    }

    private void setSalesHasReward(long salesId) {

        for (Reward reward : rewards) {

            SalesHasReward salesHasReward = new SalesHasReward();
            salesHasReward.setSales_id(salesId);
            salesHasReward.setReward_id(reward.getId());

            salesHasRewardDao.insert(salesHasReward);

        }

    }


    @Override
    public void onPurchaseInfoSent(String customerDeviceId) {

        List<Sales> salesList = salesDao.queryRaw(
                "WHERE " + SalesDao.Properties.Id.columnName + "=?",
                new String[]{salesId + ""}
        );

        for (Sales sales : salesList) {

            sales.setCustomer_id(Long.valueOf(customerDeviceId));
            salesDao.update(sales);

        }


        wifiDirectConnectivityDataPresenter.disconnect(new WifiP2pManager.ActionListener() {

            @Override
            public void onSuccess() {
                hideSubmitDocumentDialog();
                CheckoutActivity.this.finish();
                Toast.makeText(CheckoutActivity.this, "Purchase Information Sent", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(int reason) {
                hideSubmitDocumentDialog();
                CheckoutActivity.this.finish();
                Toast.makeText(CheckoutActivity.this, "Purchase information sent, but failed to disconnect to peer, please restart your wifi", Toast.LENGTH_SHORT).show();
            }
        });

        MainActivity.mainActivity.finish();
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    protected void showSubmitDocumentDialog(String message) {

        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setMessage(message);
        progressDialog.setIndeterminate(true);
        progressDialog.setCancelable(false);
        progressDialog.show();

    }

    protected void hideSubmitDocumentDialog() {
        progressDialog.hide();
    }

    protected void updateSubmitDocumentDialogMessage(String message) {
        progressDialog.setMessage(message);
    }

    protected void hideDialogLater(int hideAfterMillis, final String onHideToastMessage, final boolean disconnectWhenReached) {
        new android.os.Handler().postDelayed(
                new Runnable() {
                    public void run() {
                        if (progressDialog.isShowing()) {
                            hideSubmitDocumentDialog();
                            if (onHideToastMessage != null) {
                                Toast.makeText(CheckoutActivity.this, onHideToastMessage, Toast.LENGTH_LONG).show();
                                wifiDirectConnectivityDataPresenter.cancelconnect();
                            }

                            if (disconnectWhenReached && WifiDirectConnectivityState.getInstance().isConnectedToDevice()) {
//                                customerPurchasesViewEventListener.onCancel();

                            }

                        }
                    }
                },
                hideAfterMillis);
    }
}
