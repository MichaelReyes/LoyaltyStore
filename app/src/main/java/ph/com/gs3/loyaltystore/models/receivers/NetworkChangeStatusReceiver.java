package ph.com.gs3.loyaltystore.models.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.WifiManager;

/**
 * Created by Bryan-PC on 22/02/2016.
 */
public class NetworkChangeStatusReceiver extends BroadcastReceiver {


    private NetworkChangeStatusListener networkChangeStatusListener;

    public NetworkChangeStatusReceiver(NetworkChangeStatusListener networkChangeStatusListener) {
        this.networkChangeStatusListener = networkChangeStatusListener;
    }

    @Override
    public void onReceive(Context context, Intent intent) {

        WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);

        try {
            networkChangeStatusListener.onChangeNetworkStatus(wifiManager.isWifiEnabled());
        } catch (InterruptedException e) {
            e.printStackTrace();
        }


    }

    public IntentFilter getIntentFilter() {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(WifiManager.WIFI_STATE_CHANGED_ACTION);
        return intentFilter;
    }

    public interface NetworkChangeStatusListener {

        void onChangeNetworkStatus(boolean isWifiEnabled) throws InterruptedException;

    }

}
