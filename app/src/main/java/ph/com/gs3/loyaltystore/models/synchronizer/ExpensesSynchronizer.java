package ph.com.gs3.loyaltystore.models.synchronizer;

import android.content.Context;
import android.util.Log;

import java.io.IOException;
import java.util.List;

import okhttp3.logging.HttpLoggingInterceptor;
import ph.com.gs3.loyaltystore.LoyaltyStoreApplication;
import ph.com.gs3.loyaltystore.models.api.ExpensesAPI;
import ph.com.gs3.loyaltystore.models.api.ServiceGenerator;
import ph.com.gs3.loyaltystore.models.api.objects.UploadAPIResponse;
import ph.com.gs3.loyaltystore.models.sqlite.dao.Expenses;
import ph.com.gs3.loyaltystore.models.sqlite.dao.ExpensesDao;
import retrofit2.Call;
import retrofit2.Response;

/**
 * Created by Michael Reyes on 2/15/2016.
 */
public class ExpensesSynchronizer {

    public static final String TAG = ExpensesSynchronizer.class.getSimpleName();

    public static List<Expenses> sync(Context context, String formalisticsServer) {

        ExpensesDao expensesDao
                = LoyaltyStoreApplication.getSession().getExpensesDao();


        List<Expenses> expensesList
                = expensesDao
                    .queryBuilder()
                    .where(
                            ExpensesDao.Properties.Is_synced.eq(false)
                    ).list();


        ServiceGenerator serviceGeneratorFormalistics = new ServiceGenerator(context, formalisticsServer, HttpLoggingInterceptor.Level.BODY);
        ExpensesAPI expensesAPI = serviceGeneratorFormalistics.createService(ExpensesAPI.class);


        Call<UploadAPIResponse> expensesUploadRequestCall = expensesAPI.uploadExpensesToFormalistics(expensesList);

        try {
            Response<UploadAPIResponse> apiResponse = expensesUploadRequestCall.execute();
            UploadAPIResponse uploadAPIResponse = apiResponse.body();

            if (uploadAPIResponse != null && uploadAPIResponse.status != null) {
                Log.e(TAG, uploadAPIResponse.status);

                if ("SUCCESS".equals(uploadAPIResponse.status)) {

                    for(Expenses expenses : expensesList){

                        expenses.setIs_synced(true);
                        expensesDao.insertOrReplace(expenses);

                    }

                    return expensesList;
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
