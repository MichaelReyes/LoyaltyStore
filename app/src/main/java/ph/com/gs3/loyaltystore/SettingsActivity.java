package ph.com.gs3.loyaltystore;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.p2p.WifiP2pDevice;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import ph.com.gs3.loyaltystore.models.api.HttpCommunicator;
import ph.com.gs3.loyaltystore.models.services.AdvertisementSenderService;
import ph.com.gs3.loyaltystore.models.sqlite.dao.Store;
import ph.com.gs3.loyaltystore.models.sqlite.dao.StoreDao;
import ph.com.gs3.loyaltystore.models.values.Retailer;
import ph.com.gs3.loyaltystore.presenters.WifiDirectConnectivityDataPresenter;
import retrofit.Call;
import retrofit.Callback;
import retrofit.Response;
import retrofit.Retrofit;
import retrofit.http.Field;
import retrofit.http.FormUrlEncoded;
import retrofit.http.GET;
import retrofit.http.POST;
import retrofit.http.Path;

public class SettingsActivity extends Activity
        implements WifiDirectConnectivityDataPresenter.WifiDirectConnectivityPresentationListener {

    public static final String TAG = SettingsActivity.class.getSimpleName();

    private EditText etRetailName;
    private EditText etAdvertisement;
    private EditText etServicePortNumber;
    private EditText etServerUrl;

    private Button bSave;

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    private Button bRegister;
    private Button bBroadcast;

    private List<String> retailerNameList;
    private ArrayAdapter<String> retailerNameListAdapter;

    private Retailer retailer;

    private HttpCommunicator httpCommunicator;
    private Retrofit retrofit;
    private RegisterStoreDeviceAPI registerStoreDeviceAPI;

    private WifiDirectConnectivityDataPresenter wifiDirectConnectivityDataPresenter;

    private ProgressDialog progressDialog;

    private StoreDao storeDao;

    private boolean isDeviceRegister;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        retailer = Retailer.getDeviceRetailerFromSharedPreferences(this);

        wifiDirectConnectivityDataPresenter = new WifiDirectConnectivityDataPresenter(
                this, retailer.getDeviceInfo()
        );

        initializeDao();
        initializeComponents();
        initializeData();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        progressDialog.dismiss();
    }

    private void initializeDao() {

        storeDao = LoyaltyStoreApplication.getInstance().getSession().getStoreDao();

    }

    private void initializeComponents() {

        progressDialog = new ProgressDialog(this);

        retailerNameList = new ArrayList<>();
        retailerNameListAdapter = new ArrayAdapter<String>(
                this, android.R.layout.simple_spinner_item, retailerNameList);

        retailerNameListAdapter.setDropDownViewResource(
                android.R.layout.simple_spinner_dropdown_item
        );

        etRetailName = (EditText) findViewById(R.id.Settings_etStoreName);
        etServicePortNumber = (EditText) findViewById(R.id.Settings_etServicePortNumber);
        etAdvertisement = (EditText) findViewById(R.id.Settings_etAdvertisement);
        etServerUrl = (EditText) findViewById(R.id.Settings_etServerAddress);

        bRegister = (Button) findViewById(R.id.Settings_bRegister);
        bRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isNetworkAvailable()) {
                    onGetAvailableBranches();
                }
            }
        });

        bSave = (Button) findViewById(R.id.Settings_bSave);
        bSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                registerDevice();

                if (!isDeviceRegister) {
                    if (isNetworkAvailable()) {
                        registerDevice();
                    }
                }

                retailer.setStoreName(etRetailName.getText().toString());
                retailer.setAdvertisment(etAdvertisement.getText().toString());
                retailer.setServicePortNumber(Integer.parseInt(etServicePortNumber.getText().toString()));
                retailer.setServerUrl(etServerUrl.getText().toString());

                retailer.save(SettingsActivity.this);

                wifiDirectConnectivityDataPresenter.resetDeviceInfo(retailer.getDeviceInfo());

                //restart the app
                Intent intentRestart = getBaseContext().getPackageManager()
                        .getLaunchIntentForPackage(getBaseContext().getPackageName());
                intentRestart.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intentRestart);

                //finish();

                //  TODO: add a date updated in the preferences
            }
        });

        bBroadcast = (Button) findViewById(R.id.Settings_bBroadcast);
        bBroadcast.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBroadcastAdvertisment();
            }
        });

    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    private void registerDevice() {

        if (!etServerUrl.getText().toString().equals("")) {

            initializeApiCommunicator(etServerUrl.getText().toString());

            List<Store> stores = getStoreDataByName(etRetailName.getText().toString());

            for (final Store store : stores) {

                Call<String> registerCall = registerStoreDeviceAPI.registerStore(
                        Long.toString(store.getId()), retailer.getDeviceId()
                );

                registerCall.enqueue(new Callback<String>() {
                    @Override
                    public void onResponse(Response<String> response, Retrofit retrofit) {
                        Log.d(TAG, " RESPONSE : " + response.body().toString());

                        try {
                            JSONObject jsonObject = new JSONObject(response.body().toString());


                            retailer.setStoreId(store.getId());
                            retailer.save(SettingsActivity.this);

                            store.setDevice_web_id(jsonObject.getInt("device_web_id"));
                            storeDao.update(store);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }

                    @Override
                    public void onFailure(Throwable t) {
                        Toast.makeText(
                                SettingsActivity.this,
                                "Failed to register device. Please check your network connection.",
                                Toast.LENGTH_LONG
                        ).show();
                    }
                });

            }

        }


    }

    private List<Store> getStoreDataByName(String name) {

        return storeDao.queryRaw(
                "WHERE " + StoreDao.Properties.Name.columnName + "=?",
                new String[]{name}
        );

    }

    private void onBroadcastAdvertisment() {

        if (etAdvertisement.getText().toString() != "") {

            retailer.setAdvertisment(etAdvertisement.getText().toString());
            retailer.save(this);

            Intent intent = new Intent(this, AdvertisementSenderService.class);
            startService(intent);

        }


    }

    private void onGetAvailableBranches() {

        if (!etServerUrl.getText().toString().equals("")) {

            initializeApiCommunicator(etServerUrl.getText().toString());

            showDialog("Please wait while getting available branches...");

            Call<List<Store>> storesCall = registerStoreDeviceAPI.getStoresWithNoDeviceID();

            storesCall.enqueue(new Callback<List<Store>>() {
                @Override
                public void onResponse(Response<List<Store>> response, Retrofit retrofit) {

                    retailerNameList.clear();

                    List<Store> stores = response.body();

                    storeDao.deleteAll();

                    for (Store store : stores) {

                        storeDao.insert(store);

                        retailerNameList.add(store.getName());

                    }

                    retailerNameListAdapter.notifyDataSetChanged();

                    hideDialog();
                    setChoices();

                }

                @Override
                public void onFailure(Throwable t) {

                    Toast.makeText(
                            SettingsActivity.this,
                            "Failed to get information from web",
                            Toast.LENGTH_LONG
                    ).show();
                    Log.d(TAG, "Failed to get information from web. Please check your network connection.");
                    hideDialog();

                }
            });

        }
    }

    private void showDialog(String message) {

        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setMessage(message);
        progressDialog.setIndeterminate(true);
        progressDialog.setCancelable(false);
        progressDialog.show();

    }

    private void hideDialog() {
        if (progressDialog.isShowing()) {
            progressDialog.hide();
        }
    }

    private void setChoices() {
        AlertDialog.Builder builder = new AlertDialog.Builder(SettingsActivity.this);
        builder.setTitle("Select desired branch");

        builder.setNegativeButton(
                "cancel",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

        builder.setAdapter(
                retailerNameListAdapter,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        etRetailName.setText(retailerNameListAdapter.getItem(which));

                    }
                });
        builder.show();

    }

    private void initializeData() {

        if (!retailer.getStoreName().equals("")) {
            isDeviceRegister = true;
        }

        etRetailName.setText(retailer.getStoreName());
        etAdvertisement.setText(retailer.getAdvertisment());
        etServerUrl.setText(retailer.getServerUrl());
        etServicePortNumber.setText(Integer.toString(retailer.getServicePortNumber()));

    }

    private void initializeApiCommunicator(String serverUrl) {

        httpCommunicator = new HttpCommunicator(serverUrl);
        retrofit = httpCommunicator.getRetrofit();
        registerStoreDeviceAPI = retrofit.create(RegisterStoreDeviceAPI.class);

    }

    @Override
    public void onNewPeersDiscovered(List<WifiP2pDevice> wifiP2pDevices) {

    }

    @Override
    public void onConnectionEstablished() {

    }

    @Override
    public void onConnectionTerminated() {

    }

    public interface RegisterStoreDeviceAPI {

        @GET("/stores/nodevice")
        Call<List<Store>> getStoresWithNoDeviceID();

        @FormUrlEncoded
        @POST("/stores/{id}/device/register")
        Call<String> registerStore(@Path("id") String id,
                                   @Field("device_id") String device_id);

    }

}
