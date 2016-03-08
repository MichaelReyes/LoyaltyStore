package ph.com.gs3.loyaltystore.models.tasks;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;

import ph.com.gs3.loyaltystore.LoyaltyStoreApplication;
import ph.com.gs3.loyaltystore.models.WifiDirectConnectivityState;
import ph.com.gs3.loyaltystore.models.sqlite.dao.Reward;
import ph.com.gs3.loyaltystore.models.sqlite.dao.RewardDao;
import ph.com.gs3.loyaltystore.models.sqlite.dao.SalesHasReward;
import ph.com.gs3.loyaltystore.models.sqlite.dao.SalesHasRewardDao;
import ph.com.gs3.loyaltystore.models.sqlite.dao.SalesProduct;
import ph.com.gs3.loyaltystore.models.sqlite.dao.SalesProductDao;
import ph.com.gs3.loyaltystore.models.values.Retailer;

/**
 * Created by Bryan-PC on 04/03/2016.
 */
public class SendPurchaseInfoAndRewardsTask extends AsyncTask<Void, SendPurchaseInfoAndRewardsTask.ProgressType, Void> {

    public static final String TAG = SendPurchaseInfoAndRewardsTask.class.getSimpleName();

    private Context context;
    private int port;
    private JSONObject jsonObjectSalesData;
    private SendPurchaseInfoAndRewardsTaskListener sendPurchaseInfoAndRewardsTaskListener;

    private String customerDeviceId;

    private RewardDao rewardDao;
    private SalesProductDao salesProductDao;
    private SalesHasRewardDao salesHasRewardDao;

    private List<Reward> rewards;

    public enum ProgressType {
        CUSTOMER_ID, SALES, REWARDS
    }

    public SendPurchaseInfoAndRewardsTask(Context context,
                                          int port,
                                          JSONObject jsonObjectSalesData,
                                          SendPurchaseInfoAndRewardsTaskListener sendPurchaseInfoAndRewardsTaskListener) {
        this.context = context;
        this.port = port;
        this.jsonObjectSalesData = jsonObjectSalesData;
        this.sendPurchaseInfoAndRewardsTaskListener = sendPurchaseInfoAndRewardsTaskListener;

        initializeDateAccessObjects();

    }

    private void initializeDateAccessObjects(){

        salesProductDao = LoyaltyStoreApplication.getSession().getSalesProductDao();
        salesHasRewardDao = LoyaltyStoreApplication.getSession().getSalesHasRewardDao();
        rewardDao = LoyaltyStoreApplication.getSession().getRewardDao();

    }

    @Override
    protected void onProgressUpdate(ProgressType... progressTypes) {
        super.onProgressUpdate(progressTypes);

        if (progressTypes[0] == ProgressType.CUSTOMER_ID) {
            //listener here
        } else if (progressTypes[0] == ProgressType.SALES) {

        } else if (progressTypes[0] == ProgressType.REWARDS) {

        }
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

            acquireCustomerId(dataInputStream);
            publishProgress(ProgressType.CUSTOMER_ID);
            sendSales(dataOutputStream,dataInputStream);
            publishProgress(ProgressType.SALES);
            sendRewards(dataOutputStream);
            publishProgress(ProgressType.REWARDS);


        }catch (IOException e){
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return null;
    }

    private void awaitClientReadyConfirmation(DataInputStream dataInputStream) throws IOException {

        String clientReadyConfirmation = dataInputStream.readUTF();
        Log.v(TAG, clientReadyConfirmation);

    }

    private void acquireCustomerId(DataInputStream dataInputStream) throws IOException {

        customerDeviceId = dataInputStream.readUTF();
        Log.d(TAG, "Customer Id : " + customerDeviceId);

    }
    private void sendSales(DataOutputStream dataOutputStream, DataInputStream dataInputStream) throws IOException, JSONException {

        String preMessage = dataInputStream.readUTF();
        Gson gson = new Gson();

        if ("SALES".equals(preMessage)) {

            Retailer retailer = Retailer.getDeviceRetailerFromSharedPreferences(context);
            JSONObject storeJSON = new JSONObject();
            try {

                storeJSON.put("id", retailer.getStoreId());
                storeJSON.put("device_id", retailer.getDeviceId());
                storeJSON.put("name", retailer.getStoreName());
                dataOutputStream.writeUTF(storeJSON.toString());

            } catch (JSONException e) {
                e.printStackTrace();
                dataOutputStream.writeUTF("SALES_END");
                return;
            }

            String salesTransactionNumber = jsonObjectSalesData.getString("transaction_number");

            dataOutputStream.writeUTF(jsonObjectSalesData.toString());

            String salesConfirmation = dataInputStream.readUTF();
            if ("SALES_RECEIVED".equals(salesConfirmation)) {

                List<SalesHasReward> salesRewardList =
                        salesHasRewardDao
                                .queryBuilder()
                                .where(
                                        SalesHasRewardDao.Properties.Sales_transaction_number.eq(salesTransactionNumber)
                                ).list();
                dataOutputStream.writeUTF(gson.toJson(salesRewardList));

            } else {
                Log.e(TAG, "Sales sync failed, expected SALES_RECEIVED");
            }

            String salesRewardsConfirmation = dataInputStream.readUTF();

            if ("SALES_REWARDS_RECEIVED".equals(salesRewardsConfirmation)) {
                List<SalesProduct> salesProductList =
                        salesProductDao
                                .queryBuilder()
                                .where(
                                        SalesProductDao.Properties.Sales_transaction_number.eq(salesTransactionNumber)
                                ).list();
                Log.e(TAG, salesProductList.size() + " products under sales: " + salesTransactionNumber + " will be sent");
                dataOutputStream.writeUTF(gson.toJson(salesProductList));
            } else {
                Log.e(TAG, "Sales sync failed, expected SALES_REWARDS_RECEIVED");
            }

            String salesProductsConfirmation = dataInputStream.readUTF();
            if ("SALES_PRODUCTS_RECEIVED".equals(salesProductsConfirmation)) {
                //Sales sent
            } else {
                Log.e(TAG, "Sales sync failed, expected SALES_PRODUCTS_RECEIVED");
            }

        }

        dataOutputStream.writeUTF("SALES_END");

    }

    private void sendRewards(DataOutputStream dataOutputStream) throws IOException {

        rewards = rewardDao.loadAll();
        Gson gson = new Gson();
        String json = gson.toJson(rewards);

        Log.v(TAG, json);

        dataOutputStream.writeUTF("REWARDS");
        dataOutputStream.writeUTF(json);

    }

    public interface SendPurchaseInfoAndRewardsTaskListener {

    }
}
