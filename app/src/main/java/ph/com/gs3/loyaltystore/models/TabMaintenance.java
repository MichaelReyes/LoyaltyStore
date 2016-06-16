package ph.com.gs3.loyaltystore.models;

import android.content.Context;
import android.content.SharedPreferences;

import java.io.Serializable;

/**
 * Created by Michael Reyes on 04/24/2016.
 */
public class TabMaintenance implements Serializable {

    public static final String TAG = TabMaintenance.class.getSimpleName();

    private boolean isInvoiceTabActive;
    private boolean isInvoiceMenuAsButtonsTabActive;
    private boolean isInvoiceMenuAsListTabActive;
    private boolean isSynchronizeTabActive;
    private boolean isSynchronizeByWebTabActive;
    private boolean isSynchronizeByDriverTabActive;
    private boolean isInventoryTabActive;
    private boolean isInventoryOfSalesTabActive;
    private boolean isInventoryOfStocksTabActive;
    private boolean isInventoryItemStockCountTabActive;
    private boolean isInventoryReturnsToCommissaryTabActive;
    private boolean isInventoryExpensesTabActive;
    private boolean isSalesTabActive;
    private boolean isDeliveryTabActive;
    private boolean isDeliveryHistoryTabActive;
    private boolean isDeliveryForConfirmationTabActive;

    public static TabMaintenance getTabMaintenanceFromSharedPreferences(Context context) {
        TabMaintenance tabMaintenance = new TabMaintenance();

        SharedPreferences settings = context.getSharedPreferences(TAG, Context.MODE_PRIVATE);

        tabMaintenance.isInvoiceTabActive =
                settings.getBoolean("IS_INVOICE_TAB_ACTIVE", true);
        tabMaintenance.isInvoiceMenuAsButtonsTabActive =
                settings.getBoolean("IS_INVOICE_MENU_AS_BUTTON_TAB_ACTIVE", true);
        tabMaintenance.isInvoiceMenuAsListTabActive =
                settings.getBoolean("IS_INVOICE_MENU_AS_LIST_TAB_ACTIVE", true);
        tabMaintenance.isSynchronizeTabActive =
                settings.getBoolean("IS_SYNCHRONIZE_TAB_ACTIVE", true);
        tabMaintenance.isSynchronizeByWebTabActive =
                settings.getBoolean("IS_SYNCHRONIZE_BY_WEB_TAB_ACTIVE", true);
        tabMaintenance.isSynchronizeByDriverTabActive =
                settings.getBoolean("IS_SYNCHRONIZE_BY_DRIVER_TAB_ACTIVE", true);
        tabMaintenance.isInventoryTabActive =
                settings.getBoolean("IS_INVENTORY_TAB_ACTIVE", true);
        tabMaintenance.isInventoryOfSalesTabActive =
                settings.getBoolean("IS_INVENTORY_OF_SALES_TAB_ACTIVE", true);
        tabMaintenance.isInventoryOfStocksTabActive =
                settings.getBoolean("IS_INVENTORY_OF_STOCKS_TAB_ACTIVE", true);
        tabMaintenance.isInventoryItemStockCountTabActive =
                settings.getBoolean("IS_INVENTORY_ITEM_STOCK_COUNT_TAB_ACTIVE", true);
        tabMaintenance.isInventoryReturnsToCommissaryTabActive =
                settings.getBoolean("IS_INVENTORY_RETURNS_TO_COMMISSARY_TAB_ACTIVE", true);
        tabMaintenance.isInventoryExpensesTabActive =
                settings.getBoolean("IS_INVENTORY_EXPENSES_TAB_ACTIVE", true);
        tabMaintenance.isSalesTabActive =
                settings.getBoolean("IS_SALES_TAB_ACTIVE", true);
        tabMaintenance.isDeliveryTabActive =
                settings.getBoolean("IS_DELIVERY_TAB_ACTIVE", true);
        tabMaintenance.isDeliveryHistoryTabActive =
                settings.getBoolean("IS_DELIVERY_HISTORY_TAB_ACTIVE", true);
        tabMaintenance.isDeliveryForConfirmationTabActive =
                settings.getBoolean("IS_DELIVERY_FOR_CONFIRMATION_TAB_ACTIVE", true);


        return tabMaintenance;
    }

