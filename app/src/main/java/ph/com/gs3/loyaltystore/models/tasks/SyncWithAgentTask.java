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
import ph.com.gs3.loyaltystore.models.sqlite.dao.CashReturn;
import ph.com.gs3.loyaltystore.models.sqlite.dao.CashReturnDao;
import ph.com.gs3.loyaltystore.models.sqlite.dao.Expenses;
import ph.com.gs3.loyaltystore.models.sqlite.dao.ExpensesDao;
import ph.com.gs3.loyaltystore.models.sqlite.dao.ItemReturn;
import ph.com.gs3.loyaltystore.models.sqlite.dao.ItemReturnDao;
import ph.com.gs3.loyaltystore.models.sqlite.dao.ItemStockCount;
import ph.com.gs3.loyaltystore.models.sqlite.dao.ItemStockCountDao;
import ph.com.gs3.loyaltystore.models.sqlite.dao.Product;
import ph.com.gs3.loyaltystore.models.sqlite.dao.ProductBreakdown;
import ph.com.gs3.loyaltystore.models.sqlite.dao.ProductDelivery;
import ph.com.gs3.loyaltystore.models.sqlite.dao.ProductDeliveryDao;
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
public class SyncWithAgentTask extends AsyncTask<Void, SyncWithAgentTask.ProgressType, Boolean> {

    public static final String TAG = SyncWithAgentTask.class.getSimpleName();

    public enum ProgressType {
        PRODUCTS,
        PRODUCTS_BREAKDOWN,
        REWARDS,
        PRODUCT_DELIVERIES,
        SALES,
        ITEM_RETURN,
        CASH_RETURN,
        ITEM_STOCK_COUNT,
        EXPENSES
    }

    private Context context;
    private int port;
    private SyncWithAgentTaskListener listener;

    private SalesProductDao salesProductDao;
    private SalesHasRewardDao salesHasRewardDao;
    private SalesDao salesDao;
    private ItemReturnDao itemReturnDao;
    private ExpensesDao expensesDao;
    private CashReturnDao cashReturnDao;
    private ProductDeliveryDao productDeliveryDao;
    private ItemStockCountDao itemStockCountDao;

    private List<Product> syncedProducts = new ArrayList<>();
    private List<ProductBreakdown> syncedProductsBreakdown = new ArrayList<>();
    private List<Reward> syncedRewards = new ArrayList<>();
    private List<ProductDelivery> syncedProductDeliveries = new ArrayList<>();
    private List<Sales> syncedSales = new ArrayList<>();
    private List<ItemReturn> syncedItemReturns = new ArrayList<>();
    private List<ItemReturn> syncedProcessedItemReturns = new ArrayList<>();
    private List<CashReturn> syncedCashReturns = new ArrayList<>();
    private List<CashReturn> syncedProcessedCashReturns = new ArrayList<>();
    private List<ItemStockCount> syncedItemStockCount = new ArrayList<>();
    private List<Expenses> syncedExpenses = new ArrayList<>();

    public SyncWithAgentTask(Context context, int port, SyncWithAgentTaskListener listener) {
        this.context = context;
        this.port = port;
        this.listener = listener;

        salesProductDao = LoyaltyStoreApplication.getSession().getSalesProductDao();
        salesHasRewardDao = LoyaltyStoreApplication.getSession().getSalesHasRewardDao();
        salesDao = LoyaltyStoreApplication.getSession().getSalesDao();
        itemReturnDao = LoyaltyStoreApplication.getSession().getItemReturnDao();
        expensesDao = LoyaltyStoreApplication.getSession().getExpensesDao();
        cashReturnDao = LoyaltyStoreApplication.getSession().getCashReturnDao();
        productDeliveryDao = LoyaltyStoreApplication.getSession().getProductDeliveryDao();
        itemReturnDao = LoyaltyStoreApplication.getSession().getItemReturnDao();
        itemStockCountDao = LoyaltyStoreApplication.getSession().getItemStockCountDao();

        Log.d(TAG, "Instantiated");

    }

