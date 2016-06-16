package ph.com.gs3.loyaltystore.models.synchronizer;

import android.content.Context;
import android.util.Log;

import java.io.IOException;
import java.util.List;

import okhttp3.logging.HttpLoggingInterceptor;
import ph.com.gs3.loyaltystore.LoyaltyStoreApplication;
import ph.com.gs3.loyaltystore.models.api.ItemStockCountAPI;
import ph.com.gs3.loyaltystore.models.api.ServiceGenerator;
import ph.com.gs3.loyaltystore.models.api.objects.UploadAPIResponse;
import ph.com.gs3.loyaltystore.models.sqlite.dao.ItemStockCount;
import ph.com.gs3.loyaltystore.models.sqlite.dao.ItemStockCountDao;
import retrofit2.Call;
import retrofit2.Response;

/**
 * Created by Michael Reyes on 2/15/2016.
 */
public class ItemStockCountSynchronizer {

    public static final String TAG = ItemStockCountSynchronizer.class.getSimpleName();

    public static List<ItemStockCount> sync(Context context, String formalisticsServer) {

        ItemStockCountDao itemStockCountDao
                = LoyaltyStoreApplication.getSession().getItemStockCountDao();


        List<ItemStockCount> itemStockCountList
                = itemStockCountDao
                    .queryBuilder()
                    .where(
                            ItemStockCountDao.Properties.Is_synced.eq(false)
                    ).list();


        ServiceGenerator serviceGeneratorFormalistics = new ServiceGenerator(context, formalisticsServer, HttpLoggingInterceptor.Level.BODY);
        ItemStockCountAPI itemStockCountAPI = serviceGeneratorFormalistics.createService(ItemStockCountAPI.class);


        Call<UploadAPIResponse> uploadRequestCall = itemStockCountAPI.uploadItemStockCountToFormalistics(itemStockCountList);

        try {
            Response<UploadAPIResponse> apiResponse = uploadRequestCall.execute();
            UploadAPIResponse uploadAPIResponse = apiResponse.body();

            if (uploadAPIResponse != null && uploadAPIResponse.status != null) {
                Log.e(TAG, uploadAPIResponse.status);

                if ("SUCCESS".equals(uploadAPIResponse.status)) {

                    for(ItemStockCount itemStockCount : itemStockCountList){

                        itemStockCount.setIs_synced(true);
                        itemStockCountDao.insertOrReplace(itemStockCount);

                    }

                    return itemStockCountList;
                } else {
                    if (uploadAPIResponse.error != null) {
                        Log.e(TAG, uploadAPIResponse.error);
                    } else {
                        Log.e(TAG, "Has error but cannot parse it.");
                    }
                }

            } else {
                Log.e(TAG, "Response has no status!");

                if (uploadAPIResponse.error_message != null) {
                    Log.e(TAG, uploadAPIResponse.error_message);
                }

            }

        } catch (IOException e) {
            e.printStackTrace();
        }


        return null;

    }

}
