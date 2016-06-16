package ph.com.gs3.loyaltystore.models.services;

import android.app.IntentService;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import java.io.IOException;
import java.util.List;

import okhttp3.logging.HttpLoggingInterceptor;
import ph.com.gs3.loyaltystore.LoyaltyStoreApplication;
import ph.com.gs3.loyaltystore.models.api.ServiceGenerator;
import ph.com.gs3.loyaltystore.models.api.StoreAPI;
import ph.com.gs3.loyaltystore.models.api.objects.FormalisticsAPIResponse;
import ph.com.gs3.loyaltystore.models.sqlite.dao.Store;
import ph.com.gs3.loyaltystore.models.sqlite.dao.StoreDao;
import ph.com.gs3.loyaltystore.models.values.Retailer;
import retrofit2.Call;
import retrofit2.Response;

/**
 * Created by Bryan-PC on 05/05/2016.
 */
public class RegisterStoreOnWebService extends IntentService {

    public static final String NAME = RegisterStoreOnWebService.class.getName();
    public static final String TAG = RegisterStoreOnWebService.class.getSimpleName();

    public static final String ACTION_DONE_REGISTER_STORE = "done_register_store";
    public static final String ACTION_ERROR_REGISTER_STORE = "error_register_store";

    public static final String EXTRA_STORE_NAME = "store_name";
    public static final String EXTRA_SYNC_COUNT = "sync_count";

    public RegisterStoreOnWebService() {
        super(NAME);
        Log.v(TAG, NAME);
    }

    @Override
    protected void onHandleIntent(Intent intent) {

        Log.d(TAG, " RegisterStoreOnWebService ");

        Bundle bundle = intent.getExtras();

        String storeName = bundle.getString(EXTRA_STORE_NAME);

        StoreDao storeDao = LoyaltyStoreApplication.getSession().getStoreDao();

        List<Store> storeList =
                storeDao.queryBuilder().where(StoreDao.Properties.Name.eq(storeName)).list();

        for (Store store : storeList) {

            Retailer retailer = Retailer.getDeviceRetailerFromSharedPreferences(this);

            Log.d(TAG, " URL : " + retailer.getServerUrl());

            ServiceGenerator serviceGenerator = new ServiceGenerator(
                    this, retailer.getServerUrl(), HttpLoggingInterceptor.Level.BODY);
            StoreAPI storeAPI = serviceGenerator.createService(StoreAPI.class);

            Call<FormalisticsAPIResponse> call = storeAPI.registerStoreDevice(
                    Long.toString(store.getId()), retailer.getDeviceId()
            );

            try {

                Response<FormalisticsAPIResponse> response = call.execute();
                FormalisticsAPIResponse formalisticsAPIResponse = response.body();

                if (formalisticsAPIResponse.error == null) {

                    broadcast(ACTION_DONE_REGISTER_STORE, 0);

                    Log.d(TAG, " Before save : " + retailer.getStoreId() + " ~ " + store.getId());

                    retailer.setStoreId(store.getId());
                    retailer.setStoreName(storeName);
                    retailer.save(this);

                    Log.d(TAG, " After save : " + retailer.getStoreId());


                    store.setDevice_web_id(store.getId());
                    storeDao.update(store);

                    Log.d(TAG, "========== REGISTER STORE RESPONSE START ==========");

                    Log.d(TAG, "Status : " + formalisticsAPIResponse.status);
                    Log.d(TAG, "error : " + formalisticsAPIResponse.error);
                    Log.d(TAG, "error_message : " + formalisticsAPIResponse.error_message);
                    Log.d(TAG, "result : " + formalisticsAPIResponse.results);

                    Log.d(TAG, "========== REGISTER STORE RESPONSE END ==========");

                } else {
                    broadcast(ACTION_ERROR_REGISTER_STORE, 0);
                }


            } catch (IOException e) {
                e.printStackTrace();
            }


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