    @Override
    protected Boolean doInBackground(Void... params) {
        Log.v(TAG, "Task Started");
        WifiDirectConnectivityState connectivityState = WifiDirectConnectivityState.getInstance();

        ServerSocket serverSocket = null;
        Socket socket;

        try {
            if (connectivityState.isServer()) {
                /*serverSocket = new ServerSocket(port);
                socket = serverSocket.accept();*/

                serverSocket = new ServerSocket(); // <-- create an unbound socket first
                serverSocket.setReuseAddress(true);
                serverSocket.bind(new InetSocketAddress(port)); // <-- now bind it
                socket = serverSocket.accept();
            } else {
                socket = new Socket();

                try {
                    socket.connect(new InetSocketAddress(connectivityState.getGroupOwnerAddress(), port));
                } catch (ConnectException e) {
                    e.printStackTrace();
                    return false;
                }
            }

            DataInputStream dataInputStream = new DataInputStream(socket.getInputStream());
            DataOutputStream dataOutputStream = new DataOutputStream(socket.getOutputStream());

            if (!connectivityState.isServer()) {
                sendClientReadyConfirmation(dataOutputStream);
            }

            sendStoreInfo(dataOutputStream);

            aqcuireProducts(dataInputStream);
            publishProgress(ProgressType.PRODUCTS);
            aqcuireProductsBreakdown(dataInputStream);
            publishProgress(ProgressType.PRODUCTS_BREAKDOWN);
            aqcuireRewards(dataInputStream);
            publishProgress(ProgressType.REWARDS);
            sendAndAcquireProductDelivery(dataOutputStream, dataInputStream);
            publishProgress(ProgressType.PRODUCT_DELIVERIES);
            sendSales(dataOutputStream, dataInputStream);
            publishProgress(ProgressType.SALES);
            sendAndAcquireItemReturn(dataOutputStream, dataInputStream);
            publishProgress(ProgressType.ITEM_RETURN);
            sendAndAcquireCashReturn(dataOutputStream, dataInputStream);
            publishProgress(ProgressType.CASH_RETURN);
            sendItemStockCount(dataOutputStream,dataInputStream);
            publishProgress(ProgressType.ITEM_STOCK_COUNT);
            sendExpenses(dataOutputStream, dataInputStream);
            publishProgress(ProgressType.EXPENSES);

            dataInputStream.close();
            dataOutputStream.close();
            socket.close();

            if (serverSocket != null) {
                serverSocket.close();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        return true;
    }

    @Override
    protected void onPostExecute(Boolean aBoolean) {
        super.onPostExecute(aBoolean);

        if (!aBoolean) {
            listener.onSocketConnectFailed();
        } else {
            listener.onTaskDone();
        }


    }

    @Override
    protected void onProgressUpdate(ProgressType... progressTypes) {
        super.onProgressUpdate(progressTypes);

        if (progressTypes[0] == ProgressType.PRODUCTS) {
            listener.onProductsAcquired(syncedProducts);
        } else if (progressTypes[0] == ProgressType.PRODUCTS_BREAKDOWN) {
            listener.onProductsBreakdownAcquired(syncedProductsBreakdown);
        } else if (progressTypes[0] == ProgressType.REWARDS) {
            listener.onRewardsAcquired(syncedRewards);
        } else if (progressTypes[0] == ProgressType.PRODUCT_DELIVERIES) {
            listener.onProductDeliveriesAcquired(syncedProductDeliveries);
        } else if (progressTypes[0] == ProgressType.SALES) {
            listener.onSalesSent(syncedSales);
        } else if (progressTypes[0] == ProgressType.ITEM_RETURN) {
            listener.onItemReturnSentAndAcquiredProcessed(syncedItemReturns,syncedProcessedItemReturns);
        } else if (progressTypes[0] == ProgressType.CASH_RETURN) {
            listener.onCashReturnSentAndAcquiredProcessed(syncedCashReturns,syncedProcessedCashReturns);
        }else if (progressTypes[0] == ProgressType.ITEM_STOCK_COUNT) {
            listener.onItemStockCountSent(syncedItemStockCount);
        } else if (progressTypes[0] == ProgressType.EXPENSES) {
            listener.onExpensesSent(syncedExpenses);
        }

    }


    private void sendClientReadyConfirmation(DataOutputStream dataOutputStream) throws IOException {

        dataOutputStream.writeUTF("CLIENT_READY");
        dataOutputStream.flush();

    }

    private void sendStoreInfo(DataOutputStream dataOutputStream) throws IOException {
        Retailer retailer = Retailer.getDeviceRetailerFromSharedPreferences(context);
        JSONObject storeJSON = new JSONObject();
        try {
            storeJSON.put("ID", retailer.getStoreId());
            storeJSON.put("device_id", retailer.getDeviceId());
            storeJSON.put("name", retailer.getStoreName());
            dataOutputStream.writeUTF(storeJSON.toString());
        } catch (JSONException e) {
            e.printStackTrace();
            dataOutputStream.writeUTF("STORE_INFO_ERROR");
            return;
        }
    }

    private void aqcuireProducts(DataInputStream dataInputStream) throws IOException {
        String preMessage = dataInputStream.readUTF();

        if ("PRODUCTS".equals(preMessage)) {
            String productsJsonString = dataInputStream.readUTF();
            Log.v(TAG, "Acquired Products: " + productsJsonString);

            Gson gson = new Gson();

            Product[] products = gson.fromJson(productsJsonString, Product[].class);
            syncedProducts = Arrays.asList(products);

        }

    }

    private void aqcuireProductsBreakdown(DataInputStream dataInputStream) throws IOException {
        String preMessage = dataInputStream.readUTF();

        if ("PRODUCTS_BREAKDOWN".equals(preMessage)) {
            String productsBreakdownJsonString = dataInputStream.readUTF();
            Log.v(TAG, "Acquired Products Breakdown: " + productsBreakdownJsonString);

            Gson gson = new Gson();

            ProductBreakdown[] productsBreakdown = gson.fromJson(productsBreakdownJsonString, ProductBreakdown[].class);
            syncedProductsBreakdown = Arrays.asList(productsBreakdown);

        }

    }

    private void aqcuireRewards(DataInputStream dataInputStream) throws IOException {
        String preMessage = dataInputStream.readUTF();

        if ("REWARDS".equals(preMessage)) {
            String rewardsJsonString = dataInputStream.readUTF();
            Log.v(TAG, "Acquired Rewards: " + rewardsJsonString);

            Gson gson = new Gson();

            Reward[] rewards = gson.fromJson(rewardsJsonString, Reward[].class);
            syncedRewards = Arrays.asList(rewards);
        }
    }

    private void sendAndAcquireProductDelivery(DataOutputStream dataOutputStream, DataInputStream dataInputStream) throws IOException {

        String productDeliveryConfirmationPreMessage = dataInputStream.readUTF();

        if ("PRODUCT_DELIVERY_CONFIRMATION".equals(productDeliveryConfirmationPreMessage)) {

            QueryBuilder qBuilder = productDeliveryDao.queryBuilder();
            qBuilder.where(ProductDeliveryDao.Properties.Is_synced.eq(false));
            List<ProductDelivery> productDeliveryList = qBuilder.list();

            Gson gson = new Gson();

            for (ProductDelivery productDelivery : productDeliveryList) {

                Log.v(TAG, productDelivery.getId() +
                        " " + productDelivery.getName() +
                        " " + productDelivery.getQuantity() +
                        " " + productDelivery.getStatus());

                dataOutputStream.writeUTF(gson.toJson(productDelivery));

                String productDeliveryReceivedConfirmation = dataInputStream.readUTF();
                if ("PRODUCT_DELIVERY_CONFIRMATION_RECEIVED".equals(productDeliveryReceivedConfirmation)) {
                    productDelivery.setIs_synced(true);
                    productDeliveryDao.update(productDelivery);
                } else {
                    Log.e(TAG, "productDelivery sync failed, expected PRODUCT_DELIVERY_CONFIRMATION_RECEIVED");
                    break;
                }

            }
        }

        dataOutputStream.writeUTF("PRODUCT_DELIVERY_END");

        String productDeliveriesPreMessage = dataInputStream.readUTF();

        if ("PRODUCT_DELIVERIES".equals(productDeliveriesPreMessage)) {
            String productDeliveriesJsonString = dataInputStream.readUTF();
            Log.d(TAG, "Acquired Product Deliveries: " + productDeliveriesJsonString);

            Gson gson = new Gson();
            ProductDelivery[] productDeliveries = gson.fromJson(productDeliveriesJsonString, ProductDelivery[].class);
            syncedProductDeliveries = Arrays.asList(productDeliveries);

            for (ProductDelivery productDelivery : syncedProductDeliveries) {

                productDelivery.setIs_synced(false);

            }

        }

    }

    private void acquireProductDeliveries(DataInputStream dataInputStream) throws IOException {
        String preMessage = dataInputStream.readUTF();

        if ("PRODUCT_DELIVERIES".equals(preMessage)) {
            String productDeliveriesJsonString = dataInputStream.readUTF();
            Log.d(TAG, "Acquired Product Deliveries: " + productDeliveriesJsonString);

            Gson gson = new Gson();
            ProductDelivery[] productDeliveries = gson.fromJson(productDeliveriesJsonString, ProductDelivery[].class);
            syncedProductDeliveries = Arrays.asList(productDeliveries);

        }
    }

    private void sendSales(DataOutputStream dataOutputStream, DataInputStream dataInputStream) throws IOException {

        String preMessage = dataInputStream.readUTF();

        if ("SALES".equals(preMessage)) {

            /*Retailer retailer = Retailer.getDeviceRetailerFromSharedPreferences(context);
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
            }*/

            QueryBuilder qBuilder = salesDao.queryBuilder();
            qBuilder.whereOr(SalesDao.Properties.Is_synced.eq(false), SalesDao.Properties.Is_synced.isNull());
            List<Sales> sales = qBuilder.list();

            Gson gson = new Gson();

            for (Sales salesTransaction : sales) {
                //  Send sales header
                Log.v(TAG, salesTransaction.getId() + " " + salesTransaction.getTransaction_date() + " " + salesTransaction.getAmount());
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
                    salesDao.update(salesTransaction);
//                    salesDao.update(salesTransaction);
                } else {
                    Log.e(TAG, "Sales sync failed, expected SALES_PRODUCTS_RECEIVED");
                    break;
                }
            }
        }

        dataOutputStream.writeUTF("SALES_END");

    }

    private void sendAndAcquireItemReturn(DataOutputStream dataOutputStream, DataInputStream dataInputStream) throws IOException {

        String preMessage = dataInputStream.readUTF();

        Gson gson = new Gson();

        if("PROCESSED_ITEM_RETURNS_FOR_APPROVAL".equals(preMessage)){
            String processedItemReturnsForApprovalJsonString = dataInputStream.readUTF();

            Log.v(TAG, "Acquired Processed Item Returns for approval: " + processedItemReturnsForApprovalJsonString);

            ItemReturn[] itemReturns = gson.fromJson(processedItemReturnsForApprovalJsonString, ItemReturn[].class);
            syncedProcessedItemReturns = Arrays.asList(itemReturns);
        }

        dataOutputStream.writeUTF("PROCESSED_ITEM_RETURNS_FOR_APPROVAL_END");

        preMessage = dataInputStream.readUTF();


        if ("ITEM_RETURN".equals(preMessage)) {

            QueryBuilder qBuilder = itemReturnDao.queryBuilder();
            qBuilder.whereOr(ItemReturnDao.Properties.Is_synced.eq(false), ItemReturnDao.Properties.Is_synced.isNull());
            List<ItemReturn> itemReturns = qBuilder.list();

            for (ItemReturn itemReturn : itemReturns) {
                //  Send sales header
                Log.v(TAG, itemReturn.getId() + " " + itemReturn.getType() + " " + itemReturn.getQuantity());
                dataOutputStream.writeUTF(gson.toJson(itemReturn));

                String itemReturnConfirmation = dataInputStream.readUTF();
                if ("ITEM_RETURN_RECEIVED".equals(itemReturnConfirmation)) {
                    //  Send sales rewards
                    itemReturn.setIs_synced(true);
                    itemReturnDao.update(itemReturn);
                } else {
                    Log.e(TAG, "item return sync failed, expected ITEM_RETURN_RECEIVED");
                    break;
                }

            }

            dataOutputStream.writeUTF("ITEM_RETURN_END");
        }

    }

    private void sendAndAcquireCashReturn(DataOutputStream dataOutputStream, DataInputStream dataInputStream) throws IOException {

        String preMessage = dataInputStream.readUTF();

        Gson gson = new Gson();


        if("PROCESSED_CASH_RETURNS_FOR_APPROVAL".equals(preMessage)){
            String processedItemReturnsForApprovalJsonString = dataInputStream.readUTF();

            Log.v(TAG, "Acquired Processed Cash Returns for approval: " + processedItemReturnsForApprovalJsonString);

            ItemReturn[] itemReturns = gson.fromJson(processedItemReturnsForApprovalJsonString, ItemReturn[].class);
            syncedProcessedItemReturns = Arrays.asList(itemReturns);
        }

        dataOutputStream.writeUTF("PROCESSED_CASH_RETURNS_FOR_APPROVAL_END");

        preMessage = dataInputStream.readUTF();

        if ("CASH_RETURN".equals(preMessage)) {

            QueryBuilder qBuilder = cashReturnDao.queryBuilder();
            qBuilder.whereOr(CashReturnDao.Properties.Is_synced.eq(false), CashReturnDao.Properties.Is_synced.isNull());
            List<CashReturn> cashReturns = qBuilder.list();

            for (CashReturn cashReturn : cashReturns) {
                //  Send cash return header
                Log.v(TAG, cashReturn.getId() + " " + cashReturn.getType() + " " + cashReturn.getAmount() + " " + cashReturn.getRemarks());
                dataOutputStream.writeUTF(gson.toJson(cashReturn));

                String itemCashReturnConfirmation = dataInputStream.readUTF();
                if ("CASH_RETURN_RECEIVED".equals(itemCashReturnConfirmation)) {
                    //  Send sales rewards
                    cashReturn.setIs_synced(true);
                    cashReturnDao.update(cashReturn);
                } else {
                    Log.e(TAG, "item return sync failed, expected CASH_RETURN_RECEIVED");
                    break;
                }

            }

            dataOutputStream.writeUTF("CASH_RETURN_END");
        }

    }

    private void sendItemStockCount(DataOutputStream dataOutputStream, DataInputStream dataInputStream) throws IOException {

        Gson gson = new Gson();

        String preMessage =  dataInputStream.readUTF();

        if ("ITEM_STOCK_COUNT".equals(preMessage)) {

            QueryBuilder qBuilder = itemStockCountDao.queryBuilder();
            qBuilder.whereOr(ItemStockCountDao.Properties.Is_synced.eq(false), ItemStockCountDao.Properties.Is_synced.isNull());
            List<ItemStockCount> itemStockCountList = qBuilder.list();

            for (ItemStockCount itemStockCount : itemStockCountList) {
                //  Send sales header
                Log.v(TAG, itemStockCount.getId() + " " +
                        itemStockCount.getName() + " " +
                        itemStockCount.getExpectedQuantity() + " " +
                        itemStockCount.getQuantity()
                );
                dataOutputStream.writeUTF(gson.toJson(itemStockCount));

                String expensesConfirmation = dataInputStream.readUTF();
                if ("ITEM_STOCK_COUNT_RECEIVED".equals(expensesConfirmation)) {
                    //  Send sales rewards
                    itemStockCount.setIs_synced(true);
                    itemStockCountDao.update(itemStockCount);
                } else {
                    Log.e(TAG, "itemStockCount sync failed, expected ITEM_STOCK_COUNT_RECEIVED");
                    break;
                }

            }
        }

        dataOutputStream.writeUTF("ITEM_STOCK_COUNT_END");

    }

    private void sendExpenses(DataOutputStream dataOutputStream, DataInputStream dataInputStream) throws IOException {

        Gson gson = new Gson();

        String preMessage =  dataInputStream.readUTF();

        if ("EXPENSES".equals(preMessage)) {

            QueryBuilder qBuilder = expensesDao.queryBuilder();
            qBuilder.whereOr(ExpensesDao.Properties.Is_synced.eq(false), ExpensesDao.Properties.Is_synced.isNull());
            List<Expenses> expensesList = qBuilder.list();

            for (Expenses expenses : expensesList) {
                //  Send sales header
                Log.v(TAG, expenses.getId() + " " + expenses.getDescription() + " " + expenses.getAmount());
                dataOutputStream.writeUTF(gson.toJson(expenses));

                String expensesConfirmation = dataInputStream.readUTF();
                if ("EXPENSES_RECEIVED".equals(expensesConfirmation)) {
                    //  Send sales rewards
                    expenses.setIs_synced(true);
                    expensesDao.update(expenses);
                } else {
                    Log.e(TAG, "expenses sync failed, expected EXPENSES_RECEIVED");
                    break;
                }

            }
        }

        dataOutputStream.writeUTF("EXPENSES_END");

    }

    public interface SyncWithAgentTaskListener {

        void onProductsAcquired(List<Product> products);

        void onProductsBreakdownAcquired(List<ProductBreakdown> productBreakdownList);

        void onRewardsAcquired(List<Reward> rewards);

        void onProductDeliveriesAcquired(List<ProductDelivery> productDeliveries);

        void onSalesSent(List<Sales> sales);

        void onItemReturnSentAndAcquiredProcessed(List<ItemReturn> itemReturns, List<ItemReturn> processedItemReturns);

        void onCashReturnSentAndAcquiredProcessed(List<CashReturn> cashReturns, List<CashReturn> processedCashReturns);

        void onItemStockCountSent(List<ItemStockCount> itemStockCountList);

        void onExpensesSent(List<Expenses> expensesList);

        void onTaskDone();

        void onSocketConnectFailed();

    }

}
