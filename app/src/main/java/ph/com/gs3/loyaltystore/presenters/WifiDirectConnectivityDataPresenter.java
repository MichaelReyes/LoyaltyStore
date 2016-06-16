package ph.com.gs3.loyaltystore.presenters;

import android.content.Context;
import android.net.wifi.p2p.WifiP2pConfig;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pGroup;
import android.net.wifi.p2p.WifiP2pManager;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONException;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

import ph.com.gs3.loyaltystore.models.WifiDirectConnectivityState;
import ph.com.gs3.loyaltystore.models.receivers.WifiDirectBroadcastReceiver;
import ph.com.gs3.loyaltystore.models.values.DeviceInfo;
import ph.com.gs3.loyaltystore.models.values.Retailer;


/**
 * Responsibilities in relation to wifi direct/P2P connectivity is delegated here
 * <p/>
 * Created by Ervinne Sodusta on 8/17/2015.
 */
public class WifiDirectConnectivityDataPresenter implements
        Observer,
        WifiDirectConnectivityState.WifiDirectPeerConnectivityStateListener {

    public static final String TAG = WifiDirectConnectivityDataPresenter.class.getSimpleName();

    private WifiDirectConnectivityPresentationListener wifiDirectConnectivityPresentationListener;

    private WifiDirectBroadcastReceiver wifiDirectBroadcastReceiver;
    private WifiP2pManager wifiP2pManager;
    private WifiP2pManager.Channel channel;

    private WifiDirectConnectivityState lastConnectivityState;

    private DeviceInfo deviceInfo;

    private Context context;

    private DeviceInfo.Type filterType;

    public WifiDirectConnectivityDataPresenter(Context context, DeviceInfo deviceInfo) {
        this.context = context;
        this.deviceInfo = deviceInfo;

        this.wifiDirectConnectivityPresentationListener = (WifiDirectConnectivityPresentationListener) context;

        wifiP2pManager = (WifiP2pManager) this.context.getSystemService(Context.WIFI_P2P_SERVICE);
        channel = wifiP2pManager.initialize(this.context, this.context.getMainLooper(), null);

        wifiDirectBroadcastReceiver = new WifiDirectBroadcastReceiver(wifiP2pManager, channel);
    }


    public void onResume() {

        // register this as an observer of the wifi direct connectivity state
        WifiDirectConnectivityState.getInstance().addObserver(this);
        WifiDirectConnectivityState.getInstance().addPeerConnectivityStateListener(this);

        // start listenting to wifi direct broadcasts
        context.registerReceiver(wifiDirectBroadcastReceiver, wifiDirectBroadcastReceiver.getIntentFilter());

        discoverPeers(filterType);
    }

    public void onDestroy() {

        WifiDirectConnectivityState.getInstance().deletePeerConnectivityStateListener(this);
        try {
            context.unregisterReceiver(wifiDirectBroadcastReceiver);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        }


    }

    public void discoverPeers(DeviceInfo.Type type) {
        this.filterType = type;
        wifiP2pManager.discoverPeers(channel, peerDiscoveryActionListener);
    }

    @Override
    public void update(Observable observable, Object data) {

        lastConnectivityState = (WifiDirectConnectivityState) observable;

//        if (!connectivityState.isConnectedToDevice()) {
//            connectivityState.reset();
//        }

        Log.d(TAG, "Detected change in the WifiDirectConnectivityState instance");

        // always update the devices regardless of object update
        // TODO: change this ^ implementation

        // filter the devices
        List<WifiP2pDevice> readableDevices = new ArrayList<>();
        for (WifiP2pDevice device : lastConnectivityState.getDeviceList()) {

            try {
                DeviceInfo deviceInfo = DeviceInfo.unserialize(device.deviceName);

                /*
                    Log.v(TAG, device.deviceName);
                    Log.v(TAG, deviceInfo.getType().toString());
                */

                if (deviceInfo.getType() == filterType) {
                    // add here
                    readableDevices.add(device);

                }
            } catch (JSONException e) {
                Log.i(TAG, "Device " + device.deviceName + " was ignored as it's name does not represent a valid device info this application uses");
            }

        }

        wifiDirectConnectivityPresentationListener.onNewPeersDiscovered(readableDevices);

    }

    public WifiDirectConnectivityState getLastConnectivityState () {
        return lastConnectivityState;
    }

    public void resetDeviceInfo(DeviceInfo deviceInfo) {
        this.deviceInfo = deviceInfo;
        initializeDeviceInfo();
    }

    public void initializeDeviceInfo() {

        try {
            Method m = wifiP2pManager.getClass().getMethod(
//                    "setSecondaryDeviceType",
                    "setDeviceName",
                    new Class[]{WifiP2pManager.Channel.class, String.class,
                            WifiP2pManager.ActionListener.class});

            final String serializedDeviceInfo = deviceInfo.serialize();

            Log.d(TAG, "Setting device information: " + serializedDeviceInfo);

            m.invoke(wifiP2pManager, channel, deviceInfo.getOwnerDisplayName(), new WifiP2pManager.ActionListener() {
                public void onSuccess() {
                    //Code for Success in changing name
                    Log.d(TAG, "Successfully set device information to: " + serializedDeviceInfo);
                }

                public void onFailure(int reason) {
                    //Code to be done while name change Fails
                    Log.e(TAG, "Failed to set device information. Reason number: " + reason);
                }
            });

        } catch (NoSuchMethodException e) {
            Log.w(TAG, "Failed to set device information, the method setSecondaryDeviceType does not exist in the WifiP2pManager class");
        } catch (InvocationTargetException e) {
            Log.e(TAG, "InvocationTargetException: " + e.getMessage());
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            Log.e(TAG, "IllegalAccessException: " + e.getMessage());
            e.printStackTrace();
        } catch (JSONException e) {
            Log.w(TAG, "Failed to serialize device information");
        }

    }

    private WifiP2pManager.ActionListener peerDiscoveryActionListener = new WifiP2pManager.ActionListener() {
        @Override
        public void onSuccess() {
            Log.v(TAG, "Successfully discovered peers");
        }

        @Override
        public void onFailure(int reason) {
            String message;

            switch (reason) {
                case WifiP2pManager.BUSY:
                    message = "Failed to discover peers, manager is busy";
                    break;
                case WifiP2pManager.ERROR:
                    message = "There was an error trying to search for peers";
                    break;
                case WifiP2pManager.NO_SERVICE_REQUESTS:
                    message = "Failed to discover peers, no service requests";
                    break;
                case WifiP2pManager.P2P_UNSUPPORTED:
                    message = "Failed to discover peers, peer to peer is not supported on this device";
                    break;
                default:
                    message = "Failed to discover peers, unable to determine why.";
            }

            Log.v(TAG, message);
        }
    };

    @Override
    public void onPeerDeviceConnectionEstablished() {
        WifiDirectConnectivityState observableState = WifiDirectConnectivityState.getInstance();
        Retailer retailer = Retailer.getDeviceRetailerFromSharedPreferences(context);

        Log.d(TAG, "Connection established");
        wifiDirectConnectivityPresentationListener.onConnectionEstablished();
    }

    @Override
    public void onPeerDeviceConnectionFailed() {


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

    public void cancelconnect() {

        if (wifiP2pManager != null && channel != null) {
            wifiP2pManager.cancelConnect(channel, new WifiP2pManager.ActionListener() {

                @Override
                public void onSuccess() {
                    Log.d(TAG, "Connection cancelled succesfully.");
                }

                @Override
                public void onFailure(int reasonCode) {
                    Log.d(TAG, "Connection cancel failed.");
                }
            });
        }
    }

    public void connectToCustomer(WifiP2pDevice customerDevice, int servicePort) {

        disconnect(new WifiP2pManager.ActionListener() {
            @Override
            public void onSuccess() {

            }

            @Override
            public void onFailure(int reason) {

            }
        });

        WifiP2pConfig config = new WifiP2pConfig();
        config.deviceAddress = customerDevice.deviceAddress;

        Log.v(TAG, "Connecting to: " + customerDevice.deviceAddress);

        wifiP2pManager.connect(channel, config, new WifiP2pManager.ActionListener() {
            @Override
            public void onSuccess() {
                //Toast.makeText(context, "Connected.", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(int reason) {
                wifiDirectConnectivityPresentationListener.onConnectionTerminated();
                Toast.makeText(context, "Connect failed.", Toast.LENGTH_SHORT).show();
            }
        });

    }

    public interface WifiDirectConnectivityPresentationListener {

        void onNewPeersDiscovered(List<WifiP2pDevice> wifiP2pDevices);

        void onConnectionEstablished();

        void onConnectionTerminated();

    }
}
