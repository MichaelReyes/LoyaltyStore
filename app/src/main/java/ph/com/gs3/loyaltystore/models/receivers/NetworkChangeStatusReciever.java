package ph.com.gs3.loyaltystore.models.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.WifiManager;
import android.widget.Toast;

/**
 * Created by Bryan-PC on 22/02/2016.
 */
public class NetworkChangeStatusReciever extends BroadcastReceiver {


    @Override
    public void onReceive(Context context, Intent intent) {

        WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);

        if(wifiManager.isWifiEnabled()){

            Toast.makeText(context, context.getClass().getSimpleName() + " " + wifiManager.isWifiEnabled() + "", Toast.LENGTH_LONG).show();

        }

    }

    public IntentFilter getIntentFilter() {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION);
        return intentFilter;
    }
}
