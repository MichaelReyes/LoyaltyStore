package ph.com.gs3.loyaltystore.models.api;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import ph.com.gs3.loyaltystore.globals.Constants;
import ph.com.gs3.loyaltystore.models.services.converters.ToStringConverterFactory;
import retrofit.GsonConverterFactory;
import retrofit.Retrofit;

/**
 * Created by Michael Reyes on 11/4/2015.
 */
public class HttpCommunicator {

    public static final String TAG = HttpCommunicator.class.getSimpleName();

    private Constants constants;

    private Retrofit retrofit;

    public HttpCommunicator() {

        constants = new Constants();

        Gson gson = new GsonBuilder()
                .setDateFormat("yyyy-MM-dd'T'HH:mm:ssZ")
                .create();

        retrofit = new Retrofit.Builder()
                .baseUrl(constants.SERVER_ADDRESS)
                .addConverterFactory(new ToStringConverterFactory())
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();

    }


    public Retrofit getRetrofit() {
        return retrofit;
    }


}
