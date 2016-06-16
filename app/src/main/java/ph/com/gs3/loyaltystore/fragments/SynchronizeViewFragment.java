package ph.com.gs3.loyaltystore.fragments;

import android.app.Activity;
import android.content.Context;
import android.net.wifi.p2p.WifiP2pDevice;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import ph.com.gs3.loyaltystore.R;
import ph.com.gs3.loyaltystore.adapters.AgentDeviceListAdapter;

/**
 * Created by Bryan-PC on 24/04/2016.
 */
public class SynchronizeViewFragment extends Fragment {

    public static final String TAG = SynchronizeViewFragment.class.getSimpleName();

    private Context context;
    private FragmentActivity activity;

    private SynchronizeViewFragmentListener listener;

    private TextView tvConnectivity;

    protected ProgressBar pbSyncProducts;
    protected ProgressBar pbSyncRewards;
    protected ProgressBar pbSyncSales;
    protected ProgressBar pbSyncDeliveries;

    protected TextView tvSyncProductsLabel;
    protected TextView tvSyncRewardsLabel;
    protected TextView tvSyncSalesLabel;
    protected TextView tvSyncDeliveriesLabel;

    protected TextView tvSyncProductsResult;
    protected TextView tvSyncRewardsResult;
    protected TextView tvSyncSalesResult;
    protected TextView tvSyncDeliveriesResult;

    private Button bSync;
    private Button bClose;

    private ListView lvAgentDevice;
    private List<WifiP2pDevice> agentDeviceList;

    private AgentDeviceListAdapter adapter;

    private View v;

    public SynchronizeViewFragment createInstance() {
        SynchronizeViewFragment synchronizeViewFragment = new SynchronizeViewFragment();
        return synchronizeViewFragment;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;
        this.activity = (FragmentActivity) context;

        if (context instanceof SynchronizeViewFragmentListener) {
            listener = (SynchronizeViewFragmentListener) context;
        } else {
            throw new RuntimeException(context.getClass().getSimpleName() + " must implement SynchronizeViewFragmentListener");
        }

    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        this.context = activity;
        this.activity = (FragmentActivity) activity;

        if (context instanceof SynchronizeViewFragmentListener) {
            listener = (SynchronizeViewFragmentListener) activity;
        } else {
            throw new RuntimeException(context.getClass().getSimpleName() + " must implement SynchronizeViewFragmentListener");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.activity_sync_with_agent,container,false);
        v = rootView;

        initializeViews();

        listener.onSynchronizeViewReady();

        return rootView;
    }

    private void initializeViews(){

        agentDeviceList = new ArrayList<>();

        adapter = new AgentDeviceListAdapter(context, agentDeviceList);

        lvAgentDevice = (ListView) v.findViewById(R.id.Sync_lvDeviceList);
        lvAgentDevice.setAdapter(adapter);

        tvConnectivity = (TextView) v.findViewById(R.id.Sync_tvConnectivity);

        pbSyncProducts = (ProgressBar) v.findViewById(R.id.Sync_pbSyncProductsProgress);
        pbSyncRewards = (ProgressBar) v.findViewById(R.id.Sync_pbSyncRewardsProgress);
        pbSyncSales = (ProgressBar) v.findViewById(R.id.Sync_pbSyncSalesProgress);
        pbSyncDeliveries = (ProgressBar) v.findViewById(R.id.Sync_pbSyncDeliveriesProgress);

        tvSyncProductsLabel = (TextView) v.findViewById(R.id.Sync_tvSyncProductsLabel);
        tvSyncRewardsLabel = (TextView) v.findViewById(R.id.Sync_tvSyncRewardsLabel);
        tvSyncSalesLabel = (TextView) v.findViewById(R.id.Sync_tvSyncSalesLabel);
        tvSyncDeliveriesLabel = (TextView) v.findViewById(R.id.Sync_tvSyncDeliveriesLabel);

        tvSyncProductsResult = (TextView) v.findViewById(R.id.Sync_tvSyncProductsResult);
        tvSyncRewardsResult = (TextView) v.findViewById(R.id.Sync_tvSyncRewardsResult);
        tvSyncSalesResult = (TextView) v.findViewById(R.id.Sync_tvSyncSalesResult);
        tvSyncDeliveriesResult = (TextView) v.findViewById(R.id.Sync_tvSyncDeliveriesResult);

        bSync = (Button) v.findViewById(R.id.Sync_bSync);
        bSync.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onSync();
            }
        });

        bClose = (Button) v.findViewById(R.id.Sync_bClose);
        bClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onClose();
            }
        });

    }

    public void setAgentDeviceList(List<WifiP2pDevice> agentDeviceList){

        if(adapter!=null)
            adapter.setAgentDeviceList(agentDeviceList);

    }

    public void setConnectivity(String connectivity){
        tvConnectivity.setText(connectivity);
    }

    public void showSnycStarted(){
        tvSyncProductsResult.setText("Synchronizing");
        tvSyncRewardsResult.setText("Synchronizing");
        tvSyncSalesResult.setText("Synchronizing");
        tvSyncDeliveriesResult.setText("Synchronizing");

        pbSyncProducts.setVisibility(View.VISIBLE);
        pbSyncRewards.setVisibility(View.VISIBLE);
        pbSyncSales.setVisibility(View.VISIBLE);
        pbSyncDeliveries.setVisibility(View.VISIBLE);
    }

    public List<WifiP2pDevice> getAgentDeviceList(){
        return adapter.getAgentDeviceList();
    }

    public void markSyncProductsDone(int synchedProductCount) {
        pbSyncProducts.setVisibility(View.GONE);
        if (synchedProductCount > 0) {
            tvSyncProductsResult.setText(synchedProductCount + " products synced");
        } else {
            tvSyncProductsResult.setText("Done");
        }
    }

    public void markSyncRewardsDone(int syncedRewardsCount) {
        pbSyncRewards.setVisibility(View.GONE);
        if (syncedRewardsCount > 0) {
            tvSyncRewardsResult.setText(syncedRewardsCount + " rewards synced");
        } else {
            tvSyncRewardsResult.setText("Done");
        }
    }

    public void markSyncSalesDone(int syncedSalesCount) {
        pbSyncSales.setVisibility(View.GONE);
        if (syncedSalesCount > 0) {
            tvSyncSalesResult.setText(syncedSalesCount + " sales transactions synced");
        } else {
            tvSyncSalesResult.setText("Done");
        }
    }

    public void markSyncDeliveriesDone(int syncedDeliveriesCount) {
        pbSyncDeliveries.setVisibility(View.GONE);
        if (syncedDeliveriesCount > 0) {
            tvSyncDeliveriesResult.setText(syncedDeliveriesCount + " deliveries synced");
        } else {
            tvSyncDeliveriesResult.setText("Done");
        }
    }

    public void setSyncButtonVisibility(int visibility){
        bSync.setVisibility(View.VISIBLE);
    }

    public void setCloseButtonVisibility(int visibility){
        bClose.setVisibility(View.GONE);
    }

    public interface SynchronizeViewFragmentListener {

        void onSynchronizeViewReady();

        void onSync();

        void onClose();

    }
}
