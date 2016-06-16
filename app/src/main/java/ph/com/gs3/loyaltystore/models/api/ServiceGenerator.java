package ph.com.gs3.loyaltystore.models.api;

import android.content.Context;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.Date;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import ph.com.gs3.loyaltystore.com.franmontiel.persistentcookiejar.ClearableCookieJar;
import ph.com.gs3.loyaltystore.com.franmontiel.persistentcookiejar.PersistentCookieJar;
import ph.com.gs3.loyaltystore.com.franmontiel.persistentcookiejar.cache.SetCookieCache;
import ph.com.gs3.loyaltystore.com.franmontiel.persistentcookiejar.persistence.SharedPrefsCookiePersistor;
import retrofit2.GsonConverterFactory;
import retrofit2.Retrofit;

public class ServiceGenerator {

    public static final String TAG = ServiceGenerator.class.getSimpleName();

    //    private static CookieManager cookieManager;
    private static OkHttpClient client;
    private OkHttpClient.Builder httpClientBuilder;

    private Retrofit.Builder builder;

    private Context context;

    public ServiceGenerator(Context context, String baseUrl) {
        this.context = context;
        setupCookies();
        setupHttpClientBuilder(null);
        setupBuilder(baseUrl);
    }

    public ServiceGenerator(Context context, String baseUrl, HttpLoggingInterceptor.Level loggingLevel) {
        this.context = context;
        setupCookies();
        setupHttpClientBuilder(loggingLevel);
        setupBuilder(baseUrl);
    }

    private void setupHttpClientBuilder(HttpLoggingInterceptor.Level loggingLevel) {
        httpClientBuilder = new OkHttpClient.Builder();
        httpClientBuilder.connectTimeout(0, TimeUnit.SECONDS);
        httpClientBuilder.readTimeout(0,TimeUnit.SECONDS);

        if (loggingLevel != null) {
            HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
            interceptor.setLevel(loggingLevel);
            httpClientBuilder.interceptors().add(interceptor);
        }

    }

    private void setupBuilder(String baseUrl) {

        Gson gson = new GsonBuilder()
//                .setDateFormat("yyyy-MM-dd'T'HH:mm:ssZ")
                .registerTypeAdapter(Date.class, new DateDeserializer())
                .create();

        builder = new Retrofit.Builder()
                .baseUrl(baseUrl)
                .addConverterFactory(GsonConverterFactory.create(gson));
    }

    private void setupCookies() {

//        if (cookieManager == null) {
//            cookieManager = new CookieManager();
//            cookieManager.setCookiePolicy(CookiePolicy.ACCEPT_ALL);
//            CookieHandler.setDefault(myCookieManager);
//        }

    }


    public <S> S createService(Class<S> serviceClass) {

        if (client == null) {
            ClearableCookieJar cookieJar = new PersistentCookieJar(new SetCookieCache(), new SharedPrefsCookiePersistor(context));
            client = httpClientBuilder.cookieJar(cookieJar).build();
        }

        Retrofit retrofit = builder.client(client).build();
        return retrofit.create(serviceClass);
    }
}