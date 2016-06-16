package ph.com.gs3.loyaltystore;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings.Secure;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import ph.com.gs3.loyaltystore.adapters.SalesProductWithReturnListAdapter;
import ph.com.gs3.loyaltystore.fragments.MainViewFragment;
import ph.com.gs3.loyaltystore.fragments.RewardViewFragment;
import ph.com.gs3.loyaltystore.fragments.SalesProductsViewFragment;
import ph.com.gs3.loyaltystore.models.services.AdvertisementSenderService;
import ph.com.gs3.loyaltystore.models.services.DiscoverPeersOnBackgroundService;
import ph.com.gs3.loyaltystore.models.sqlite.dao.Product;
import ph.com.gs3.loyaltystore.models.sqlite.dao.ProductDao;
import ph.com.gs3.loyaltystore.models.sqlite.dao.ProductDelivery;
import ph.com.gs3.loyaltystore.models.sqlite.dao.ProductDeliveryDao;
import ph.com.gs3.loyaltystore.models.sqlite.dao.Reward;
import ph.com.gs3.loyaltystore.models.sqlite.dao.RewardDao;
import ph.com.gs3.loyaltystore.models.sqlite.dao.SalesProduct;
import ph.com.gs3.loyaltystore.models.sqlite.dao.SalesProductDao;
import ph.com.gs3.loyaltystore.models.values.Retailer;


