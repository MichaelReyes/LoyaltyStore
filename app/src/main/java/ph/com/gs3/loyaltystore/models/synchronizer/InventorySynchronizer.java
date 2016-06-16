package ph.com.gs3.loyaltystore.models.synchronizer;

import android.content.Context;
import android.util.Log;

import com.google.gson.Gson;

import java.io.IOException;
import java.util.List;

import okhttp3.logging.HttpLoggingInterceptor;
import ph.com.gs3.loyaltystore.LoyaltyStoreApplication;
import ph.com.gs3.loyaltystore.models.api.InventoryAPI;
import ph.com.gs3.loyaltystore.models.api.ServiceGenerator;
import ph.com.gs3.loyaltystore.models.api.objects.FormalisticsAPIResponse;
import ph.com.gs3.loyaltystore.models.sqlite.dao.ItemInventory;
import ph.com.gs3.loyaltystore.models.sqlite.dao.ItemInventoryDao;
import retrofit2.Call;
import retrofit2.Response;

/**
 * Created by Ervinne Sodusta on 2/15/2016.
 */
public class InventorySynchronizer {

    public static final String TAG = InventorySynchronizer.class.getSimpleName();

    public static List<ItemInventory> sync(Context context, String server, String formalisticsServer) {

        ItemInventoryDao itemInventoryDao
                = LoyaltyStoreApplication.getNewSession().getItemInventoryDao();

        List<ItemInventory> itemInventoryList
                = itemInventoryDao
                    .queryBuilder()
                    .where(
                            ItemInventoryDao.Properties.Is_updated.eq(true)
                    ).list();

        ServiceGenerator serviceGeneratorFormalistics = new ServiceGenerator(context, formalisticsServer, HttpLoggingInterceptor.Level.BODY);
        InventoryAPI inventoryAPIFormalistics = serviceGeneratorFormalistics.createService(InventoryAPI.class);

        Call<FormalisticsAPIResponse> inventoryUploadRequestCall = inventoryAPIFormalistics.updateInventoryInFormalistics(itemInventoryList);

        try {
            Response<FormalisticsAPIResponse> apiResponse = inventoryUploadRequestCall.execute();
            FormalisticsAPIResponse formalisticsAPIResponse = apiResponse.body();

            if (formalisticsAPIResponse != null && formalisticsAPIResponse.status != null) {
                Log.e(TAG, formalisticsAPIResponse.status);

                if ("SUCCESS".equals(formalisticsAPIResponse.status)) {

                    // synced Inventory
                    Log.d(TAG, "====================== INVENTORY START ======================");

                    for (ItemInventory itemInventory : itemInventoryList) {

                        Log.d(TAG, "--------------------------------------------------------");

                        Log.d(TAG, "ID : " + itemInventory.getId());
                        Log.d(TAG, "PRODUCT_ID : " + itemInventory.getProduct_id());
                        Log.d(TAG, "STORE_ID : " + itemInventory.getStore_id());
                        Log.d(TAG, "NAME : " + itemInventory.getName());
                        Log.d(TAG, "QUANTITY : " + itemInventory.getQuantity());

                        Log.d(TAG, "--------------------------------------------------------");

                        itemInventory.setIs_updated(false);
                        itemInventoryDao.insertOrReplace(itemInventory);

                    }

                    Log.d(TAG, "====================== INVENTORY END ======================");

                    return itemInventoryList;
                } else {
                    if (formalisticsAPIResponse.error != null) {
                        Log.e(TAG, formalisticsAPIResponse.error);
                    } else {
                        Log.e(TAG, "Has error but cannot parse it.");
                    }
                }

            } else {
                Log.e(TAG, "Response has no status!");

                if (formalisticsAPIResponse.error_message != null) {
                    Log.e(TAG, formalisticsAPIResponse.error_message);
                }

            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        /*if(itemInventoryList.size() > 10) {

            int recordFrom = 0;
            int recordTo = 10;

            while (recordTo < itemInventoryList.size()){
                List<ItemInventory> inventoryList
                        = itemInventoryDao
                            .queryBuilder()
                            .where(
                                    ItemInventoryDao.Properties.Product_id.isNotNull()
                            ).limit(recordTo).offset(recordFrom).list();

                startSync(inventoryAPIFormalistics,inventoryList);

                if((recordTo + 10) > itemInventoryList.size()){
                    recordTo = itemInventoryList.size();
                }else{
                    recordTo+=10;
                }

                recordFrom +=10;
            }

        }else{
          return startSync(inventoryAPIFormalistics,itemInventoryList);
        }*/
        return null;

    }

    private static List<ItemInventory> startSync(InventoryAPI inventoryAPIFormalistics, List<ItemInventory> itemInventoryList){

        Call<FormalisticsAPIResponse> inventoryUploadRequestCall = inventoryAPIFormalistics.updateInventoryInFormalistics(itemInventoryList);

        Gson gson = new Gson();
        Log.d("MIKE", gson.toJson(itemInventoryList));

        try {
            Response<FormalisticsAPIResponse> apiResponse = inventoryUploadRequestCall.execute();
            FormalisticsAPIResponse formalisticsAPIResponse = apiResponse.body();

            if (formalisticsAPIResponse != null && formalisticsAPIResponse.status != null) {
                Log.e(TAG, formalisticsAPIResponse.status);

                if ("SUCCESS".equals(formalisticsAPIResponse.status)) {

                    // synced Inventory
                    Log.d(TAG, "====================== INVENTORY START ======================");

                    for (ItemInventory itemInventory : itemInventoryList) {

                        Log.d(TAG, "--------------------------------------------------------");

                        Log.d(TAG, "ID : " + itemInventory.getId());
                        Log.d(TAG, "PRODUCT_ID : " + itemInventory.getProduct_id());
                        Log.d(TAG, "STORE_ID : " + itemInventory.getStore_id());
                        Log.d(TAG, "NAME : " + itemInventory.getName());
                        Log.d(TAG, "QUANTITY : " + itemInventory.getQuantity());

                        Log.d(TAG, "--------------------------------------------------------");

                    }

                    Log.d(TAG, "====================== INVENTORY END ======================");

                    return itemInventoryList;
                } else {
                    if (formalisticsAPIResponse.error != null) {
                        Log.e(TAG, formalisticsAPIResponse.error);
                    } else {
                        Log.e(TAG, "Has error but cannot parse it.");
                    }
                }

            } else {
                Log.e(TAG, "Response has no status!");

                if (formalisticsAPIResponse.error_message != null) {
                    Log.e(TAG, formalisticsAPIResponse.error_message);
                }

            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }


}
