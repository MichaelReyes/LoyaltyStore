package ph.com.gs3.loyaltystore.models.tasks;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.google.gson.Gson;

import java.io.IOException;
import java.util.List;

import okhttp3.logging.HttpLoggingInterceptor;
import ph.com.gs3.loyaltystore.LoyaltyStoreApplication;
import ph.com.gs3.loyaltystore.models.User;
import ph.com.gs3.loyaltystore.models.api.InventoryAPI;
import ph.com.gs3.loyaltystore.models.api.ServiceGenerator;
import ph.com.gs3.loyaltystore.models.api.objects.SyncInventoryFromWebResponse;
import ph.com.gs3.loyaltystore.models.services.ServerSyncService;
import ph.com.gs3.loyaltystore.models.sqlite.dao.ItemInventory;
import ph.com.gs3.loyaltystore.models.sqlite.dao.ItemInventoryDao;
import ph.com.gs3.loyaltystore.models.values.Retailer;
import retrofit2.Call;
import retrofit2.Response;

/**
 * Created by Ervinne Sodusta on 8/18/2015.
 */
public class SyncInventoryFromWebTask extends AsyncTask<Void, Void, Void> {

    public static final String TAG = SyncInventoryFromWebTask.class.getSimpleName();
    private SyncInventoryFromWebTaskEventListener listener;

    private Context context;

    public SyncInventoryFromWebTask(Context context, SyncInventoryFromWebTaskEventListener listener) {
        this.context = context;
        this.listener = listener;
    }

    @Override
    protected Void doInBackground(Void... params) {

        Log.d(TAG, "Do in background");

        User currentUser = User.getSavedUser(context);
        Retailer retailer = Retailer.getDeviceRetailerFromSharedPreferences(context);

        if (!ServerSyncService.formalisticsLogin(currentUser, context) || retailer.getStoreId() == -1) {
            Log.d(TAG, "Needs Authentication");
            listener.onNeedsAuthentication();
            return null;
        }

        ServiceGenerator serviceGenerator = new ServiceGenerator(context, currentUser.getFormalisticsServer(), HttpLoggingInterceptor.Level.BODY);
        InventoryAPI inventoryAPI = serviceGenerator.createService(InventoryAPI.class);

        Gson gson = new Gson();

        //Log.e(TAG, "store ID : " + retailer.getStoreId());

        Call<List<SyncInventoryFromWebResponse>> responseBodyCall =
                inventoryAPI.syncInventoryFromWeb(retailer.getStoreId());

        try {
            Response<List<SyncInventoryFromWebResponse>> response = responseBodyCall.execute();
            List<SyncInventoryFromWebResponse> syncInventoryFromWebResponseList = response.body();

            ItemInventoryDao itemInventoryDao = LoyaltyStoreApplication.getSession().getItemInventoryDao();

            for(SyncInventoryFromWebResponse data : syncInventoryFromWebResponseList){

                List<ItemInventory> itemInventoryList
                        = itemInventoryDao
                            .queryBuilder()
                            .where(ItemInventoryDao.Properties.Product_id.eq(data.txt_id)).list();

                for(ItemInventory itemInventory : itemInventoryList){

                    itemInventory.setQuantity(data.txt_stock);
                    itemInventoryDao.insertOrReplace(itemInventory);

                }

            }


        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        listener.onSyncInventoryFromWebDone();
    }

    public interface SyncInventoryFromWebTaskEventListener {

        void onSyncInventoryFromWebDone();

        void onNeedsAuthentication();

    }

}
