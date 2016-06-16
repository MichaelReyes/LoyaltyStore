package ph.com.gs3.loyaltystore.models.services;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import java.io.IOException;
import java.util.List;

import okhttp3.logging.HttpLoggingInterceptor;
import ph.com.gs3.loyaltystore.models.User;
import ph.com.gs3.loyaltystore.models.api.ServiceGenerator;
import ph.com.gs3.loyaltystore.models.api.UsersAPI;
import ph.com.gs3.loyaltystore.models.api.objects.FormalisticsLoginResponse;
import ph.com.gs3.loyaltystore.models.api.objects.FormalisticsUser;
import ph.com.gs3.loyaltystore.models.api.objects.ReturnsUploadRequest;
import ph.com.gs3.loyaltystore.models.sqlite.dao.Expenses;
import ph.com.gs3.loyaltystore.models.sqlite.dao.ItemInventory;
import ph.com.gs3.loyaltystore.models.sqlite.dao.ItemStockCount;
import ph.com.gs3.loyaltystore.models.sqlite.dao.Product;
import ph.com.gs3.loyaltystore.models.sqlite.dao.ProductBreakdown;
import ph.com.gs3.loyaltystore.models.sqlite.dao.Reward;
import ph.com.gs3.loyaltystore.models.sqlite.dao.Sales;
import ph.com.gs3.loyaltystore.models.synchronizer.ExpensesSynchronizer;
import ph.com.gs3.loyaltystore.models.synchronizer.InventorySynchronizer;
import ph.com.gs3.loyaltystore.models.synchronizer.ItemStockCountSynchronizer;
import ph.com.gs3.loyaltystore.models.synchronizer.ProductBreakdownSynchronizer;
import ph.com.gs3.loyaltystore.models.synchronizer.ProductSynchronizer;
import ph.com.gs3.loyaltystore.models.synchronizer.ReturnsSynchronizer;
import ph.com.gs3.loyaltystore.models.synchronizer.RewardsSynchronizer;
import ph.com.gs3.loyaltystore.models.synchronizer.SalesSynchronizer;
import ph.com.gs3.loyaltystore.models.values.Retailer;
import retrofit2.Call;

/**
 * Created by Ervinne Sodusta on 2/3/2016.
 */
public class ServerSyncService extends IntentService {

    public static final String NAME = ServerSyncService.class.getName();
    public static final String TAG = ServerSyncService.class.getSimpleName();

    public static final String ACTION_NEED_AUTHENTICATION = "need_authentication";
    public static final String ACTION_DONE_PRODUCTS_SYNC = "done_products_sync";
    public static final String ACTION_DONE_PRODUCTS_BREAKDOWN_SYNC = "done_products_breakdown_sync";
    public static final String ACTION_DONE_REWARDS_SYNC = "done_rewards_sync";
    public static final String ACTION_DONE_SALES_SYNC = "done_sales_sync";
    public static final String ACTION_DONE_RETURNS_SYNC = "done_returns_sync";
    public static final String ACTION_DONE_EXPENSES_SYNC = "done_expenses_sync";
    public static final String ACTION_DONE_INVENTORY_SYNC = "done_inventory_sync";
    public static final String ACTION_DONE_ITEM_STOCK_COUNT_SYNC = " done_item_stock_count_sync";
    public static final String ACTION_DONE_PRODUCTS_FOR_DELIVERY_SYNC = "done_products_for_delivery_sync";
    public static final String ACTION_ERROR = "error";

    public static final String EXTRA_SYNC_COUNT = "sync_count";

    public ServerSyncService() {
        super(NAME);
        Log.v(TAG, NAME);
    }

