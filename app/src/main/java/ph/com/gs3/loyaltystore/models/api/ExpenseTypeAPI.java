package ph.com.gs3.loyaltystore.models.api;

import java.util.List;

import ph.com.gs3.loyaltystore.models.sqlite.dao.ExpenseType;
import retrofit2.Call;
import retrofit2.http.GET;

/**
 * Created by Ervinne Sodusta on 2/3/2016.
 */
public interface ExpenseTypeAPI {

    @GET("pos/expense-type-list")
    Call<List<ExpenseType>> getExpenseTypeListInFormalistics();

}
