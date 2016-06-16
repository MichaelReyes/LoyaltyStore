package ph.com.gs3.loyaltystore.fragments;

import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import ph.com.gs3.loyaltystore.R;

/**
 * Created by Ervinne Sodusta on 2/10/2016.
 */
public abstract class SyncDataFragment extends Fragment {

    protected ProgressBar pbSyncProducts;
    protected ProgressBar pbSyncRewards;
    protected ProgressBar pbSyncSales;
    protected ProgressBar pbSyncInventory;
    protected ProgressBar pbSyncReturns;

    protected TextView tvSyncProductsLabel;
    protected TextView tvSyncRewardsLabel;
    protected TextView tvSyncSalesLabel;
    protected TextView tvSyncInventoryLabel;
    protected TextView tvSyncReturnsLabel;

    protected TextView tvSyncProductsResult;
    protected TextView tvSyncRewardsResult;
    protected TextView tvSyncSalesResult;
    protected TextView tvSyncInventoryResult;
    protected TextView tvSyncReturnResult;

    protected Button bSync;

    protected void initializeDataSyncViews(View rootView) {

        Log.d("SyncDataFragment","initializeDataSyncViews");

        pbSyncProducts = (ProgressBar) rootView.findViewById(R.id.SyncOnWeb_pbSyncProductsProgress);
        pbSyncRewards = (ProgressBar) rootView.findViewById(R.id.SyncOnWeb_pbSyncRewardsProgress);
        pbSyncSales = (ProgressBar) rootView.findViewById(R.id.SyncOnWeb_pbSyncSalesProgress);
        pbSyncInventory = (ProgressBar) rootView.findViewById(R.id.SyncOnWeb_pbSyncInventoryProgress);
        pbSyncReturns = (ProgressBar) rootView.findViewById(R.id.SyncOnWeb_pbSyncReturnsProgress);

        tvSyncProductsLabel = (TextView) rootView.findViewById(R.id.SyncOnWeb_tvSyncProductsLabel);
        tvSyncRewardsLabel = (TextView) rootView.findViewById(R.id.SyncOnWeb_tvSyncRewardsLabel);
        tvSyncSalesLabel = (TextView) rootView.findViewById(R.id.SyncOnWeb_tvSyncSalesLabel);
        tvSyncInventoryLabel = (TextView) rootView.findViewById(R.id.SyncOnWeb_tvSyncInventoryLabel);
        tvSyncReturnsLabel = (TextView) rootView.findViewById(R.id.SyncOnWeb_tvSyncReturnsLabel);

        tvSyncProductsResult = (TextView) rootView.findViewById(R.id.SyncOnWeb_tvSyncProductsResult);
        tvSyncRewardsResult = (TextView) rootView.findViewById(R.id.SyncOnWeb_tvSyncRewardsResult);
        tvSyncSalesResult = (TextView) rootView.findViewById(R.id.SyncOnWeb_tvSyncSalesResult);
        tvSyncInventoryResult = (TextView) rootView.findViewById(R.id.SyncOnWeb_tvSyncInventoryResult);
        tvSyncReturnResult = (TextView) rootView.findViewById(R.id.SyncOnWeb_tvSyncReturnsResult);

        bSync = (Button) rootView.findViewById(R.id.email_sign_in_button);

        tvSyncProductsResult.setText("Ready to sync");
        tvSyncRewardsResult.setText("Ready to sync");
        tvSyncSalesResult.setText("Ready to sync");
        tvSyncReturnResult.setText("Ready to sync");
        tvSyncInventoryResult.setText("Ready to sync");

    }

    public void enableSyncButton(boolean enable) {
        bSync.setEnabled(enable);
    }

    public void showSyncStarted() {

        tvSyncProductsResult.setText("Synchronizing");
        tvSyncRewardsResult.setText("Synchronizing");
        tvSyncSalesResult.setText("Synchronizing");
        tvSyncReturnResult.setText("Synchronizing");
        tvSyncInventoryResult.setText("Synchronizing");

        pbSyncProducts.setVisibility(View.VISIBLE);
        pbSyncRewards.setVisibility(View.VISIBLE);
        pbSyncSales.setVisibility(View.VISIBLE);
        pbSyncReturns.setVisibility(View.VISIBLE);
        pbSyncInventory.setVisibility(View.VISIBLE);
    }

    public void markSyncProductsDone(int syncedProductCount) {
        pbSyncProducts.setVisibility(View.GONE);
        if (syncedProductCount > 0) {
            tvSyncProductsResult.setText(syncedProductCount + " products synced");
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

    public void markSyncReturnsDone(int syncedReturnsCount){
        pbSyncReturns.setVisibility(View.GONE);
        if(syncedReturnsCount > 0){
            tvSyncReturnResult.setText(syncedReturnsCount + " for return record synced");
        }else{
            tvSyncReturnResult.setText("Done");
        }
    }

    public void markSyncInventoryDone(int syncedInventoryDone){
        pbSyncInventory.setVisibility(View.GONE);
        if(syncedInventoryDone > 0){
            tvSyncInventoryResult.setText(syncedInventoryDone + " inventory record synced");
        }else{
            tvSyncInventoryResult.setText("Done");
        }
    }

}