    @Override
    protected void onHandleIntent(Intent intent) {

        //  log user in
        User currentUser = User.getSavedUser(this);
        String server = currentUser.getServer();
        String formalisticsServer = currentUser.getFormalisticsServer();

        Log.d(TAG, "==============================");

        Log.d(TAG, "User :" + currentUser.getName());
        Log.d(TAG, "Server :" + currentUser.getServer());
        Log.d(TAG, "Formalistics Server :" + currentUser.getFormalisticsServer());

        Log.d(TAG, "==============================");

          if (!formalisticsLogin(currentUser, this)) {
             broadcast(ACTION_NEED_AUTHENTICATION, 0);
            return;
        }

        /*if (!login(currentUser, this)) {
            broadcast(ACTION_NEED_AUTHENTICATION, 0);
            return;
        }*/

        Retailer retailer = Retailer.getDeviceRetailerFromSharedPreferences(this);

        List<Product> productList = ProductSynchronizer.sync(this, formalisticsServer);

        if (productList != null) {
            broadcast(ACTION_DONE_PRODUCTS_SYNC, productList.size());
        } else {
            broadcast(ACTION_DONE_PRODUCTS_SYNC, 0);
        }

        List<ProductBreakdown> productBreakdownList = ProductBreakdownSynchronizer.sync(this, formalisticsServer);

        if (productBreakdownList != null) {
            broadcast(ACTION_DONE_PRODUCTS_BREAKDOWN_SYNC, productBreakdownList.size());
        } else {
            broadcast(ACTION_DONE_PRODUCTS_BREAKDOWN_SYNC, 0);
        }

        List<Reward> rewards = RewardsSynchronizer.sync(this, formalisticsServer);
        if (rewards != null) {
            broadcast(ACTION_DONE_REWARDS_SYNC, rewards.size());
        } else {
            broadcast(ACTION_DONE_REWARDS_SYNC, 0);
        }

        List<Sales> salesList = SalesSynchronizer.sync(this, server, formalisticsServer);
        if (salesList != null) {
            broadcast(ACTION_DONE_SALES_SYNC, salesList.size());
        } else {
            broadcast(ACTION_DONE_SALES_SYNC, 0);
        }

        ReturnsUploadRequest returnsUploadRequest = ReturnsSynchronizer.sync(this,server,formalisticsServer);

        if(returnsUploadRequest != null){
            broadcast(ACTION_DONE_RETURNS_SYNC,
                    returnsUploadRequest.itemReturns.size() + returnsUploadRequest.cashReturns.size());
        }else{
            broadcast(ACTION_DONE_RETURNS_SYNC,0);
        }

        List<ItemInventory> itemInventoryList =
                InventorySynchronizer.sync(this,server,formalisticsServer);

        if(itemInventoryList != null){
            broadcast(ACTION_DONE_INVENTORY_SYNC, itemInventoryList.size());
        }else{
            broadcast(ACTION_DONE_INVENTORY_SYNC,0);
        }

        List<Expenses> expensesList
                = ExpensesSynchronizer.sync(this,formalisticsServer);

        if(expensesList != null){
            broadcast(ACTION_DONE_EXPENSES_SYNC,expensesList.size());
        }else{
            broadcast(ACTION_DONE_EXPENSES_SYNC,0);
        }

        List<ItemStockCount> itemStockCountList
                = ItemStockCountSynchronizer.sync(this,formalisticsServer);

        if(itemStockCountList != null){
            broadcast(ACTION_DONE_ITEM_STOCK_COUNT_SYNC, itemStockCountList.size());
        }else{
            broadcast(ACTION_DONE_ITEM_STOCK_COUNT_SYNC, 0);
        }

        /*List<ProductForDelivery> productsForDelivery = ProductForDeliverySynchronizer.sync(this, formalisticsServer, retailer);
        if (productsForDelivery != null) {
            broadcast(ACTION_DONE_PRODUCTS_FOR_DELIVERY_SYNC, productsForDelivery.size());
        } else {
            broadcast(ACTION_DONE_PRODUCTS_FOR_DELIVERY_SYNC, 0);
        }*/


    }

    public static boolean formalisticsLogin(User currentUser, Context context) {

        String formalisticsServer = currentUser.getFormalisticsServer();
        String email = currentUser.getEmail();
        String password = currentUser.getPassword();

        Log.v(TAG, formalisticsServer + " " + email + " " + password);

        if (formalisticsServer == null || email == null || password == null) {
            return false;
        }

        if (password == null || password == ""){
            password = "password";
        }

        ServiceGenerator serviceGenerator = new ServiceGenerator(context, formalisticsServer, HttpLoggingInterceptor.Level.BODY);
        UsersAPI usersAPI = serviceGenerator.createService(UsersAPI.class);
        Call<FormalisticsLoginResponse> call = usersAPI.formalisticsLogin(email, password);
        try {
            retrofit2.Response response = call.execute();

            if (response != null) {

                Log.d(TAG, "Login response");

                FormalisticsLoginResponse loginResponse = (FormalisticsLoginResponse) response.body();
                if (loginResponse == null) {
                    return false;
                }

                FormalisticsUser formalisticsUser = loginResponse.results;
                User loggedInUser = new User();

                if (formalisticsUser != null) {
                    /*
                    loggedInUser.setEmail(formalisticsUser.email);
                    loggedInUser.setFormalisticsServer(formalisticsServer);
                    loggedInUser.setPassword(password);
                    loggedInUser.save(context);
                    */

                    loggedInUser.setId((int) formalisticsUser.id);
                    loggedInUser.setEmail(formalisticsUser.email);
                    loggedInUser.setName(formalisticsUser.display_name);
                    loggedInUser.setFormalisticsServer(formalisticsServer);
                    loggedInUser.setPassword(password);
                    loggedInUser.setServer(formalisticsServer);
                    loggedInUser.save(context);

                    Log.v(TAG, loginResponse.toString());
                    Log.v(TAG, loggedInUser.toString());

                    return true;
                }

            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return false;
    }

    private void broadcast(String action, int syncCount) {
        Intent broadcastIntent = new Intent();
        broadcastIntent.setAction(action);
        broadcastIntent.addCategory(Intent.CATEGORY_DEFAULT);
        broadcastIntent.putExtra(EXTRA_SYNC_COUNT, syncCount);
        sendBroadcast(broadcastIntent);
    }

}
