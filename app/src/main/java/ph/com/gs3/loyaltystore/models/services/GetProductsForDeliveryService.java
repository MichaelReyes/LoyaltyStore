package ph.com.gs3.loyaltystore.models.services;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

import java.util.List;

import ph.com.gs3.loyaltystore.models.User;
import ph.com.gs3.loyaltystore.models.sqlite.dao.ProductForDelivery;
import ph.com.gs3.loyaltystore.models.synchronizer.ProductForDeliverySynchronizer;
import ph.com.gs3.loyaltystore.models.values.Retailer;

/**
 * Created by Ervinne Sodusta on 2/3/2016.
 */
public class GetProductsForDeliveryService extends IntentService {

    public static final String NAME = GetProductsForDeliveryService.class.getName();
    public static final String TAG = GetProductsForDeliveryService.class.getSimpleName();

    public static final String ACTION_NEED_AUTHENTICATION = "need_authentication";
    public static final String ACTION_DONE_PRODUCTS_FOR_DELIVERY_SYNC = "done_products_for_delivery_sync";
    public static final String ACTION_ERROR = "error";

    public static final String EXTRA_SYNC_COUNT = "sync_count";

    public GetProductsForDeliveryService() {
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

        Retailer retailer = Retailer.getDeviceRetailerFromSharedPreferences(this);

        List<ProductForDelivery> productsForDelivery = ProductForDeliverySynchronizer.sync(this, formalisticsServer, retailer);
        if (productsForDelivery != null) {
            broadcast(ACTION_DONE_PRODUCTS_FOR_DELIVERY_SYNC, productsForDelivery.size());
        } else {
            broadcast(ACTION_DONE_PRODUCTS_FOR_DELIVERY_SYNC, 0);
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
