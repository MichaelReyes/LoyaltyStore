package ph.com.gs3.loyaltystore;

import android.content.Intent;
import android.net.wifi.p2p.WifiP2pDevice;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import java.util.List;

import ph.com.gs3.loyaltystore.models.values.Retailer;
import ph.com.gs3.loyaltystore.presenters.WifiDirectConnectivityDataPresenter;

public class SettingsActivity extends AppCompatActivity implements WifiDirectConnectivityDataPresenter.WifiDirectConnectivityPresentationListener {

    private EditText etRetailName;
    private EditText etAdvertisement;
    private EditText etServicePortNumber;

    private Button bSave;

    private Retailer retailer;

    private WifiDirectConnectivityDataPresenter wifiDirectConnectivityDataPresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        retailer = Retailer.getDeviceRetailerFromSharedPreferences(this);

        wifiDirectConnectivityDataPresenter = new WifiDirectConnectivityDataPresenter(
                this, retailer.getDeviceInfo()
        );

        initializeComponents();
        initializeData();

    }

    private void initializeComponents() {

        etRetailName = (EditText) findViewById(R.id.Settings_etStoreName);
        etAdvertisement = (EditText) findViewById(R.id.Settings_etAdvertisement);
        etServicePortNumber = (EditText) findViewById(R.id.Settings_etServicePortNumber);

        bSave = (Button) findViewById(R.id.Settings_bSave);
        bSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                retailer.setStoreName(etRetailName.getText().toString());
                retailer.setAdvertisment(etAdvertisement.getText().toString());
                retailer.setServicePortNumber(Integer.parseInt(etServicePortNumber.getText().toString()));

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
    }

    private void initializeData() {

        etRetailName.setText(retailer.getStoreName());
        etAdvertisement.setText(retailer.getAdvertisment());
        etServicePortNumber.setText(Integer.toString(retailer.getServicePortNumber()));

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
}
