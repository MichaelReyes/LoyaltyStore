package ph.com.gs3.loyaltystore.models.tasks;

import android.os.AsyncTask;
import android.util.Log;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ConnectException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import ph.com.gs3.loyaltystore.LoyaltyStoreApplication;
import ph.com.gs3.loyaltystore.models.WifiDirectConnectivityState;
import ph.com.gs3.loyaltystore.models.sqlite.dao.Product;
import ph.com.gs3.loyaltystore.models.sqlite.dao.Reward;
import ph.com.gs3.loyaltystore.models.sqlite.dao.Sales;
import ph.com.gs3.loyaltystore.models.sqlite.dao.SalesDao;

/**
 * Created by Ervinne Sodusta on 2/15/2016.
 */
public class SyncWithAgentTask extends AsyncTask<Void, SyncWithAgentTask.ProgressType, Void> {

    public static final String TAG = SyncWithAgentTask.class.getSimpleName();

    public enum ProgressType {
        PRODUCTS, REWARDS, SALES
    }

    private int port;
    private SyncWithAgentTaskListener listener;

    private SalesDao salesDao;

    private List<Product> synchedProducts = new ArrayList<>();
    private List<Reward> synchedRewards = new ArrayList<>();
    private List<Sales> synchedSales = new ArrayList<>();

    public SyncWithAgentTask(int port, SyncWithAgentTaskListener listener) {
        this.port = port;
        this.listener = listener;

        salesDao = LoyaltyStoreApplication.getSession().getSalesDao();
    }

    @Override
    protected Void doInBackground(Void... params) {
        Log.v(TAG, "Task Started");
        WifiDirectConnectivityState connectivityState = WifiDirectConnectivityState.getInstance();

        ServerSocket serverSocket = null;
        Socket socket;

        try {
            if (connectivityState.isServer()) {
                serverSocket = new ServerSocket(port);
                socket = serverSocket.accept();
            } else {
                socket = new Socket();

                try {
                    socket.connect(new InetSocketAddress(connectivityState.getGroupOwnerAddress(), port));
                } catch (ConnectException e) {
                    e.printStackTrace();
                    return null;
                }
            }

            DataInputStream dataInputStream = new DataInputStream(socket.getInputStream());
            DataOutputStream dataOutputStream = new DataOutputStream(socket.getOutputStream());

            if (!connectivityState.isServer()) {
                sendClientReadyConfirmation(dataOutputStream);
            }

            aqcuireProducts(dataInputStream);
            publishProgress(ProgressType.PRODUCTS);
            aqcuireRewards(dataInputStream);
            publishProgress(ProgressType.REWARDS);
            sendSales(dataOutputStream, dataInputStream);
            publishProgress(ProgressType.SALES);

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
    protected void onProgressUpdate(ProgressType... progressTypes) {
        super.onProgressUpdate(progressTypes);

        if (progressTypes[0] == ProgressType.PRODUCTS) {
            listener.onProductsAcquired(synchedProducts);
        } else if (progressTypes[0] == ProgressType.REWARDS) {
            listener.onRewardsAcquired(synchedRewards);
        } else if (progressTypes[0] == ProgressType.SALES) {
            listener.onSalesSent(synchedSales);
        }

    }

    private void sendClientReadyConfirmation(DataOutputStream dataOutputStream) throws IOException {

        dataOutputStream.writeUTF("CLIENT_READY");
        dataOutputStream.flush();

    }

    private void aqcuireProducts(DataInputStream dataInputStream) throws IOException {
        String preMessage = dataInputStream.readUTF();

        if ("PRODUCTS".equals(preMessage)) {
            String productsJsonString = dataInputStream.readUTF();
            Log.v(TAG, "Acquired Products: " + productsJsonString);
        }

    }

    private void aqcuireRewards(DataInputStream dataInputStream) throws IOException {
        String preMessage = dataInputStream.readUTF();

        if ("REWARDS".equals(preMessage)) {
            String rewardsJsonString = dataInputStream.readUTF();
            Log.v(TAG, "Acquired Rewards: " + rewardsJsonString);
        }
    }

    private void sendSales(DataOutputStream dataOutputStream, DataInputStream dataInputStream) throws IOException {

        String preMessage = dataInputStream.readUTF();

        if ("SALES".equals(preMessage)) {

        }

    }

    private List<Sales> getUnSynchedSales() {
        List<Sales> sales = salesDao.queryRaw("is_synched = false");

        for (Sales salesTransaction : sales) {
            Log.v(TAG, salesTransaction.getTransacion_date() + " " + salesTransaction.getAmount());
        }

        return null;
    }

    public interface SyncWithAgentTaskListener {

        void onProductsAcquired(List<Product> products);

        void onRewardsAcquired(List<Reward> rewards);

        void onSalesSent(List<Sales> sales);
    }

}
