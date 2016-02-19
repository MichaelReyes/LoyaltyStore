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
import java.net.ConnectException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import de.greenrobot.dao.query.QueryBuilder;
import ph.com.gs3.loyaltystore.LoyaltyStoreApplication;
import ph.com.gs3.loyaltystore.models.WifiDirectConnectivityState;
import ph.com.gs3.loyaltystore.models.sqlite.dao.Product;
import ph.com.gs3.loyaltystore.models.sqlite.dao.Reward;
import ph.com.gs3.loyaltystore.models.sqlite.dao.Sales;
import ph.com.gs3.loyaltystore.models.sqlite.dao.SalesDao;
import ph.com.gs3.loyaltystore.models.sqlite.dao.SalesHasReward;
import ph.com.gs3.loyaltystore.models.sqlite.dao.SalesHasRewardDao;
import ph.com.gs3.loyaltystore.models.sqlite.dao.SalesProduct;
import ph.com.gs3.loyaltystore.models.sqlite.dao.SalesProductDao;
import ph.com.gs3.loyaltystore.models.values.Retailer;

/**
 * Created by Ervinne Sodusta on 2/15/2016.
 */
public class SyncWithAgentTask extends AsyncTask<Void, SyncWithAgentTask.ProgressType, Void> {

    public static final String TAG = SyncWithAgentTask.class.getSimpleName();

    public enum ProgressType {
        PRODUCTS, REWARDS, SALES
    }

    private Context context;
    private int port;
    private SyncWithAgentTaskListener listener;

    private SalesProductDao salesProductDao;
    private SalesHasRewardDao salesHasRewardDao;
    private SalesDao salesDao;

    private List<Product> synchedProducts = new ArrayList<>();
    private List<Reward> synchedRewards = new ArrayList<>();
    private List<Sales> synchedSales = new ArrayList<>();

    public SyncWithAgentTask(Context context, int port, SyncWithAgentTaskListener listener) {
        this.context = context;
        this.port = port;
        this.listener = listener;

        salesProductDao = LoyaltyStoreApplication.getSession().getSalesProductDao();
        salesHasRewardDao = LoyaltyStoreApplication.getSession().getSalesHasRewardDao();
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

            Gson gson = new Gson();

            Product[] products = gson.fromJson(productsJsonString, Product[].class);
            synchedProducts = Arrays.asList(products);

        }

    }

    private void aqcuireRewards(DataInputStream dataInputStream) throws IOException {
        String preMessage = dataInputStream.readUTF();

        if ("REWARDS".equals(preMessage)) {
            String rewardsJsonString = dataInputStream.readUTF();
            Log.v(TAG, "Acquired Rewards: " + rewardsJsonString);

            Gson gson = new Gson();

            Reward[] rewards = gson.fromJson(rewardsJsonString, Reward[].class);
            synchedRewards = Arrays.asList(rewards);
        }
    }

    private void sendSales(DataOutputStream dataOutputStream, DataInputStream dataInputStream) throws IOException {

        String preMessage = dataInputStream.readUTF();

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

            QueryBuilder qBuilder = salesDao.queryBuilder();
            qBuilder.whereOr(SalesDao.Properties.Is_synced.eq(false), SalesDao.Properties.Is_synced.isNull());
            List<Sales> sales = qBuilder.list();

            Gson gson = new Gson();

            for (Sales salesTransaction : sales) {
                //  Send sales header
                Log.v(TAG, salesTransaction.getId() + " " + salesTransaction.getTransaction_date()+ " " + salesTransaction.getAmount());
                dataOutputStream.writeUTF(gson.toJson(salesTransaction));

                String salesConfirmation = dataInputStream.readUTF();
                if ("SALES_RECEIVED".equals(salesConfirmation)) {
                    //  Send sales rewards
                    List<SalesHasReward> salesRewardList = salesHasRewardDao.queryBuilder().where(SalesHasRewardDao.Properties.Sales_transaction_number.eq(salesTransaction.getTransaction_number())).list();
                    dataOutputStream.writeUTF(gson.toJson(salesRewardList));
                } else {
                    Log.e(TAG, "Sales sync failed, expected SALES_RECEIVED");
                    break;
                }

                String salesRewardsConfirmation = dataInputStream.readUTF();

                if ("SALES_REWARDS_RECEIVED".equals(salesRewardsConfirmation)) {
                    List<SalesProduct> salesProductList = salesProductDao.queryBuilder().where(SalesProductDao.Properties.Sales_transaction_number.eq(salesTransaction.getTransaction_number())).list();
                    Log.e(TAG, salesProductList.size() + " products under sales: " + salesTransaction.getId() + " will be sent");
                    dataOutputStream.writeUTF(gson.toJson(salesProductList));
                } else {
                    Log.e(TAG, "Sales sync failed, expected SALES_REWARDS_RECEIVED");
                    break;
                }

                String salesProductsConfirmation = dataInputStream.readUTF();
                if ("SALES_PRODUCTS_RECEIVED".equals(salesProductsConfirmation)) {
                    salesTransaction.setIs_synced(true);
//                    salesDao.update(salesTransaction);
                } else {
                    Log.e(TAG, "Sales sync failed, expected SALES_PRODUCTS_RECEIVED");
                    break;
                }
            }
        }

        dataOutputStream.writeUTF("SALES_END");

    }

    public interface SyncWithAgentTaskListener {

        void onProductsAcquired(List<Product> products);

        void onRewardsAcquired(List<Reward> rewards);

        void onSalesSent(List<Sales> sales);
    }

}
