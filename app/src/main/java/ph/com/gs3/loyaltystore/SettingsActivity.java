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

import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import ph.com.gs3.loyaltystore.models.api.HttpCommunicator;
import ph.com.gs3.loyaltystore.models.api.objects.FormalisticsAPIResponse;
import ph.com.gs3.loyaltystore.models.services.AdvertisementSenderService;
import ph.com.gs3.loyaltystore.models.sqlite.dao.Store;
import ph.com.gs3.loyaltystore.models.sqlite.dao.StoreDao;
import ph.com.gs3.loyaltystore.models.values.Retailer;
import ph.com.gs3.loyaltystore.presenters.WifiDirectConnectivityDataPresenter;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

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

    private boolean isDeviceRegistered;

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
                if (isNetworkAvailable() && !isDeviceRegistered) {
                    //onGetAvailableBranches();
                    onGetAvailableBranchesFromFormalistics();
                }else{
                    Toast.makeText(SettingsActivity.this,"Device already registered.",Toast.LENGTH_LONG).show();
                }
            }
        });

        bSave = (Button) findViewById(R.id.Settings_bSave);
        bSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                retailer.setStoreName(etRetailName.getText().toString());
                retailer.setAdvertisment(etAdvertisement.getText().toString());
                retailer.setServicePortNumber(Integer.parseInt(etServicePortNumber.getText().toString()));
                retailer.setServerUrl(etServerUrl.getText().toString());

                retailer.save(SettingsActivity.this);

                wifiDirectConnectivityDataPresenter.resetDeviceInfo(retailer.getDeviceInfo());

                if (!isDeviceRegistered) {
                    registerDeviceInFormalistics();
                }else{
                    finish();
                }

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

    private void onGetAvailableBranchesFromFormalistics() {

        if (!etServerUrl.getText().toString().equals("")) {

            initializeApiCommunicator(etServerUrl.getText().toString());

            showDialog("Please wait while getting available branches...");

            Call<List<Store>> storesCall = registerStoreDeviceAPI.getAvailableStoresForRegistration();
            final Gson gson= new Gson();
            storesCall.enqueue(new Callback<List<Store>>() {
                @Override
                public void onResponse(Response<List<Store>> response) {

                    List<Store> stores = response.body();
                    Log.d(TAG,"RECIEVED : " + gson.toJson(response.body()));
                    retailerNameList.clear();

                    storeDao.deleteAll();

                    for (Store store : stores) {

                        storeDao.insert(store);

                        Log.d(TAG, "Store Id : " + store.getId());
                        Log.d(TAG, "Store Name : " + store.getName());

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
                    Log.e(TAG, "Failed to get information from web. Please check your network connection.");
                    hideDialog();

                }
            });

        } else {
            Toast.makeText(
                    SettingsActivity.this,
                    "Please provide a server url to continue.",
                    Toast.LENGTH_LONG
            ).show();
        }

    }

    private void registerDeviceInFormalistics() {
        if (!etServerUrl.getText().toString().equals("")) {

            Log.d(TAG, "registerDeviceInFormalistics");

            initializeApiCommunicator(etServerUrl.getText().toString());

            showDialog("Registering device, Please wait...");

            List<Store> stores = getStoreDataByName(etRetailName.getText().toString());

            for (final Store store : stores) {

                Log.d(TAG, " INFO SENT : " + store.getId() + " ~ " + retailer.getDeviceId());

                Call<FormalisticsAPIResponse> registerCall = registerStoreDeviceAPI.registerStoreDevice(
                        Long.toString(store.getId()), retailer.getDeviceId()
                );

                registerCall.enqueue(new Callback<FormalisticsAPIResponse>() {
                    @Override
                    public void onResponse(Response<FormalisticsAPIResponse> response) {

                        Gson gson = new Gson();

                        Log.d(TAG, " ON RESPONSE " + gson.toJson(response.body()));

                        FormalisticsAPIResponse formalisticsAPIResponse = response.body();

                        Log.d(TAG, "========== RESPONSE START ==========");

                        Log.d(TAG, "Status : " + formalisticsAPIResponse.status);
                        Log.d(TAG, "error : " + formalisticsAPIResponse.error);
                        Log.d(TAG, "error_message : " + formalisticsAPIResponse.error_message);
                        Log.d(TAG, "result : " +  formalisticsAPIResponse.results);

                        Log.d(TAG, "========== RESPONSE END ==========");

                        retailer.setStoreId(store.getId());
                        retailer.save(SettingsActivity.this);

                        store.setDevice_web_id(store.getId());
                        storeDao.update(store);

                        hideDialog();

                        if(formalisticsAPIResponse.error == null){
                            registrationSuccessfulDialog();
                        }else{
                            Toast.makeText(
                                    SettingsActivity.this,
                                    "Failed to register device.\n " + formalisticsAPIResponse.error_message,
                                    Toast.LENGTH_LONG
                            ).show();
                        }



                    }

                    @Override
                    public void onFailure(Throwable t) {
                        Toast.makeText(
                                SettingsActivity.this,
                                "Failed to register device. Please check your network connection.",
                                Toast.LENGTH_LONG
                        ).show();

                        hideDialog();

                    }
                });

            }

        }
    }

    private void onGetAvailableBranches() {

        if (!etServerUrl.getText().toString().equals("")) {

            initializeApiCommunicator(etServerUrl.getText().toString());

            showDialog("Please wait while getting available branches...");

            Call<List<Store>> storesCall = registerStoreDeviceAPI.getStoresWithNoDeviceID();

            storesCall.enqueue(new Callback<List<Store>>() {
                @Override
                public void onResponse(Response<List<Store>> response) {

                    retailerNameList.clear();

                    List<Store> stores = response.body();

                    Log.d(TAG, "SIZE : " + stores.size());

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
                    Log.e(TAG, "Failed to get information from web. Please check your network connection.");
                    hideDialog();

                }
            });

        }
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
                    public void onResponse(Response<String> response) {
                        Log.d(TAG, " RESPONSE : " + response.body().toString());

                        try {
                            JSONObject jsonObject = new JSONObject(response.body().toString());


                            retailer.setStoreId(store.getId());
                            retailer.save(SettingsActivity.this);

                            store.setDevice_web_id(jsonObject.getLong("device_web_id"));
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

    private void registrationSuccessfulDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Device successfully registered. \n");

        builder.setNegativeButton("Close", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
                restartApp();
            }
        });

        builder.show();
    }

    private void restartApp(){

        Intent intentRestart = getBaseContext().getPackageManager()
                        .getLaunchIntentForPackage(getBaseContext().getPackageName());
                intentRestart.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intentRestart);

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
            progressDialog.dismiss();
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
            isDeviceRegistered = true;
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

        @GET("pos/available-stores-for-registration")
        Call<List<Store>> getAvailableStoresForRegistration();


        @FormUrlEncoded
        @POST("/pos/register-store")
        Call<FormalisticsAPIResponse> registerStoreDevice(@Field("store_id") String storeId,
                                                                    @Field("device_id") String deviceId);


    }

}
