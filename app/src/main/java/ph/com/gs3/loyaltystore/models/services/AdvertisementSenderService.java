package ph.com.gs3.loyaltystore.models.services;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.p2p.WifiP2pConfig;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pGroup;
import android.net.wifi.p2p.WifiP2pManager;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import java.util.List;

import ph.com.gs3.loyaltystore.models.WifiDirectConnectivityState;
import ph.com.gs3.loyaltystore.models.receivers.WifiDirectBroadcastReceiver;
import ph.com.gs3.loyaltystore.models.tasks.SendAdvertisementTask;
import ph.com.gs3.loyaltystore.models.values.Retailer;


/**
 * Created by Ervinne Sodusta on 10/20/2015.
 */
public class AdvertisementSenderService extends Service
        implements WifiDirectConnectivityState.WifiDirectPeerConnectivityStateListener {

    public static final String TAG = AdvertisementSenderService.class.getSimpleName();
    public static final String NAME = AdvertisementSenderService.class.getName();

    //  TODO: set a configuration for this
    public static final int ADVERTISEMENT_PORT = 3001;

    private WifiDirectBroadcastReceiver wifiDirectBroadcastReceiver;
    private WifiP2pManager wifiP2pManager;
    private WifiP2pManager.Channel channel;

    private WifiDirectConnectivityState wifiDirectConnectivityState;

    private Retailer currentRetailer;
    private List<WifiP2pDevice> deviceQueue;
    private int currentDeviceIndex;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    @Override
    public void onCreate() {
        super.onCreate();

        wifiP2pManager = (WifiP2pManager) getSystemService(Context.WIFI_P2P_SERVICE);
        channel = wifiP2pManager.initialize(this, this.getMainLooper(), null);

        wifiDirectBroadcastReceiver = new WifiDirectBroadcastReceiver(wifiP2pManager, channel);

        wifiDirectConnectivityState = WifiDirectConnectivityState.getInstance();
    }

    @Override
    public void onDestroy() {
        destroy();
        super.onDestroy();
    }

//    @Override
//    protected void onHandleIntent(Intent intent) {

    @Override
    public void onStart(Intent intent, int startId) {
        super.onStart(intent, startId);

        currentRetailer = Retailer.getDeviceRetailerFromSharedPreferences(this);

        initialize();
        currentDeviceIndex = 0;
        deviceQueue = wifiDirectConnectivityState.getDeviceList();

        Log.v(TAG, TAG + " service started. " + deviceQueue.size() + " devices to send advertisements to.");

        sendNextAdvertisement();

    }

    private void sendNextAdvertisement() {

        if (currentDeviceIndex >= deviceQueue.size()) {
            destroy();
        } else {
            Log.v(TAG, "Sending advertisement to next device: index: " + currentDeviceIndex);
            sendAdvertisement(deviceQueue.get(currentDeviceIndex));
            currentDeviceIndex++;
        }

    }

    private void sendAdvertisement(WifiP2pDevice device) {

        WifiP2pConfig config = new WifiP2pConfig();
        config.deviceAddress = device.deviceAddress;

        Log.v(TAG, "Connecting to: " + device.deviceAddress);

        wifiP2pManager.connect(channel, config, new WifiP2pManager.ActionListener() {
            @Override
            public void onSuccess() {
                Toast.makeText(AdvertisementSenderService.this, "Connected. Sending Advertisement", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(int reason) {
                Toast.makeText(AdvertisementSenderService.this, "Connect failed. Advertisement not sent.", Toast.LENGTH_SHORT).show();
            }
        });

    }

    public void initialize() {
        WifiDirectConnectivityState.getInstance().addPeerConnectivityStateListener(this);
        registerReceiver(
                wifiDirectBroadcastReceiver, wifiDirectBroadcastReceiver.getIntentFilter()
        );
    }

    public void destroy() {

        WifiDirectConnectivityState.getInstance().deletePeerConnectivityStateListener(this);
        // TODO: check if there is a disconnection required here later

        try {
            if (wifiDirectBroadcastReceiver != null) {
                unregisterReceiver(wifiDirectBroadcastReceiver);
            }
        } catch (IllegalArgumentException e) {
            Log.i(TAG, "wifiDirectBroadcastReceiver is already unregistered");
            wifiDirectBroadcastReceiver = null;
        }

    }

    /**
     * When the customer accepts connection
     */
    @Override
    public void onPeerDeviceConnectionEstablished() {
        Log.v(TAG, "Peer device connection established");

        SendAdvertisementTask sendAdvertisementTask = new SendAdvertisementTask(this,ADVERTISEMENT_PORT, currentRetailer, new SendAdvertisementTask.SendAdvertisementTaskListener() {
            @Override
            public void onFinish() {

                /**
                * Disconnect before sending advertisement to the next device.
                */

                disconnect(new WifiP2pManager.ActionListener() {
                    @Override
                    public void onSuccess() {
                        Toast.makeText(AdvertisementSenderService.this, "Advertisment Sent.", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onFailure(int i) {
                        Toast.makeText(AdvertisementSenderService.this, "Failed to disconnect.", Toast.LENGTH_SHORT).show();
                    }
                });

                sendNextAdvertisement();
            }
        });

        sendAdvertisementTask.execute();

    }

    @Override
    public void onPeerDeviceConnectionFailed() {
        Log.v(TAG, "Peer device connection failed");
        sendNextAdvertisement();
    }

    public void disconnect(final WifiP2pManager.ActionListener actionListener) {

        if (wifiP2pManager != null && channel != null) {
            wifiP2pManager.requestGroupInfo(channel, new WifiP2pManager.GroupInfoListener() {
                @Override
                public void onGroupInfoAvailable(WifiP2pGroup group) {
                    if (group != null && wifiP2pManager != null && channel != null /*&& group.isGroupOwner()*/) {
                        wifiP2pManager.removeGroup(channel, actionListener);
                    }
                }
            });
        }
    }

}
