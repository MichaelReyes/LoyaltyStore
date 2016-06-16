package ph.com.gs3.loyaltystore.models.services;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

import java.io.IOException;
import java.util.List;

import okhttp3.logging.HttpLoggingInterceptor;
import ph.com.gs3.loyaltystore.LoyaltyStoreApplication;
import ph.com.gs3.loyaltystore.models.api.ServiceGenerator;
import ph.com.gs3.loyaltystore.models.api.StoreAPI;
import ph.com.gs3.loyaltystore.models.sqlite.dao.Store;
import ph.com.gs3.loyaltystore.models.sqlite.dao.StoreDao;
import ph.com.gs3.loyaltystore.models.values.Retailer;
import retrofit2.Call;
import retrofit2.Response;

/**
 * Created by Bryan-PC on 05/05/2016.
 */
public class GetAvailableStoresOnWebService extends IntentService {

    public static final String NAME = GetAvailableStoresOnWebService.class.getName();
    public static final String TAG = GetAvailableStoresOnWebService.class.getSimpleName();

    public static final String ACTION_DONE_GET_AVAILABLE_STORES = "done_get_available_stores";

    public static final String EXTRA_SYNC_COUNT = "sync_count";

    public GetAvailableStoresOnWebService() {
        super(NAME);
        Log.v(TAG, NAME);
    }

    @Override
    protected void onHandleIntent(Intent intent) {

        Retailer retailer = Retailer.getDeviceRetailerFromSharedPreferences(this);

        Log.d(TAG, " URL : " + retailer.getServerUrl());

        ServiceGenerator serviceGenerator = new ServiceGenerator(
                this, retailer.getServerUrl(), HttpLoggingInterceptor.Level.BODY);
        StoreAPI storeAPI = serviceGenerator.createService(StoreAPI.class);

        Call<List<Store>> call = storeAPI.getAvailableStoresForRegistration();

        try {
            Response<List<Store>> response = call.execute();
            List<Store> storeList = response.body();

            StoreDao storeDao = LoyaltyStoreApplication.getSession().getStoreDao();

            storeDao.insertOrReplaceInTx(storeList);

            Log.d(TAG, "Store List Response Body Size : " + storeList.size());

            if (storeList != null)
                broadcast(ACTION_DONE_GET_AVAILABLE_STORES, storeList.size());
            else
                broadcast(ACTION_DONE_GET_AVAILABLE_STORES,0);


        } catch (IOException e) {
            e.printStackTrace();
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