    public void save(Context context) {

        SharedPreferences settings = context.getSharedPreferences(TAG, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = settings.edit();

        editor.putBoolean("IS_INVOICE_TAB_ACTIVE",isInvoiceTabActive);
        editor.putBoolean("IS_INVOICE_MENU_AS_BUTTON_TAB_ACTIVE",isInvoiceMenuAsButtonsTabActive);
        editor.putBoolean("IS_INVOICE_MENU_AS_LIST_TAB_ACTIVE",isInvoiceMenuAsListTabActive);
        editor.putBoolean("IS_SYNCHRONIZE_TAB_ACTIVE",isSynchronizeTabActive);
        editor.putBoolean("IS_SYNCHRONIZE_BY_WEB_TAB_ACTIVE",isSynchronizeByWebTabActive);
        editor.putBoolean("IS_SYNCHRONIZE_BY_DRIVER_TAB_ACTIVE",isSynchronizeByDriverTabActive);
        editor.putBoolean("IS_INVENTORY_TAB_ACTIVE",isInventoryTabActive);
        editor.putBoolean("IS_INVENTORY_OF_SALES_TAB_ACTIVE",isInventoryOfSalesTabActive);
        editor.putBoolean("IS_INVENTORY_OF_STOCKS_TAB_ACTIVE",isInventoryOfStocksTabActive);
        editor.putBoolean("IS_INVENTORY_ITEM_STOCK_COUNT_TAB_ACTIVE",isInventoryItemStockCountTabActive);
        editor.putBoolean("IS_INVENTORY_RETURNS_TO_COMMISSARY_TAB_ACTIVE",isInventoryReturnsToCommissaryTabActive);
        editor.putBoolean("IS_INVENTORY_EXPENSES_TAB_ACTIVE",isInventoryExpensesTabActive);
        editor.putBoolean("IS_SALES_TAB_ACTIVE",isSalesTabActive);
        editor.putBoolean("IS_DELIVERY_TAB_ACTIVE",isDeliveryTabActive);
        editor.putBoolean("IS_DELIVERY_HISTORY_TAB_ACTIVE",isDeliveryHistoryTabActive);
        editor.putBoolean("IS_DELIVERY_FOR_CONFIRMATION_TAB_ACTIVE",isDeliveryForConfirmationTabActive);


        editor.commit();
    }

    //<editor-fold desc="Getters & Setters">

    public boolean isInvoiceTabActive() {
        return isInvoiceTabActive;
    }

    public void setInvoiceTabActive(boolean invoiceTabActive) {
        isInvoiceTabActive = invoiceTabActive;
    }

    public boolean isInvoiceMenuAsButtonsTabActive() {
        return isInvoiceMenuAsButtonsTabActive;
    }

    public void setInvoiceMenuAsButtonsTabActive(boolean invoiceMenuAsButtonsTabActive) {
        isInvoiceMenuAsButtonsTabActive = invoiceMenuAsButtonsTabActive;
    }

    public boolean isInvoiceMenuAsListTabActive() {
        return isInvoiceMenuAsListTabActive;
    }

    public void setInvoiceMenuAsListTabActive(boolean invoiceMenuAsListTabActive) {
        isInvoiceMenuAsListTabActive = invoiceMenuAsListTabActive;
    }

    public boolean isSynchronizeTabActive() {
        return isSynchronizeTabActive;
    }

    public void setSynchronizeTabActive(boolean synchronizeTabActive) {
        isSynchronizeTabActive = synchronizeTabActive;
    }

    public boolean isSynchronizeByWebTabActive() {
        return isSynchronizeByWebTabActive;
    }

    public void setSynchronizeByWebTabActive(boolean synchronizeByWebTabActive) {
        isSynchronizeByWebTabActive = synchronizeByWebTabActive;
    }

    public boolean isSynchronizeByDriverTabActive() {
        return isSynchronizeByDriverTabActive;
    }

    public void setSynchronizeByDriverTabActive(boolean synchronizeByDriverTabActive) {
        isSynchronizeByDriverTabActive = synchronizeByDriverTabActive;
    }

    public boolean isInventoryTabActive() {
        return isInventoryTabActive;
    }

    public void setInventoryTabActive(boolean inventoryTabActive) {
        isInventoryTabActive = inventoryTabActive;
    }

    public boolean isInventoryOfSalesTabActive() {
        return isInventoryOfSalesTabActive;
    }

    public void setInventoryOfSalesTabActive(boolean inventoryOfSalesTabActive) {
        isInventoryOfSalesTabActive = inventoryOfSalesTabActive;
    }

    public boolean isInventoryOfStocksTabActive() {
        return isInventoryOfStocksTabActive;
    }

    public void setInventoryOfStocksTabActive(boolean inventoryOfStocksTabActive) {
        isInventoryOfStocksTabActive = inventoryOfStocksTabActive;
    }

    public boolean isInventoryItemStockCountTabActive() {
        return isInventoryItemStockCountTabActive;
    }

    public void setInventoryItemStockCountTabActive(boolean inventoryItemStockCountTabActive) {
        isInventoryItemStockCountTabActive = inventoryItemStockCountTabActive;
    }

    public boolean isInventoryReturnsToCommissaryTabActive() {
        return isInventoryReturnsToCommissaryTabActive;
    }

    public void setInventoryReturnsToCommissaryTabActive(boolean inventoryReturnsToCommissaryTabActive) {
        isInventoryReturnsToCommissaryTabActive = inventoryReturnsToCommissaryTabActive;
    }

    public boolean isInventoryExpensesTabActive() {
        return isInventoryExpensesTabActive;
    }

    public void setInventoryExpensesTabActive(boolean inventoryExpensesTabActive) {
        isInventoryExpensesTabActive = inventoryExpensesTabActive;
    }

    public boolean isSalesTabActive() {
        return isSalesTabActive;
    }

    public void setSalesTabActive(boolean salesTabActive) {
        isSalesTabActive = salesTabActive;
    }

    public boolean isDeliveryTabActive() {
        return isDeliveryTabActive;
    }

    public void setDeliveryTabActive(boolean deliveryTabActive) {
        isDeliveryTabActive = deliveryTabActive;
    }

    public boolean isDeliveryHistoryTabActive() {
        return isDeliveryHistoryTabActive;
    }

    public void setDeliveryHistoryTabActive(boolean deliveryHistoryTabActive) {
        isDeliveryHistoryTabActive = deliveryHistoryTabActive;
    }

    public boolean isDeliveryForConfirmationTabActive() {
        return isDeliveryForConfirmationTabActive;
    }

    public void setDeliveryForConfirmationTabActive(boolean deliveryForConfirmationTabActive) {
        isDeliveryForConfirmationTabActive = deliveryForConfirmationTabActive;
    }


    //</editor-fold>
}
