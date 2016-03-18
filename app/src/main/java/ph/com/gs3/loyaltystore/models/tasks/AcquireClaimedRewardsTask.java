package ph.com.gs3.loyaltystore.models.tasks;

import android.os.AsyncTask;
import android.util.Log;

import com.google.gson.Gson;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ConnectException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import ph.com.gs3.loyaltystore.models.WifiDirectConnectivityState;
import ph.com.gs3.loyaltystore.models.sqlite.dao.SalesHasReward;

/**
 * Created by Bryan-PC on 17/03/2016.
 */
public class AcquireClaimedRewardsTask extends AsyncTask<Void,Void,Void> {

    public static final String TAG = AcquireClaimedRewardsTask.class.getSimpleName();

    private int port;

    private String storeName;
    private float amount;
    private int points;

    List<SalesHasReward> claimedRewardsList = new ArrayList<>();

    private AcquireClaimedRewardsTaskListener acquireClaimedRewardsTaskListener;

    public AcquireClaimedRewardsTask(int port, AcquireClaimedRewardsTaskListener acquireClaimedRewardsTaskListener) {
        this.port = port;
        this.acquireClaimedRewardsTaskListener = acquireClaimedRewardsTaskListener;
    }

    @Override
    protected Void doInBackground(Void... params) {

        WifiDirectConnectivityState connectivityState = WifiDirectConnectivityState.getInstance();

        ServerSocket serverSocket = null;
        Socket socket;

        try {
            if (connectivityState.isServer()) {
                /*serverSocket = new ServerSocket(port);
                serverSocket.setReuseAddress(true);*/
                serverSocket = new ServerSocket(); // <-- create an unbound socket first
                serverSocket.setReuseAddress(true);
                serverSocket.bind(new InetSocketAddress(port)); // <-- now bind it
                socket = serverSocket.accept();
            } else {
                socket = new Socket();

                try {
                    socket.connect(new InetSocketAddress(connectivityState.getGroupOwnerAddress(), port));
                }catch(ConnectException e){
                    e.printStackTrace();
                    return null;
                }
            }

            DataInputStream dataInputStream = new DataInputStream(socket.getInputStream());
            DataOutputStream dataOutputStream = new DataOutputStream(socket.getOutputStream());

            if (connectivityState.isServer()) {
                awaitClientReadyConfirmation(dataInputStream);
            }

            acquireClaimedRewards(dataInputStream,dataOutputStream);


            dataInputStream.close();
            dataOutputStream.close();
            socket.close();

            if (serverSocket != null) {
                serverSocket.close();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);

        Log.d(TAG, "AcquirePurchaseInfoTask ENDED");

        acquireClaimedRewardsTaskListener.onClaimedRewardsAcquired(claimedRewardsList);

    }

    private void awaitClientReadyConfirmation(DataInputStream dataInputStream) throws IOException {

        String clientReadyConfirmation = dataInputStream.readUTF();
        Log.v(TAG, clientReadyConfirmation);

    }


    private void acquireClaimedRewards(DataInputStream dataInputStream, DataOutputStream dataOutputStream) throws IOException {

        String preMessage = dataInputStream.readUTF();

        Gson gson = new Gson();

        if("CLAIMED_REWARDS".equals(preMessage)){

            String salesHasRewardJsonString = dataInputStream.readUTF();
            SalesHasReward[] salesHasRewards = gson.fromJson(salesHasRewardJsonString, SalesHasReward[].class);
            claimedRewardsList = Arrays.asList(salesHasRewards);

            dataOutputStream.writeUTF("CLAIMED_REWARDS_RECIEVED");

        }

    }

    public interface AcquireClaimedRewardsTaskListener {

        void onClaimedRewardsAcquired(List<SalesHasReward> claimedRewardsList);

    }

}
