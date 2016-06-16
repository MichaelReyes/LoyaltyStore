package ph.com.gs3.loyaltystore.models.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import ph.com.gs3.loyaltystore.models.services.ServerSyncService;

/**
 * Created by Ervinne Sodusta on 2/8/2016.
 */
public class ServerSyncServiceBroadcastReceiver extends BroadcastReceiver {

    private EventListener eventListener;

    public ServerSyncServiceBroadcastReceiver(EventListener eventListener) {
        this.eventListener = eventListener;
    }

    @Override
    public void onReceive(Context context, Intent intent) {

        String action = intent.getAction();

        Log.d("RECIEVER", " RECIEVED ACTION : " + action);

        if (action == ServerSyncService.ACTION_NEED_AUTHENTICATION) {
            eventListener.onAuthenticationNeeded();
        } else if (action == ServerSyncService.ACTION_ERROR) {
            //  TODO: add message later
            eventListener.onError("Error");
        } else {
            int syncCount = intent.getExtras().getInt(ServerSyncService.EXTRA_SYNC_COUNT, 0);
            eventListener.onSyncDone(action, syncCount);
        }

    }

    public interface EventListener {

        void onAuthenticationNeeded();

        void onSyncDone(String actionName, int syncCount);

        void onError(String errorMessage);

    }

}