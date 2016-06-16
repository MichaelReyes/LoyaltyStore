package ph.com.gs3.loyaltystore.fragments;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ToggleButton;

import ph.com.gs3.loyaltystore.R;
import ph.com.gs3.loyaltystore.models.TabMaintenance;

/**
 * Created by Bryan-PC on 28/04/2016.
 */
public class TabMaintenanceSettingsFragment extends Fragment implements CompoundButton.OnCheckedChangeListener {

    public static final String TAG = TabMaintenanceSettingsFragment.class.getSimpleName();

    private Context context;
    private FragmentActivity activity;

    public ToggleButton tglInvoice, tglInvoiceMenuAsButtons, tglInvoiceMenuAsList;
    public ToggleButton tglSynchronize, tglSynchronizeByWeb, tglSynchronizeByDriver;
    public ToggleButton tglInventory, tglInventoryOfSales, tglInventoryOfStocks,
            tglInventoryItemStockCount, tglInventoryReturnsToCommissary, tglInventoryExpenses;
    public ToggleButton tglSales;
    public ToggleButton tglDelivery, tglDeliveryHistory, tglDeliveriesForConfirmation;

    private Button bSave;

    private TabMaintenanceSettingsFragmentListener listener;

    public TabMaintenanceSettingsFragment createInstance() {

        TabMaintenanceSettingsFragment tabMaintenanceSettingsFragment = new TabMaintenanceSettingsFragment();
        return tabMaintenanceSettingsFragment;

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;
        this.activity = (FragmentActivity) context;

        if (context instanceof TabMaintenanceSettingsFragmentListener) {
            listener = (TabMaintenanceSettingsFragmentListener) context;
        } else {
            throw new RuntimeException(
                    getContext().getClass().getSimpleName() +
                            " must implement TabMaintenanceSettingsFragmentListener"
            );
        }

    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        this.activity = (FragmentActivity) activity;
        this.context = activity;

        if (context instanceof TabMaintenanceSettingsFragmentListener) {
            listener = (TabMaintenanceSettingsFragmentListener) activity;
        } else {
            throw new RuntimeException(
                    getContext().getClass().getSimpleName() +
                            " must implement TabMaintenanceSettingsFragmentListener"
            );
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_settings_tab_maintenance, container, false);

        TabMaintenance tabMaintenance = TabMaintenance.getTabMaintenanceFromSharedPreferences(activity);

        tglInvoice = (ToggleButton) rootView.findViewById(R.id.Settings_tglInvoice);
        tglInvoice.setChecked(tabMaintenance.isInvoiceTabActive());
        tglInvoice.setOnCheckedChangeListener(this);
        tglInvoiceMenuAsButtons = (ToggleButton) rootView.findViewById(R.id.Settings_tglInvoice_MenuAsButtons);
        tglInvoiceMenuAsButtons.setChecked(tabMaintenance.isInvoiceMenuAsButtonsTabActive());
        tglInvoiceMenuAsButtons.setOnCheckedChangeListener(this);
        tglInvoiceMenuAsList = (ToggleButton) rootView.findViewById(R.id.Settings_tglInvoice_MenuAsList);
        tglInvoiceMenuAsList.setChecked(tabMaintenance.isInvoiceMenuAsListTabActive());
        tglInvoiceMenuAsList.setOnCheckedChangeListener(this);

        tglSynchronize = (ToggleButton) rootView.findViewById(R.id.Settings_tglSynchronize);
        tglSynchronize.setChecked(tabMaintenance.isSynchronizeTabActive());
        tglSynchronize.setOnCheckedChangeListener(this);
        tglSynchronizeByDriver = (ToggleButton) rootView.findViewById(R.id.Settings_tglSynchronize_SynchronizeWifiDirect);
        tglSynchronizeByDriver.setChecked(tabMaintenance.isSynchronizeByDriverTabActive());
        tglSynchronizeByDriver.setOnCheckedChangeListener(this);
        tglSynchronizeByWeb = (ToggleButton) rootView.findViewById(R.id.Settings_tglSynchronize_SynchronizeWeb);
        tglSynchronizeByWeb.setChecked(tabMaintenance.isSynchronizeByWebTabActive());
        if (tabMaintenance.isSynchronizeByWebTabActive() && tabMaintenance.isSynchronizeByDriverTabActive())
            tglSynchronizeByDriver.setChecked(false);
        tglSynchronizeByWeb.setOnCheckedChangeListener(this);


        tglInventory = (ToggleButton) rootView.findViewById(R.id.Settings_tglInventory);
        tglInventory.setChecked(tabMaintenance.isInventoryTabActive());
        tglInventory.setOnCheckedChangeListener(this);
        tglInventoryOfSales = (ToggleButton) rootView.findViewById(R.id.Settings_tglInventory_InventoryOfSales);
        tglInventoryOfSales.setChecked(tabMaintenance.isInventoryOfSalesTabActive());
        tglInventoryOfSales.setOnCheckedChangeListener(this);
        tglInventoryOfStocks = (ToggleButton) rootView.findViewById(R.id.Settings_tglInventory_InventoryOfStocks);
        tglInventoryOfStocks.setChecked(tabMaintenance.isInventoryOfStocksTabActive());
        tglInventoryOfStocks.setOnCheckedChangeListener(this);
        tglInventoryItemStockCount = (ToggleButton) rootView.findViewById(R.id.Settings_tglInventory_ItemStockCount);
        tglInventoryItemStockCount.setChecked(tabMaintenance.isInventoryItemStockCountTabActive());
        tglInventoryItemStockCount.setOnCheckedChangeListener(this);
        tglInventoryReturnsToCommissary = (ToggleButton) rootView.findViewById(R.id.Settings_tglInventory_ReturnsToCommissary);
        tglInventoryReturnsToCommissary.setChecked(tabMaintenance.isInventoryReturnsToCommissaryTabActive());
        tglInventoryReturnsToCommissary.setOnCheckedChangeListener(this);
        tglInventoryExpenses = (ToggleButton) rootView.findViewById(R.id.Settings_tglInventory_Expenses);
        tglInventoryExpenses.setChecked(tabMaintenance.isInventoryExpensesTabActive());
        tglInventoryExpenses.setOnCheckedChangeListener(this);

        tglSales = (ToggleButton) rootView.findViewById(R.id.Settings_tglSales);
        tglSales.setChecked(tabMaintenance.isSalesTabActive());
        tglSales.setOnCheckedChangeListener(this);

        tglDelivery = (ToggleButton) rootView.findViewById(R.id.Settings_tglDelivery);
        tglDelivery.setChecked(tabMaintenance.isDeliveryTabActive());
        tglDelivery.setOnCheckedChangeListener(this);
        tglDeliveryHistory = (ToggleButton) rootView.findViewById(R.id.Settings_tglDelivery_History);
        tglDeliveryHistory.setChecked(tabMaintenance.isDeliveryHistoryTabActive());
        tglDeliveryHistory.setOnCheckedChangeListener(this);
        tglDeliveriesForConfirmation = (ToggleButton) rootView.findViewById(R.id.Settings_tglDelivery_ForConfirmation);
        tglDeliveriesForConfirmation.setChecked(tabMaintenance.isDeliveryForConfirmationTabActive());
        tglDeliveriesForConfirmation.setOnCheckedChangeListener(this);

        bSave = (Button) rootView.findViewById(R.id.Settings_bSaveTabMaintenance);
        bSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onSaveTabMaintenance();
            }
        });

        return rootView;
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if (buttonView == tglInvoice) {
            if (!isChecked) {
                tglInvoiceMenuAsButtons.setChecked(false);
                tglInvoiceMenuAsList.setChecked(false);

                tglInvoiceMenuAsButtons.setEnabled(false);
                tglInvoiceMenuAsList.setEnabled(false);
            }else{
                tglInvoiceMenuAsButtons.setEnabled(true);
                tglInvoiceMenuAsList.setEnabled(true);
            }
        } else if (buttonView == tglSynchronize) {
            if (!isChecked) {
                tglSynchronizeByWeb.setChecked(false);
                tglSynchronizeByDriver.setChecked(false);

                tglSynchronizeByDriver.setEnabled(false);
                tglSynchronizeByDriver.setEnabled(false);
            }else{
                tglSynchronizeByDriver.setEnabled(true);
                tglSynchronizeByDriver.setEnabled(true);
            }
        } else if (buttonView == tglSynchronizeByWeb) {
            if (isChecked) {
                tglSynchronizeByDriver.setChecked(false);
            }
        } else if (buttonView == tglSynchronizeByDriver) {
            if (isChecked) {
                tglSynchronizeByWeb.setChecked(false);
            }
        } else if (buttonView == tglInventory) {
            if(!isChecked){
                tglInventoryOfSales.setChecked(false);
                tglInventoryOfStocks.setChecked(false);
                tglInventoryItemStockCount.setChecked(false);
                tglInventoryReturnsToCommissary.setChecked(false);
                tglInventoryExpenses.setChecked(false);

                tglInventoryOfSales.setEnabled(false);
                tglInventoryOfStocks.setEnabled(false);
                tglInventoryItemStockCount.setEnabled(false);
                tglInventoryReturnsToCommissary.setEnabled(false);
                tglInventoryExpenses.setEnabled(false);

            }else{

                tglInventoryOfSales.setEnabled(true);
                tglInventoryOfStocks.setEnabled(true);
                tglInventoryItemStockCount.setEnabled(true);
                tglInventoryReturnsToCommissary.setEnabled(true);
                tglInventoryExpenses.setEnabled(true);

            }
        }else if(buttonView == tglDelivery){
            if(!isChecked){
                tglDeliveryHistory.setChecked(false);
                tglDeliveriesForConfirmation.setChecked(false);

                tglDeliveryHistory.setEnabled(false);
                tglDeliveriesForConfirmation.setEnabled(false);
            }else{
                tglDeliveryHistory.setEnabled(true);
                tglDeliveriesForConfirmation.setEnabled(true);
            }
        }
    }

    public interface TabMaintenanceSettingsFragmentListener {

        void onSaveTabMaintenance();

    }


}
