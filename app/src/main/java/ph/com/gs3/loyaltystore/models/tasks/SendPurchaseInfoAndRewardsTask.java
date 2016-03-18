package ph.com.gs3.loyaltystore.models.tasks;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.google.gson.Gson;

import org.json.JSONArray;
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
import ph.com.gs3.loyaltystore.models.sqlite.dao.Product;
import ph.com.gs3.loyaltystore.models.sqlite.dao.ProductDao;
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
    private boolean isFirstUse = false;

    private ProductDao productDao;
    private RewardDao rewardDao;
    private SalesProductDao salesProductDao;
    private SalesHasRewardDao salesHasRewardDao;

    private WifiDirectConnectivityState connectivityState;

    private List<Reward> rewards;

    public enum ProgressType {
        CUSTOMER_ID, CUSTOMER_TRANSACTION_RECORD_COUNT, SALES, REWARDS
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

    private void initializeDateAccessObjects() {

        salesProductDao = LoyaltyStoreApplication.getSession().getSalesProductDao();
        salesHasRewardDao = LoyaltyStoreApplication.getSession().getSalesHasRewardDao();
        rewardDao = LoyaltyStoreApplication.getSession().getRewardDao();
        productDao = LoyaltyStoreApplication.getSession().getProductDao();

    }

    @Override
    protected void onProgressUpdate(ProgressType... progressTypes) {
        super.onProgressUpdate(progressTypes);

        if (progressTypes[0] == ProgressType.CUSTOMER_ID) {
            sendPurchaseInfoAndRewardsTaskListener.onCustomerIdAcquired(customerDeviceId);
        } else if (progressTypes[0] == ProgressType.CUSTOMER_TRANSACTION_RECORD_COUNT) {
            sendPurchaseInfoAndRewardsTaskListener.onCustomerTransactionRecordsAcquired(isFirstUse);
        } else if (progressTypes[0] == ProgressType.SALES) {
            sendPurchaseInfoAndRewardsTaskListener.onSalesSent();
        } else if (progressTypes[0] == ProgressType.REWARDS) {
            sendPurchaseInfoAndRewardsTaskListener.onRewardsSent();
        }
    }

    @Override
    protected Void doInBackground(Void... params) {

        Log.d(TAG, "SendPurchaseInfoAndRewardsTask STARTED!");

        connectivityState = WifiDirectConnectivityState.getInstance();

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
                socket.connect(new InetSocketAddress(connectivityState.getGroupOwnerAddress(), port));
            }

            DataInputStream dataInputStream = new DataInputStream(socket.getInputStream());
            DataOutputStream dataOutputStream = new DataOutputStream(socket.getOutputStream());

            if (connectivityState.isServer()) {
                awaitClientReadyConfirmation(dataInputStream);
            }

            acquireCustomerId(dataInputStream);
            publishProgress(ProgressType.CUSTOMER_ID);
            acquireCustomerTransactionRecordCount(dataInputStream);
            publishProgress(ProgressType.CUSTOMER_TRANSACTION_RECORD_COUNT);
            sendSales(dataOutputStream, dataInputStream);
            publishProgress(ProgressType.SALES);
            sendRewards(dataOutputStream,dataInputStream);
            publishProgress(ProgressType.REWARDS);


        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }

        return null;
    }

    private void awaitClientReadyConfirmation(DataInputStream dataInputStream) throws IOException {

        String clientReadyConfirmation = dataInputStream.readUTF();
        Log.v(TAG, clientReadyConfirmation);

    }

    private void acquireCustomerId(DataInputStream dataInputStream) throws IOException {

        String preMessage = dataInputStream.readUTF();

        if ("CUSTOMER_ID".equals(preMessage)) {
            customerDeviceId = dataInputStream.readUTF();
            Log.d(TAG, "Customer Id : " + customerDeviceId);
        }

    }

    private void acquireCustomerTransactionRecordCount(DataInputStream dataInputStream) throws IOException {

        String preMessage = dataInputStream.readUTF();

        if ("CUSTOMER_TRANSACTION_RECORD_COUNT".equals(preMessage)) {
            int customerTransactionRecordCount = Integer.valueOf(dataInputStream.readUTF());
            if (customerTransactionRecordCount <= 0) {
                isFirstUse = true;
            }
        }


    }

    private void sendSales(DataOutputStream dataOutputStream, DataInputStream dataInputStream) throws IOException, JSONException {

        String preMessage = dataInputStream.readUTF();
        Gson gson = new Gson();

        Log.d(TAG, "PRE MESSAGE : " + preMessage);

        if ("SALES".equals(preMessage)) {

            Retailer retailer = Retailer.getDeviceRetailerFromSharedPreferences(context);
            JSONObject storeJSON = new JSONObject();
            try {

                storeJSON.put("id", retailer.getStoreId());
                storeJSON.put("device_id", retailer.getDeviceId());
                storeJSON.put("name", retailer.getStoreName());
                storeJSON.put("mac_address", connectivityState.getCurrentDeviceAddress());
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

                if(isFirstUse){

                    List<Reward> rewardList =
                            rewardDao
                                    .queryBuilder()
                                    .where(
                                            RewardDao.Properties.Reward_condition.eq("first_use")
                                    ).list();

                    for(Reward reward : rewardList){
                        SalesHasReward salesHasReward = new SalesHasReward();
                        salesHasReward.setSales_transaction_number(salesTransactionNumber);
                        salesHasReward.setReward_id(reward.getId());

                        salesRewardList.add(salesHasReward);
                        salesHasRewardDao.insert(salesHasReward);
                    }

                }

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

                JSONArray jsonArray = new JSONArray();
                JSONObject jsonObject;

                for (SalesProduct salesProduct : salesProductList) {

                    jsonObject = new JSONObject();
                    jsonObject.put("id", salesProduct.getId());
                    jsonObject.put("product_id", salesProduct.getProduct_id());
                    jsonObject.put("quantity", salesProduct.getQuantity());
                    jsonObject.put("sale_type", salesProduct.getSale_type());
                    jsonObject.put("sales_transaction_number", salesProduct.getSales_transaction_number());
                    jsonObject.put("sub_total", salesProduct.getSub_total());

                    List<Product> products =
                            productDao
                                    .queryBuilder()
                                    .where(
                                            ProductDao.Properties.Id.eq(
                                                    salesProduct.getProduct_id()
                                            )
                                    )
                                    .list();

                    for (Product product : products) {
                        jsonObject.put("product_name", product.getName());
                        jsonObject.put("unit_cost", product.getUnit_cost());
                        jsonObject.put("sku", product.getSku());
                    }

                    jsonArray.put(jsonObject);

                }

                Log.e(TAG, salesProductList.size() + " products under sales: " + salesTransactionNumber + " will be sent");
                //dataOutputStream.writeUTF(gson.toJson(salesProductList));
                dataOutputStream.writeUTF(jsonArray.toString());
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

    private void sendRewards(DataOutputStream dataOutputStream, DataInputStream dataInputStream) throws IOException {

        rewards = rewardDao.loadAll();
        Gson gson = new Gson();
        String json = gson.toJson(rewards);

        Log.v(TAG, json);

        dataOutputStream.writeUTF("REWARDS");
        dataOutputStream.writeUTF(json);

        String confirmationMessage = dataInputStream.readUTF();

        Log.d(TAG, "REWARDS RECIEVED CONFIRMATION MESSAGE : " + confirmationMessage);

    }

    public interface SendPurchaseInfoAndRewardsTaskListener {

        void onCustomerIdAcquired(String customerDeviceId);

        void onCustomerTransactionRecordsAcquired(boolean isFirstUse);

        void onSalesSent();

        void onRewardsSent();

    }
}
