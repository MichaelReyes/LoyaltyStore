package ph.com.gs3.loyaltystore.models.synchronizer;

import android.content.Context;
import android.util.Log;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.logging.HttpLoggingInterceptor;
import ph.com.gs3.loyaltystore.LoyaltyStoreApplication;
import ph.com.gs3.loyaltystore.models.api.ReturnsAPI;
import ph.com.gs3.loyaltystore.models.api.ServiceGenerator;
import ph.com.gs3.loyaltystore.models.api.objects.ReturnsUploadRequest;
import ph.com.gs3.loyaltystore.models.api.objects.SyncReturnsAPIResponse;
import ph.com.gs3.loyaltystore.models.sqlite.dao.CashReturn;
import ph.com.gs3.loyaltystore.models.sqlite.dao.CashReturnDao;
import ph.com.gs3.loyaltystore.models.sqlite.dao.ItemReturn;
import ph.com.gs3.loyaltystore.models.sqlite.dao.ItemReturnDao;
import retrofit2.Call;
import retrofit2.Response;

/**
 * Created by Ervinne Sodusta on 2/15/2016.
 */
public class ReturnsSynchronizer {

    public static final String TAG = ReturnsSynchronizer.class.getSimpleName();

    public static ReturnsUploadRequest sync(Context context, String server, String formalisticsServer) {

        ItemReturnDao itemReturnDao
                = LoyaltyStoreApplication.getSession().getItemReturnDao();
        CashReturnDao cashReturnDao
                = LoyaltyStoreApplication.getSession().getCashReturnDao();

        ReturnsUploadRequest returnsUploadRequest = new ReturnsUploadRequest();

        List<ItemReturn> itemReturnList
                = itemReturnDao.queryBuilder().where(ItemReturnDao.Properties.Is_synced.eq(false)).list();

        List<CashReturn> cashReturnList
                = cashReturnDao.queryBuilder().where(CashReturnDao.Properties.Is_synced.eq(false)).list();

        if (itemReturnList != null)
            returnsUploadRequest.itemReturns = new ArrayList<>();
            returnsUploadRequest.itemReturns.addAll(itemReturnList);
        if (cashReturnList != null)
            returnsUploadRequest.cashReturns = new ArrayList<>();
            returnsUploadRequest.cashReturns.addAll(cashReturnList);

        if(returnsUploadRequest.itemReturns != null && returnsUploadRequest != null) {

            ServiceGenerator serviceGeneratorFormalistics = new ServiceGenerator(context, formalisticsServer, HttpLoggingInterceptor.Level.BODY);
            ReturnsAPI returnsAPI = serviceGeneratorFormalistics.createService(ReturnsAPI.class);

            Call<SyncReturnsAPIResponse> returnsUploadRequestCall = returnsAPI.uploadReturnsToFormalistics(returnsUploadRequest);

            try {
                Response<SyncReturnsAPIResponse> apiResponse = returnsUploadRequestCall.execute();
                SyncReturnsAPIResponse returnsUploadFormalisticsAPIResponse = apiResponse.body();

                if (returnsUploadFormalisticsAPIResponse != null && returnsUploadFormalisticsAPIResponse.status != null) {
                    Log.e(TAG, returnsUploadFormalisticsAPIResponse.status);

                    if ("SUCCESS".equals(returnsUploadFormalisticsAPIResponse.status)) {

                        //  set returns as synched
                        for (ItemReturn itemReturn : returnsUploadRequest.itemReturns) {

                            itemReturn.setIs_synced(true);
                            itemReturnDao.insertOrReplace(itemReturn);

                        }

                        for (CashReturn cashReturn : returnsUploadRequest.cashReturns) {

                            cashReturn.setIs_synced(true);
                            cashReturnDao.insertOrReplace(cashReturn);

                        }

                        return returnsUploadRequest;

                    } else {
                        if (returnsUploadFormalisticsAPIResponse.error != null) {
                            Log.e(TAG, returnsUploadFormalisticsAPIResponse.error);
                        } else {
                            Log.e(TAG, "Has error but cannot parse it.");
                        }
                    }

                } else {
                    Log.e(TAG, "Response has no status!");

                    if (returnsUploadFormalisticsAPIResponse.error_message != null) {
                        Log.e(TAG, returnsUploadFormalisticsAPIResponse.error_message);
                    }

                }

            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return null;

    }

}