public class MainActivity extends AppCompatActivity implements
        MainViewFragment.MainViewFragmentEventListener {

    public static final String TAG = MainActivity.class.getSimpleName();

    private static final int MENU_SEND_AND_RECIEVE = 1;
    private static final int MENU_RETURNS_TO_COMMISSARY = 2;
    private static final int MENU_EXPENSES = 3;
    private static final int MENU_SALES = 4;
    private static final int MENU_DELIVERY_HISTORY = 5;
    private static final int MENU_DELIVERY_FOR_CONFIRMATION_LIST = 6;
    private static final int MENU_INVENTORY = 7;
    private static final int MENU_SETTINGS = 8;

    public static Activity mainActivity;

    private MainViewFragment mainViewFragment;

    private SalesProductWithReturnListAdapter salesProductWithReturnListAdapter;

    private Retailer retailer;

    private float totalDiscount;

    private String deviceId;

    private List<Product> products;
    private List<SalesProduct> salesProducts;
    private List<Reward> rewards;

    private ProductDao productDao;
    private RewardDao rewardDao;
    private SalesProductDao salesProductDao;

    private Intent discoverPeersOnBackgroundIntent;

    private static final SimpleDateFormat formatter = new SimpleDateFormat(
            "yyyy-MM-dd", Locale.ENGLISH);

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mainActivity = this;

        retailer = Retailer.getDeviceRetailerFromSharedPreferences(this);

        deviceId = Secure.getString(this.getContentResolver(),
                Secure.ANDROID_ID);

        retailer.setDeviceId(deviceId);

        retailer.save(this);

        mainViewFragment = (MainViewFragment) getFragmentManager().findFragmentByTag(MainViewFragment.TAG);

        products = new ArrayList<>();
        salesProducts = new ArrayList<>();

        salesProducts = new ArrayList<>();
        salesProductWithReturnListAdapter = new SalesProductWithReturnListAdapter(this);

        rewards = new ArrayList<>();

        if (mainViewFragment == null) {
            mainViewFragment = MainViewFragment.createInstance(salesProductWithReturnListAdapter);
            getFragmentManager().beginTransaction().add(R.id.container, mainViewFragment, MainViewFragment.TAG).commit();
        }

        productDao = LoyaltyStoreApplication.getInstance().getSession().getProductDao();
        rewardDao = LoyaltyStoreApplication.getInstance().getSession().getRewardDao();
        salesProductDao = LoyaltyStoreApplication.getInstance().getSession().getSalesProductDao();

        startBackgroundService();

    }

    private void startBackgroundService() {

        setServiceIntent();

        if (!isServiceRunning(DiscoverPeersOnBackgroundService.class)) {
            this.startService(discoverPeersOnBackgroundIntent);

        } else {
            Log.d(TAG, "DiscoverPeersOnBackgroundService SERVICE ALREADY RUNNING!");
        }

    }


    private void setServiceIntent() {

        discoverPeersOnBackgroundIntent = new Intent(this, DiscoverPeersOnBackgroundService.class);

    }

    /*@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_default, menu);
        return true;
    }*/

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        menu.clear();

        menu.add(0, MENU_SEND_AND_RECIEVE, Menu.NONE, "Send and Receive")
                .setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM | MenuItem.SHOW_AS_ACTION_WITH_TEXT);
        menu.add(0, MENU_RETURNS_TO_COMMISSARY, Menu.NONE, "Returns To Commissary")
                .setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM | MenuItem.SHOW_AS_ACTION_WITH_TEXT);
        menu.add(0, MENU_EXPENSES, Menu.NONE, "Expenses")
                .setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM | MenuItem.SHOW_AS_ACTION_WITH_TEXT);
        menu.add(0, MENU_SALES, Menu.NONE, "Sales")
                .setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM | MenuItem.SHOW_AS_ACTION_WITH_TEXT);
        menu.add(0, MENU_DELIVERY_HISTORY, Menu.NONE, "Delivery History")
                .setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM | MenuItem.SHOW_AS_ACTION_WITH_TEXT);
        menu.add(0, MENU_DELIVERY_FOR_CONFIRMATION_LIST, Menu.NONE, "Delivery For Confirmation List")
                .setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM | MenuItem.SHOW_AS_ACTION_WITH_TEXT);
        menu.add(0, MENU_INVENTORY, Menu.NONE, "Inventory")
                .setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM | MenuItem.SHOW_AS_ACTION_WITH_TEXT);
        menu.add(0, MENU_SETTINGS, Menu.NONE, "Settings")
                .setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM | MenuItem.SHOW_AS_ACTION_WITH_TEXT);

        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        /*switch (id) {

            case R.id.action_Main_Sync:
                navigateToActivity(SynchronizeWithAgentActivity.class);
                break;
            case R.id.action_Main_Items_To_Return:
                navigateToActivity(ViewItemReturnActivity.class);
                break;
            case R.id.action_Main_Expenses:
                navigateToActivity(ExpensesActivity.class);
                break;
            case R.id.action_Main_Sales:
                navigateToActivity(SalesActivity.class);
                break;
            case R.id.action_Main_Settings:
                navigateToActivity(SettingsActivity.class);
                break;
            case R.id.action_Main_Confirm_Delivery:
                navigateToActivity(ConfirmProductDeliveryActivity.class);
                break;
            case R.id.action_Main_Deliveries:
                navigateToActivity(DeliveryActivity.class);
                break;
            case R.id.action_Main_Inventory:
                navigateToActivity(InventoryActivity.class);
                break;
        }*/

        switch (id) {

            case MENU_SEND_AND_RECIEVE:
                navigateToActivity(SynchronizeWithAgentActivity.class);
                break;
            case MENU_RETURNS_TO_COMMISSARY:
                navigateToActivity(ViewItemReturnActivity.class);
                break;
            case MENU_EXPENSES:
                navigateToActivity(ExpensesActivity.class);
                break;
            case MENU_SALES:
                navigateToActivity(SalesActivity.class);
                break;
            case MENU_SETTINGS:
                navigateToActivity(SettingsActivity.class);
                break;
            case MENU_DELIVERY_FOR_CONFIRMATION_LIST:
                navigateToActivity(ConfirmProductDeliveryActivity.class);
                break;
            case MENU_DELIVERY_HISTORY:
                navigateToActivity(DeliveryHistoryActivity.class);
                break;
            case MENU_INVENTORY:
                navigateToActivity(InventoryActivity.class);
                break;
        }

        return super.onOptionsItemSelected(item);

    }

    private void navigateToActivity(Class activityClass) {

        Intent intent = new Intent(this, activityClass);
        startActivity(intent);

    }


    @Override
    protected void onResume() {
        super.onResume();

        this.setTitle(retailer.getStoreName());

        rewards.clear();

        //remove freebies
        for (int i = salesProducts.size() - 1; i >= 0; i--) {
            if (salesProducts.get(i).getSale_type().equals("FREEBIE")) {
                salesProducts.remove(i);
            }
        }
        totalDiscount = 0;

        this.setTitle(retailer.getStoreName());

        ProductDeliveryDao productDeliveryDao = LoyaltyStoreApplication.getSession().getProductDeliveryDao();

        List<ProductDelivery> productDeliveryInSQLite = productDeliveryDao.loadAll();

       /*Log.d(TAG, " +++++++++++++++++++++++++++++++++++++++++++++++++++");

        for(ProductDelivery p : productDeliveryInSQLite){

            Log.d(TAG, " Product Name : " + p.getName());
            Log.d(TAG, " Status : " + p.getStatus());

        }

        Log.d(TAG, " +++++++++++++++++++++++++++++++++++++++++++++++++++");*/

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (isServiceRunning(DiscoverPeersOnBackgroundService.class)) {
            stopService(discoverPeersOnBackgroundIntent);
        }
    }

    @Override
    public void onViewReady() {

        //Log.d(TAG, "STORE ID :" + retailer.getStoreId());

        List<Product> productList = productDao.loadAll();

        //Log.d(TAG, "PRODUCTS SIZE : " + productList.size());

        /*productDao.deleteAll();

        Product product1 = new Product();
        product1.setName("Cassava Cake Original Big");
        product1.setUnit_cost((float) 190);
        product1.setSku("CCakeOrigB");

        products.add(product1);

        Product product2 = new Product();
        product2.setName("Cassava Cake Original Small");
        product2.setUnit_cost((float) 100);
        product2.setSku("CCakeOrigS");

        products.add(product2);

        Product product3 = new Product();
        product3.setName("Flavored Macapuno/Langka Big");
        product3.setUnit_cost((float) 210);
        product3.setSku("McpnB");

        products.add(product3);

        Product product4 = new Product();
        ;
        product4.setName("Flavored Macapuno/Langka Small");
        product4.setUnit_cost((float) 110);
        product4.setSku("McpnS");

        products.add(product4);

        Product product5 = new Product();
        product5.setName("Donbit's 3's");
        product5.setUnit_cost((float) 75);
        product5.setSku("Dbits3");

        products.add(product5);

        Product product6 = new Product();
        product6.setName("Donbit's Single");
        product6.setUnit_cost((float) 25);
        product6.setSku("Dbits1");

        products.add(product6);

        Product product7 = new Product();
        product7.setName("Pichi-Pichi Small");
        product7.setUnit_cost((float) 60);
        product7.setSku("PchiS");

        products.add(product7);

        Product product8 = new Product();
        product8.setName("Pichi-Pichi Medium");
        product8.setUnit_cost((float) 150);
        product8.setSku("PchiM");

        products.add(product8);

        Product product9 = new Product();
        product9.setName("Pichi-Pichi Big");
        product9.setUnit_cost((float) 250);
        product9.setSku("PchiB");

        products.add(product9);

        Product product10 = new Product();
        product10.setName("Leche Flan");
        product10.setUnit_cost((float) 125);
        product10.setSku("LFlan");

        products.add(product10);

        Product product11 = new Product();
        product11.setName("Ubenito");
        product11.setUnit_cost((float) 150);
        product11.setSku("Uben");

        products.add(product11);

        Product product12 = new Product();
        product12.setName("Nilupak");
        product12.setUnit_cost((float) 125);
        product12.setSku("Npak");

        products.add(product12);

        productDao.insertInTx(products);

        List<Product> test = productDao.loadAll();

        for (int i = 0; i < test.size(); i++) {
            Product a = test.get(i);
            Log.d(TAG, "PRODUCT :" + a.getId() + "~" + a.getName());
        }

        rewardDao.deleteAll();

        List<Reward> sampleRewards = new ArrayList<>();

        Date dateobj = new Date();
        String currDate = formatter.format(dateobj);

        java.sql.Date dateSQL = java.sql.Date.valueOf(currDate);

        Reward reward1 = new Reward();
        reward1.setReward_condition("Number of Products Purchased / Product Purchased");
        reward1.setCondition_product_id(2);
        reward1.setCondition("Equal To");
        reward1.setCondition_value((float) 5);
        reward1.setReward_type("Free Product");
        reward1.setReward("Free Nilupak for every 5 or more Cassava Cake Original Small");
        reward1.setReward_value("Nilupak");
        reward1.setValid_from(java.sql.Date.valueOf("2016-02-01"));
        reward1.setValid_until(java.sql.Date.valueOf("2016-02-27"));
        reward1.setCreated_at(dateSQL);

        sampleRewards.add(reward1);

        *//*Reward reward2 = new Reward();
        reward2.setReward_condition("Number of Products Purchased / Product Purchased");
        reward2.setCondition_product_id(2);
        reward2.setCondition("Greater Than Or Equal To");
        reward2.setCondition_value((float) 5);
        reward2.setReward_type("Discount");
        reward2.setReward("250 discount for every 5 or more Cassava Cake Original Small");
        reward2.setReward_value("250");
        reward2.setValid_from(java.sql.Date.valueOf("2016-02-01"));
        reward2.setValid_until(java.sql.Date.valueOf("2016-02-27"));
        reward2.setCreated_at(dateSQL);

        sampleRewards.add(reward2);*//*

        Reward reward3 = new Reward();
        reward3.setReward_condition("Purchase Amount");
        reward3.setCondition_product_id(0);
        reward3.setCondition("Greater Than");
        reward3.setCondition_value((float) 500);
        reward3.setReward_type("Discount");
        reward3.setReward("50 discount for every 500+ worth of purchase.");
        reward3.setReward_value("50");
        reward3.setValid_from(java.sql.Date.valueOf("2016-02-01"));
        reward3.setValid_until(java.sql.Date.valueOf("2016-02-27"));
        reward3.setCreated_at(dateSQL);

        sampleRewards.add(reward3);

        *//*Reward reward4 = new Reward();
        reward4.setReward_condition("Purchase Amount");
        reward4.setCondition_product_id(0);
        reward4.setCondition("Equal To");
        reward4.setCondition_value((float) 350);
        reward4.setReward_type("Discount");
        reward4.setReward("100 discount for every 350 purchase amount.");
        reward4.setReward_value("100");
        reward4.setValid_from(java.sql.Date.valueOf("2016-02-01"));
        reward4.setValid_until(java.sql.Date.valueOf("2016-02-27"));
        reward4.setCreated_at(dateSQL);

        sampleRewards.add(reward4);*//*

        rewardDao.insertInTx(sampleRewards);

        List<Reward> rewardList = rewardDao.loadAll();
        for (Reward r : rewardList) {
            Log.d(TAG, "REWARD : " + r.getCondition() + " ~ " + r.getValid_from());
        }*/

        products = productDao.loadAll();

        if (products.isEmpty()) {

            if (retailer.getStoreName().equals("") && retailer.getServerUrl().equals("")) {

                Intent intent = new Intent(this, SettingsActivity.class);
                startActivity(intent);

            }

        } else {

            mainViewFragment.setMenuButtons();

        }


    }

    @Override
    public void onProductClicked(final Product product) {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("");
        builder.setMessage(product.getName());

        final EditText input = new EditText(this);

        input.setInputType(InputType.TYPE_CLASS_NUMBER);
        builder.setView(input);

        // Set up the buttons
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                if (!input.getText().toString().equals("")) {

                    int quantity = Integer.parseInt(input.getText().toString());

                    if (quantity != 0) {

                        if (quantity > 9999) {

                            Toast.makeText(MainActivity.this, "Invalid input", Toast.LENGTH_SHORT).show();

                        } else {
                            onAddSalesProduct(product, quantity);
                        }

                    }

                }

            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.show();

    }

    @Override
    public void onCheckOut() {

        if (!salesProducts.isEmpty()) {

            DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    switch (which) {
                        case DialogInterface.BUTTON_POSITIVE:
                            //Yes button clicked
                            checkout();

                            break;

                        case DialogInterface.BUTTON_NEGATIVE:
                            //No button clicked
                            break;
                    }
                }
            };

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("Are you sure you want to checkout?").setPositiveButton("Yes", dialogClickListener)
                    .setNegativeButton("No", dialogClickListener).show();

        }

    }

    private void checkout() {

        checkForRewards();

        if (isServiceRunning(DiscoverPeersOnBackgroundService.class)) {
            stopService(new Intent(MainActivity.this, DiscoverPeersOnBackgroundService.class));
        }

        if (isServiceRunning(DiscoverPeersOnBackgroundService.class)) {
            stopService(discoverPeersOnBackgroundIntent);
        }

        Intent intent = new Intent(this, CheckoutActivity.class);
        /*try {
            intent.putExtra(CheckoutActivity.EXTRA_DATA_JSON_STRING,
                    convertToJsonString(salesProducts, rewards));
        } catch (JSONException e) {
            e.printStackTrace();
        }*/

        Gson gson = new Gson();

        intent.putExtra(SalesProductsViewFragment.EXTRA_SALES_PRODUCT_LIST, gson.toJson(salesProducts));
        intent.putExtra(RewardViewFragment.EXTRA_REWARDS_LIST, gson.toJson(rewards));

        Log.d(TAG, " totalDISCOUNT : " + totalDiscount);

        intent.putExtra(
                CheckoutActivity.EXTRA_TOTAL_AMOUNT,
                mainViewFragment.getTotalAmount() - totalDiscount
        );
        intent.putExtra(RewardViewFragment.EXTRA_TOTAL_DISCOUNT, totalDiscount);

        startActivity(intent);

    }

    @Override
    public void onClearTransaction() {
        salesProducts.clear();
        salesProductWithReturnListAdapter.notifyDataSetChanged();
        mainViewFragment.setTotalAmount(0);
    }


    @Override
    public void onRemoveTransaction(SalesProduct salesProduct) {

        int itemIndexToRemove = -1;

        for (int i = 0; i < salesProducts.size(); i++) {
            if (salesProducts.get(i) == salesProduct) {
                itemIndexToRemove = i;
            }
        }

        if (itemIndexToRemove != -1) {

            float totalAmount = mainViewFragment.getTotalAmount();

            totalAmount -= salesProduct.getSub_total();
            this.salesProducts.remove(itemIndexToRemove);
            salesProductWithReturnListAdapter.notifyDataSetChanged();
            mainViewFragment.setTotalAmount(totalAmount);
        }
    }

    @Override
    public void onSynchronizeClicked() {

        Intent intent = new Intent(this, SynchronizeWithAgentActivity.class);
        startActivity(intent);

    }

    @Override
    public void onSettingsClicked() {
        Intent intent = new Intent(this, SettingsActivity.class);
        startActivity(intent);
    }

    @Override
    public void onMaintenanceClicked() {

        if (isServiceRunning(DiscoverPeersOnBackgroundService.class)) {
            stopService(discoverPeersOnBackgroundIntent);
        }

        Intent intent = new Intent(this, MaintenanceActivity.class);
        startActivity(intent);
    }

    private String convertToJsonString(List<SalesProduct> salesProducts, List<Reward> rewards) throws JSONException {

        JSONObject salesProductsJsonObject;
        JSONObject rewardsJsonObject;

        JSONArray salesProductsJsonArray = new JSONArray();
        JSONArray rewardsJsonArray = new JSONArray();

        JSONObject resultJsonObject = new JSONObject();

        for (SalesProduct salesProduct : salesProducts) {

            salesProductsJsonObject = new JSONObject();

            salesProductsJsonObject.put(
                    SalesProductDao.Properties.Id.columnName, salesProduct.getId()
            );
            salesProductsJsonObject.put(
                    SalesProductDao.Properties.Sales_transaction_number.columnName, salesProduct.getSales_transaction_number()
            );
            salesProductsJsonObject.put(
                    SalesProductDao.Properties.Product_id.columnName, salesProduct.getProduct_id()
            );
            salesProductsJsonObject.put(
                    SalesProductDao.Properties.Quantity.columnName, salesProduct.getQuantity()
            );
            salesProductsJsonObject.put(
                    SalesProductDao.Properties.Sub_total.columnName, salesProduct.getSub_total()
            );
            salesProductsJsonObject.put(
                    SalesProductDao.Properties.Sale_type.columnName, salesProduct.getSale_type()
            );

            salesProductsJsonArray.put(salesProductsJsonObject);

        }

        resultJsonObject.put(SalesProduct.class.getSimpleName(), salesProductsJsonArray);

        for (Reward reward : rewards) {

            rewardsJsonObject = new JSONObject();

            rewardsJsonObject.put(
                    RewardDao.Properties.Id.columnName, reward.getId()
            );
            rewardsJsonObject.put(
                    RewardDao.Properties.Reward_condition.columnName, reward.getReward_condition()
            );
            rewardsJsonObject.put(
                    RewardDao.Properties.Condition_product_id.columnName, reward.getCondition_product_id()
            );
            rewardsJsonObject.put(
                    RewardDao.Properties.Condition.columnName, reward.getCondition()
            );
            rewardsJsonObject.put(
                    RewardDao.Properties.Condition_value.columnName, reward.getCondition_value()
            );
            rewardsJsonObject.put(
                    RewardDao.Properties.Reward_type.columnName, reward.getReward_type()
            );
            rewardsJsonObject.put(
                    RewardDao.Properties.Reward.columnName, reward.getReward()
            );
            rewardsJsonObject.put(
                    RewardDao.Properties.Reward_value.columnName, reward.getReward_value()
            );
            rewardsJsonObject.put(
                    RewardDao.Properties.Valid_from.columnName, reward.getValid_from()
            );
            rewardsJsonObject.put(
                    RewardDao.Properties.Valid_until.columnName, reward.getValid_until()
            );
            rewardsJsonObject.put(
                    RewardDao.Properties.Created_at.columnName, reward.getCreated_at()
            );
            rewardsJsonObject.put(
                    RewardDao.Properties.Updated_at.columnName, reward.getUpdated_at()
            );

            rewardsJsonArray.put(rewardsJsonObject);

        }

        resultJsonObject.put(Reward.class.getSimpleName(), rewardsJsonArray);

        return resultJsonObject.toString();

    }

    private void onAddSalesProduct(Product product, int quantity) {

        float totalAmount = mainViewFragment.getTotalAmount();

        float subTotal = product.getUnit_cost() * quantity;
        totalAmount += subTotal;

        int productIndexInList = productExistInList(product);

        if (productIndexInList == -1) {

            SalesProduct salesProduct = new SalesProduct();
            salesProduct.setProduct_id(product.getId());
            salesProduct.setQuantity(quantity);
            salesProduct.setSub_total(subTotal);
            salesProduct.setSale_type("SALE");

            salesProducts.add(salesProduct);

        } else {
            SalesProduct salesProduct = salesProducts.get(productIndexInList);
            salesProduct.setQuantity(quantity + salesProduct.getQuantity());
            salesProduct.setSub_total(subTotal + salesProduct.getSub_total());
        }

        mainViewFragment.setTotalAmount(totalAmount);

        salesProductWithReturnListAdapter.notifyDataSetChanged();

        /*String sql = " WHERE " + ProductDao.Properties.Name.columnName + "=?";

        List<Product> products = productDao.queryRaw(sql, new String[]{productSelected.getName()});

        for (Product product : products) {

            float subTotal = product.getUnit_cost() * quantity;
            totalAmount += subTotal;

            int productIndexInList = productExistInList(product);

            if (productIndexInList == -1) {

                SalesProduct salesProduct = new SalesProduct();
                salesProduct.setProduct_id(product.getId());
                salesProduct.setQuantity(quantity);
                salesProduct.setSub_total(subTotal);
                salesProduct.setSale_type("SALE");

                salesProducts.add(salesProduct);

            } else {
                SalesProduct salesProduct = salesProducts.get(productIndexInList);
                salesProduct.setQuantity(quantity + salesProduct.getQuantity());
                salesProduct.setSub_total(subTotal + salesProduct.getSub_total());
            }

            mainViewFragment.setTotalAmount(totalAmount);

            salesProductListAdapter.notifyDataSetChanged();

        }*/

    }

    private int productExistInList(Product product) {

        int index = -1;

        for (int i = 0; i < salesProducts.size(); i++) {
            if (salesProducts.get(i).getProduct_id() == product.getId()) {
                index = i;
            }
        }

        return index;

    }

    public void checkForRewards() {

        checkProductRewards();

        checkForPurchaseAmountRewards(mainViewFragment.getTotalAmount());

    }

    private void checkProductRewards() {

        for (int i = 0; i < salesProducts.size(); i++) {

            SalesProduct salesProduct = salesProducts.get(i);

            long productId = salesProduct.getProduct_id();

            Log.d(TAG, "PRODUCT ID : " + productId);

            List<Reward> allRewards = rewardDao.loadAll();

            for(Reward reward : allRewards){

                Log.d(TAG, "Condition_product_id :  " + reward.getCondition_product_id());

            }

            String sql = " WHERE " + RewardDao.Properties.Condition_product_id.columnName +
                    " = ?";

            List<Reward> rewardsForProductList =
                    rewardDao.queryRaw(sql, new String[]{productId + ""});

            Log.d(TAG,"FOUND REWARD SIZE : " + rewardsForProductList.size());

            for (Reward reward : rewardsForProductList) {

                //Log.d(TAG, reward.toString());

                if (isRewardValid(reward.getValid_from(), reward.getValid_until())) {

                    if (isBetweenCondition(
                            reward.getCondition(),
                            reward.getCondition_value(),
                            salesProduct.getQuantity())) {


                        //Log.d(TAG, "BETWEEN CONDITION :" + reward.getCondition().toUpperCase());

                        if (reward.getCondition().toUpperCase().equals("EQUAL TO") || reward.getCondition().equals("=")) {

                            for (int rewardCount = 1;
                                 rewardCount <= salesProduct.getQuantity() / reward.getCondition_value();
                                 rewardCount++) {

                                getReward(reward);
                                rewards.add(reward);

                            }

                        } else {
                            getReward(reward);
                            rewards.add(reward);
                        }


                    }

                }

            }

        }

    }

    private void checkForPurchaseAmountRewards(float totalPurchaseAmount) {

        Date date = new Date();
        String formattedDate = formatter.format(date);

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

                            getReward(reward);
                            rewards.add(reward);
                        }

                    } else {
                        getReward(reward);
                        rewards.add(reward);
                    }

                }
            }

        }

    }

    private boolean isRewardValid(Date validFrom, Date validUntil) {

        boolean isValid = false;

        Date dtValidFrom = java.sql.Date.valueOf(formatter.format(validFrom));
        Date dtValidUntil = java.sql.Date.valueOf(formatter.format(validUntil));
        Date currDate = java.sql.Date.valueOf(formatter.format(new Date()));

        if (currDate.compareTo(dtValidFrom) != -1 && currDate.compareTo(dtValidUntil) != 1) {
            isValid = true;
        }

        return isValid;

    }

    private void getReward(Reward reward) {

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

                    salesProducts.add(salesProductFreebie);
                }

                break;
            case "DISCOUNT":

                totalDiscount += Float.parseFloat(reward.getReward_value());

                break;

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

    private void broadcastAdvertisement() {

        Intent intent = new Intent(this, AdvertisementSenderService.class);
        startService(intent);

    }

    private boolean isServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

}
