package ph.com.gs3.loyaltystore.models.tasks;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import ph.com.gs3.loyaltystore.models.values.DeviceInfo;
import ph.com.gs3.loyaltystore.presenters.WifiDirectConnectivityDataPresenter;


/**
 * Created by Michael Reyes on 02/23/2016.
 */
public class SearchAgentTask extends AsyncTask<Void, Void, Void> {

    public static final String TAG = SearchAgentTask.class.getSimpleName();

    private Context context;

    private boolean cancelled = false;

    private WifiDirectConnectivityDataPresenter wifiDirectConnectivityDataPresenter;

    public SearchAgentTask(Context context, WifiDirectConnectivityDataPresenter wifiDirectConnectivityDataPresenter) {
        this.context = context;
        this.wifiDirectConnectivityDataPresenter = wifiDirectConnectivityDataPresenter;
    }

    @Override
    protected void onCancelled() {
        super.onCancelled();
        cancelled = true;
        Log.d(TAG, "CANCELLED : " + cancelled);
    }

    @Override
    protected Void doInBackground(Void... params) {

        Log.d(TAG, "TASK STARTED");

        Log.d(TAG, " CANCELLED 1 : " + isCancelled());
        Log.d(TAG, " CANCELLED 2 : " + cancelled);

        while (!isCancelled() && !cancelled) {

            Log.d(TAG, "AGENT FINDER TASK DISCOVERING PEERS");

            wifiDirectConnectivityDataPresenter.discoverPeers(DeviceInfo.Type.AGENT);

            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        }

        return null;

    }

}
