package ph.com.gs3.loyaltystore.models.tasks;

import android.os.AsyncTask;
import android.util.Log;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;

import ph.com.gs3.loyaltystore.models.WifiDirectConnectivityState;

/**
 * Created by Ervinne Sodusta on 8/18/2015.
 */
public class SendUnsyncedSaleTransactionsTask extends AsyncTask<Void, Void, Void> {

    public static final String TAG = SendUnsyncedSaleTransactionsTask.class.getSimpleName();

    private int port;

    private String jsonStringUnsyncedSaleTransactions;

    private SendUnsyncedSaleTransactionsTaskEventListener sendUnsyncedSaleTransactionsTaskEventListener;

    public SendUnsyncedSaleTransactionsTask(int port,
                                            String jsonStringUnsyncedSaleTransactions,
                                            SendUnsyncedSaleTransactionsTaskEventListener sendUnsyncedSaleTransactionsTaskEventListener) {
        this.port = port;
        this.jsonStringUnsyncedSaleTransactions = jsonStringUnsyncedSaleTransactions;
        this.sendUnsyncedSaleTransactionsTaskEventListener = sendUnsyncedSaleTransactionsTaskEventListener;
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

        sendUnsyncedSaleTransactionsTaskEventListener.onTransactionsSent();
    }

    private void awaitClientReadyConfirmation(DataInputStream dataInputStream) throws IOException {

        String clientReadyConfirmation = dataInputStream.readUTF();
        Log.v(TAG, clientReadyConfirmation);

    }

    private void sendPurchaseInfo(DataOutputStream dataOutputStream) throws IOException {

        dataOutputStream.writeUTF("SALE_TRANSACTIONS"); //  notify client that this is a purchase info
        dataOutputStream.writeUTF(jsonStringUnsyncedSaleTransactions);
        dataOutputStream.flush();

    }

    public interface SendUnsyncedSaleTransactionsTaskEventListener {

        void onTransactionsSent();

    }

}
