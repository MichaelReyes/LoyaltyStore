package ph.com.gs3.loyaltystore.models.services;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

import java.util.List;

import ph.com.gs3.loyaltystore.models.User;
import ph.com.gs3.loyaltystore.models.api.objects.ReturnsUploadRequest;
import ph.com.gs3.loyaltystore.models.sqlite.dao.ExpenseType;
import ph.com.gs3.loyaltystore.models.sqlite.dao.Expenses;
import ph.com.gs3.loyaltystore.models.sqlite.dao.ItemInventory;
import ph.com.gs3.loyaltystore.models.sqlite.dao.ItemStockCount;
import ph.com.gs3.loyaltystore.models.sqlite.dao.Sales;
import ph.com.gs3.loyaltystore.models.synchronizer.ExpenseTypeSynchronizer;
import ph.com.gs3.loyaltystore.models.synchronizer.ExpensesSynchronizer;
import ph.com.gs3.loyaltystore.models.synchronizer.InventorySynchronizer;
import ph.com.gs3.loyaltystore.models.synchronizer.ItemStockCountSynchronizer;
import ph.com.gs3.loyaltystore.models.synchronizer.ReturnsSynchronizer;
import ph.com.gs3.loyaltystore.models.synchronizer.SalesSynchronizer;

/**
 * Created by Ervinne Sodusta on 2/3/2016.
 */
public class UpdateInventoryAndSyncOtherDataService extends IntentService {

    public static final String NAME = UpdateInventoryAndSyncOtherDataService.class.getName();
    public static final String TAG = UpdateInventoryAndSyncOtherDataService.class.getSimpleName();

    public static final String ACTION_DONE_RETURNS_SYNC = "done_returns_sync";
    public static final String ACTION_DONE_EXPENSES_SYNC = "done_expenses_sync";
    public static final String ACTION_DONE_INVENTORY_SYNC = "done_inventory_sync";
    public static final String ACTION_DONE_ITEM_STOCK_COUNT_SYNC = " done_item_stock_count_sync";
    public static final String ACTION_DONE_SALES_SYNC = "done_sales_sync";
    public static final String ACTION_NEED_AUTHENTICATION = "need_authentication";
    public static final String ACTION_DONE_EXPENSE_TYPE_SYNC = "done_expense_type_sync";
    public static final String ACTION_ERROR = "error";

    public static final String EXTRA_SYNC_COUNT = "sync_count";

    public UpdateInventoryAndSyncOtherDataService() {
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

        if (!ServerSyncService.formalisticsLogin(currentUser, this)) {
            broadcast(ACTION_NEED_AUTHENTICATION, 0);
            return;
        }

        List<Sales> salesList = SalesSynchronizer.sync(this, server, formalisticsServer);
        if (salesList != null) {
            broadcast(ACTION_DONE_SALES_SYNC, salesList.size());
        } else {
            broadcast(ACTION_DONE_SALES_SYNC, 0);
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

        ReturnsUploadRequest returnsUploadRequest = ReturnsSynchronizer.sync(this,server,formalisticsServer);

        if(returnsUploadRequest != null){
            broadcast(ACTION_DONE_RETURNS_SYNC,
                    returnsUploadRequest.itemReturns.size() + returnsUploadRequest.cashReturns.size());
        }else{
            broadcast(ACTION_DONE_RETURNS_SYNC,0);
        }

        List<ExpenseType> expenseTypeList = ExpenseTypeSynchronizer.sync(this,formalisticsServer);
        if(expenseTypeList != null){
            broadcast(ACTION_DONE_EXPENSE_TYPE_SYNC, expenseTypeList.size());
        }else{
            broadcast(ACTION_DONE_EXPENSE_TYPE_SYNC, 0);
        }

    }

    private void broadcast(String action, int syncCount) {
        Intent broadcastIntent = new Intent();
        broadcastIntent.setAction(action);
        broadcastIntent.addCategory(Intent.CATEGORY_DEFAULT);
        broadcastIntent.putExtra(EXTRA_SYNC_COUNT, syncCount);
        sendBroadcast(broadcastIntent);
    }

}
