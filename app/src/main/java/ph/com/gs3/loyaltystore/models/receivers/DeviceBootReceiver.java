package ph.com.gs3.loyaltystore.models.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Created by Bryan-PC on 13/06/2016.
 */
public class DeviceBootReceiver extends BroadcastReceiver {
    AlarmBroadcastReceiver alarm = new AlarmBroadcastReceiver();
    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals("android.intent.action.BOOT_COMPLETED"))
        {
            alarm.setAlarm(context);
        }
    }
}
