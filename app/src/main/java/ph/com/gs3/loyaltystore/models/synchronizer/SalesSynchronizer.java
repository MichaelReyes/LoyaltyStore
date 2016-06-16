package ph.com.gs3.loyaltystore.models.synchronizer;

import android.content.Context;
import android.util.Log;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.logging.HttpLoggingInterceptor;
import ph.com.gs3.loyaltystore.LoyaltyStoreApplication;
import ph.com.gs3.loyaltystore.models.api.SalesAPI;
import ph.com.gs3.loyaltystore.models.api.ServiceGenerator;
import ph.com.gs3.loyaltystore.models.api.objects.SalesRequest;
import ph.com.gs3.loyaltystore.models.api.objects.SalesUploadRequest;
import ph.com.gs3.loyaltystore.models.api.objects.SyncSalesAPIResponse;
import ph.com.gs3.loyaltystore.models.sqlite.dao.Sales;
import ph.com.gs3.loyaltystore.models.sqlite.dao.SalesDao;
import ph.com.gs3.loyaltystore.models.sqlite.dao.SalesHasReward;
import ph.com.gs3.loyaltystore.models.sqlite.dao.SalesHasRewardDao;
import ph.com.gs3.loyaltystore.models.sqlite.dao.SalesProduct;
import ph.com.gs3.loyaltystore.models.sqlite.dao.SalesProductDao;
import retrofit2.Call;
import retrofit2.Response;

/**
 * Created by Ervinne Sodusta on 2/15/2016.
 */
public class SalesSynchronizer {

    public static final String TAG = SalesSynchronizer.class.getSimpleName();

    public static List<Sales> sync(Context context, String server, String formalisticsServer) {

        SalesDao salesDao = LoyaltyStoreApplication.getSession().getSalesDao();
        SalesHasRewardDao salesHasRewardDao = LoyaltyStoreApplication.getSession().getSalesHasRewardDao();
        SalesProductDao salesProductDao = LoyaltyStoreApplication.getSession().getSalesProductDao();

        List<Sales> salesList = salesDao.queryBuilder().where(SalesDao.Properties.Is_synced.eq(false)).list();
        List<SalesRequest> salesRequests = new ArrayList<>();

        for (Sales sales : salesList) {
            SalesRequest request = SalesRequest.fromSales(sales);
            salesRequests.add(request);

            Log.v(TAG, "Sales: " + request.id + " " + request.amount + " " + request.transaction_date);
        }


        SalesUploadRequest salesUploadRequest = new SalesUploadRequest();
        salesUploadRequest.salesList = new ArrayList<>();
        salesUploadRequest.salesList.addAll(salesRequests);
        salesUploadRequest.salesHasRewards = new ArrayList<>();
        salesUploadRequest.salesProducts = new ArrayList<>();

        for (Sales sales : salesList) {
            List<SalesHasReward> salesHasRewards = salesHasRewardDao.queryBuilder().where(SalesHasRewardDao.Properties.Sales_transaction_number.eq(sales.getTransaction_number())).list();
            List<SalesProduct> salesProducts = salesProductDao.queryBuilder().where(SalesProductDao.Properties.Sales_transaction_number.eq(sales.getTransaction_number())).list();

            Log.v(TAG, salesProducts.size() + " sales products for " + sales.getStore_id());

            salesUploadRequest.salesHasRewards.addAll(salesHasRewards);
            salesUploadRequest.salesProducts.addAll(salesProducts);
        }

        List<SalesProduct> allSalesProducts = salesProductDao.loadAll();

        for (SalesProduct salesProduct : allSalesProducts) {
            Log.v(TAG, "Sales Product: " + salesProduct.getName() + " " + salesProduct.getSale_type() + " " + salesProduct.getQuantity() + " " + salesProduct.getSub_total() + " " + salesProduct.getSales_transaction_number());
        }

        Log.d(TAG,"Sales List Size : " + salesUploadRequest.salesList.size());

/*
        ServiceGenerator serviceGenerator = new ServiceGenerator(context, server, HttpLoggingInterceptor.Level.BODY);
        SalesAPI salesAPI = serviceGenerator.createService(SalesAPI.class);

        Call<SalesUploadResponse> call = salesAPI.uploadSales(salesUploadRequest);

        try {
            Response<SalesUploadResponse> response = call.execute();
            SalesUploadResponse salesUploadResponse = response.body();
            Log.v(TAG, "response: " + response.body());

            if (salesUploadResponse != null && salesUploadResponse.status != null) {
                Log.e(TAG, salesUploadResponse.status);

                if ("SUCCESS".equals(salesUploadResponse.status)) {

                    //  set sales as synched
                    for (Sales sales : salesList) {
                        sales.setIs_synced(true);
                        salesDao.update(sales);
                    }

                    return salesList;
                } else {
                    if (salesUploadResponse.error != null) {
                        Log.e(TAG, salesUploadResponse.error);
                    } else {
                        Log.e(TAG, "Has error but cannot parse it.");
                    }
                }

            } else {
                Log.e(TAG, "Response has no status!");

                if (response.errorBody() != null) {
                    Log.e(TAG, response.errorBody().string());
                }

            }

        } catch (IOException e) {
            e.printStackTrace();
        }
*/
        ServiceGenerator serviceGeneratorFormalistics = new ServiceGenerator(context, formalisticsServer, HttpLoggingInterceptor.Level.BODY);
        SalesAPI salesAPIFormalistics = serviceGeneratorFormalistics.createService(SalesAPI.class);


        Call<SyncSalesAPIResponse> salesUploadRequestCall = salesAPIFormalistics.uploadSalesToFormalistics(salesUploadRequest);

        try {
            Response<SyncSalesAPIResponse> apiResponse = salesUploadRequestCall.execute();
            SyncSalesAPIResponse salesUploadFormalisticsAPIResponse = apiResponse.body();

            if (salesUploadFormalisticsAPIResponse != null && salesUploadFormalisticsAPIResponse.status != null) {
                Log.e(TAG, salesUploadFormalisticsAPIResponse.status);

                if ("SUCCESS".equals(salesUploadFormalisticsAPIResponse.status)) {

                    //  set sales as synched
                    for (Sales sales : salesList) {
                        sales.setIs_synced(true);
                        salesDao.update(sales);
                    }

                    return salesList;
                } else {
                    if (salesUploadFormalisticsAPIResponse.error != null) {
                        Log.e(TAG, salesUploadFormalisticsAPIResponse.error);
                    } else {
                        Log.e(TAG, "Has error but cannot parse it.");
                    }
                }

            } else {
                Log.e(TAG, "Response has no status!");

                if (salesUploadFormalisticsAPIResponse.error_message != null) {
                    Log.e(TAG, salesUploadFormalisticsAPIResponse.error_message);
                }

            }

        } catch (IOException e) {
            e.printStackTrace();
        }


        return null;

    }

}
