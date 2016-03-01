package ph.com.gs3.loyaltystore.models.tasks;

import android.os.AsyncTask;
import android.util.Log;

import com.google.gson.Gson;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Arrays;
import java.util.List;

import ph.com.gs3.loyaltystore.LoyaltyStoreApplication;
import ph.com.gs3.loyaltystore.models.WifiDirectConnectivityState;
import ph.com.gs3.loyaltystore.models.sqlite.dao.Sales;
import ph.com.gs3.loyaltystore.models.sqlite.dao.SalesDao;

/**
 * Created by Ervinne Sodusta on 8/18/2015.
 */
public class SendPurchaseInfoForValidationTask extends AsyncTask<Void, Void, Void> {

    public static final String TAG = SendPurchaseInfoForValidationTask.class.getSimpleName();

    private int port;

    private String jsonStringPurchaseInfo;

    private SendPurchaseInfoForValidationTaskEventListener sendPurchaseInfoForValidationTaskEventListener;

    private String customerDeviceId;

    private boolean isFirstUse = false;

    public SendPurchaseInfoForValidationTask(int port,
                                             String jsonStringPurchaseInfo,
                                             SendPurchaseInfoForValidationTaskEventListener sendPurchaseInfoForValidationTaskEventListener) {
        this.port = port;
        this.jsonStringPurchaseInfo = jsonStringPurchaseInfo;
        this.sendPurchaseInfoForValidationTaskEventListener = sendPurchaseInfoForValidationTaskEventListener;
    }

    @Override
    protected Void doInBackground(Void... params) {

        WifiDirectConnectivityState connectivityState = WifiDirectConnectivityState.getInstance();

        ServerSocket serverSocket = null;
        Socket socket;

        try {
            if (connectivityState.isServer()) {
                serverSocket = new ServerSocket(port);
                socket = serverSocket.accept();
            } else {
                socket = new Socket();
                socket.connect(new InetSocketAddress(connectivityState.getGroupOwnerAddress(), port));
            }

            DataInputStream dataInputStream = new DataInputStream(socket.getInputStream());
            DataOutputStream dataOutputStream = new DataOutputStream(socket.getOutputStream());

            if (connectivityState.isServer()) {
                awaitClientReadyConfirmation(dataInputStream);
            }

            awaitClientId(dataInputStream);
            awaitClientTransactionHistory(dataInputStream);

            sendPurchaseInfo(dataOutputStream);

            dataOutputStream.close();
            dataInputStream.close();
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

        sendPurchaseInfoForValidationTaskEventListener.onPurchaseInfoSent(customerDeviceId,isFirstUse);
    }

    private void awaitClientReadyConfirmation(DataInputStream dataInputStream) throws IOException {

        String clientReadyConfirmation = dataInputStream.readUTF();
        Log.v(TAG, clientReadyConfirmation);

    }

    private void awaitClientId(DataInputStream dataInputStream) throws IOException {

        customerDeviceId = dataInputStream.readUTF();
        Log.d(TAG, "Customer Id : " + customerDeviceId);

    }

    private void awaitClientTransactionHistory(DataInputStream dataInputStream) throws IOException {

        String preMesssage = dataInputStream.readUTF();

        if("TRANSACTIONS".equals(preMesssage)){

            String transactionsJsonString = dataInputStream.readUTF();

            Log.d(TAG, "Transactions recieved : " + transactionsJsonString);

            Gson gson = new Gson();

            List<Sales> salesListFromCustomerDevice = Arrays.asList(
                    gson.fromJson(transactionsJsonString, Sales[].class)
            );

            SalesDao salesDao = LoyaltyStoreApplication.getSession().getSalesDao();

            List<Sales> salesListByCustomerDeviceId = salesDao.queryBuilder()
                    .where(SalesDao.Properties.Customer_id.eq(customerDeviceId))
                    .list();

            if(salesListFromCustomerDevice.size() <= 0 && salesListByCustomerDeviceId.size() <= 0){

                isFirstUse = true;

            }

        }

    }

    private void sendPurchaseInfo(DataOutputStream dataOutputStream) throws IOException {

        dataOutputStream.writeUTF("PURCHASE_INFO"); //  notify client that this is a purchase info
        dataOutputStream.writeUTF(jsonStringPurchaseInfo);
        dataOutputStream.flush();

    }

    public interface SendPurchaseInfoForValidationTaskEventListener {

        void onPurchaseInfoSent(String customerDeviceId, boolean isFirstUse);

    }

}
