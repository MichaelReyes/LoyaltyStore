package ph.com.gs3.loyaltystore.models.api;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.Date;

import ph.com.gs3.loyaltystore.globals.Constants;
import ph.com.gs3.loyaltystore.models.services.converters.ToStringConverterFactory;
import retrofit2.GsonConverterFactory;
import retrofit2.Retrofit;

/**
 * Created by Michael Reyes on 11/4/2015.
 */
public class HttpCommunicator {

    public static final String TAG = HttpCommunicator.class.getSimpleName();

    private Constants constants;

    private Retrofit retrofit;

    public HttpCommunicator(String serverUrl) {

        constants = new Constants();

        Gson gson = new GsonBuilder()
                .setDateFormat("yyyy-MM-dd'T'HH:mm:ssZ")
                .registerTypeAdapter(Date.class, new DateDeserializer())
                .create();

        retrofit = new Retrofit.Builder()
                .baseUrl("http://" + serverUrl)
                .addConverterFactory(new ToStringConverterFactory())
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();

    }


    public Retrofit getRetrofit() {
        return retrofit;
    }


}
