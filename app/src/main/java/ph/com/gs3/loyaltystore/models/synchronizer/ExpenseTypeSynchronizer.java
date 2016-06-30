package ph.com.gs3.loyaltystore.models.synchronizer;

import android.content.Context;
import android.util.Log;

import java.io.IOException;
import java.util.List;

import okhttp3.logging.HttpLoggingInterceptor;
import ph.com.gs3.loyaltystore.LoyaltyStoreApplication;
import ph.com.gs3.loyaltystore.models.api.ExpenseTypeAPI;
import ph.com.gs3.loyaltystore.models.api.ServiceGenerator;
import ph.com.gs3.loyaltystore.models.sqlite.dao.ExpenseType;
import ph.com.gs3.loyaltystore.models.sqlite.dao.ExpenseTypeDao;
import retrofit2.Call;
import retrofit2.Response;

/**
 * Created by Ervinne Sodusta on 2/3/2016.
 */
public class ExpenseTypeSynchronizer {

    public static final String TAG = ExpenseTypeSynchronizer.class.getSimpleName();

    public static List<ExpenseType> sync(Context context, String server) {

        ServiceGenerator serviceGenerator = new ServiceGenerator(context, server, HttpLoggingInterceptor.Level.BODY);
        ExpenseTypeAPI expenseTypeAPI = serviceGenerator.createService(ExpenseTypeAPI.class);

        Call<List<ExpenseType>> call = expenseTypeAPI.getExpenseTypeListInFormalistics();

        try {
            Response<List<ExpenseType>> response = call.execute();
            List<ExpenseType> expenseTypeList = response.body();

            if (expenseTypeList != null && expenseTypeList.size() > 0) {
                //  save expenseTypeList

                ExpenseTypeDao expenseTypeDao
                        = LoyaltyStoreApplication.getSession().getExpenseTypeDao();

                Log.v(TAG, "========== EXPENSE TYPE INSERTED START ==========");

                for(ExpenseType expenseType : expenseTypeList){

                    long insertedId = expenseTypeDao.insertOrReplace(expenseType);

                    Log.v(
                            TAG,
                            "Expense Type Id : " + expenseType.getId() + " ~ " +
                                    " Expense Type : " + expenseType.getType()
                    );

                }

                Log.v(TAG, "========== EXPENSE TYPE INSERTED END ==========");


                return expenseTypeList;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;

    }

}
