package ph.com.gs3.loyaltystore.models.api;

import java.util.List;

import ph.com.gs3.loyaltystore.models.api.objects.UploadAPIResponse;
import ph.com.gs3.loyaltystore.models.sqlite.dao.Expenses;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

/**
 * Created by Ervinne Sodusta on 2/15/2016.
 */
public interface ExpensesAPI {

    @POST("pos/expenses-upload")
    Call<UploadAPIResponse> uploadExpensesToFormalistics(@Body List<Expenses> expensesList);

}
