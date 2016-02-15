package ph.com.gs3.loyaltystore.models.tasks;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;

import ph.com.gs3.loyaltystore.models.WifiDirectConnectivityState;
import ph.com.gs3.loyaltystore.models.values.Retailer;


/**
 * Created by Ervinne Sodusta on 10/20/2015.
 */
public class SendAdvertisementTask extends AsyncTask<Void, Void, Void> {

    public static final String TAG = SendAdvertisementTask.class.getSimpleName();

    private Context context;
    private int port;
    private Retailer retailer;
    private SendAdvertisementTaskListener sendAdvertisementTaskListener;


    public SendAdvertisementTask(Context context, int port, Retailer retailer,
                                 SendAdvertisementTaskListener sendAdvertisementTaskListener) {
        this.context = context;
        this.port = port;
        this.retailer = retailer;
        this.sendAdvertisementTaskListener = sendAdvertisementTaskListener;
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

            sendAdvertisment(dataOutputStream);

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
        sendAdvertisementTaskListener.onFinish();
    }

    private void awaitClientReadyConfirmation(DataInputStream dataInputStream) throws IOException {

        String clientReadyConfirmation = dataInputStream.readUTF();
        Log.v(TAG, clientReadyConfirmation);

    }

    private void sendAdvertisment(DataOutputStream dataOutputStream) throws IOException {

        dataOutputStream.writeUTF("ADVERTISEMENT"); //  notify client that` this is an advertisement
        dataOutputStream.writeUTF(retailer.getStoreName());
        dataOutputStream.writeUTF(retailer.getAdvertisment());
        dataOutputStream.flush();

    }

    /*private InetAddress getBroadcastAddress() throws IOException {
        WifiManager wifi = (WifiManager) context.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        DhcpInfo dhcp = wifi.getDhcpInfo();
        // handle null somehowx

        int broadcast = (dhcp.ipAddress & dhcp.netmask) | ~dhcp.netmask;
        byte[] quads = new byte[4];
        for (int k = 0; k < 4; k++)
            quads[k] = (byte) ((broadcast >> k * 8) & 0xFF);
        return InetAddress.getByAddress(quads);
    }*/

    public interface SendAdvertisementTaskListener {

        void onFinish();

    }

}
