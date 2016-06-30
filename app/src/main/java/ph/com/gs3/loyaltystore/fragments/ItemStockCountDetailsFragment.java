package ph.com.gs3.loyaltystore.fragments;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;

import java.util.ArrayList;
import java.util.List;

import ph.com.gs3.loyaltystore.R;
import ph.com.gs3.loyaltystore.adapters.InventoryViewAdapter;
import ph.com.gs3.loyaltystore.adapters.ItemStockCountListViewAdapter;
import ph.com.gs3.loyaltystore.adapters.objects.InventoryRowItem;
import ph.com.gs3.loyaltystore.models.sqlite.dao.ItemInventory;
import ph.com.gs3.loyaltystore.models.sqlite.dao.ItemStockCount;

/**
 * Created by Bryan-PC on 07/04/2016.
 */
public class ItemStockCountDetailsFragment extends Fragment {

    public static final String TAG = ItemStockCountDetailsFragment.class.getSimpleName();

    private Context context;

    private LinearLayout llItemsExpectedOutput;

    private ListView lvItemsPhysicalCount;
    private ListView lvExpectedOutput;

    private ItemStockCountDetailsFragmentListener listener;

    private InventoryViewAdapter inventoryAdapter;
    private ItemStockCountListViewAdapter stockCountAdapter;

    private Button bSave;
    private Button bSyncInventoryFromWeb;

    private ProgressBar pbSyncInventoryFromWeb;

    private List<ItemStockCount> itemStockCountList;

    private int currentFirstVisibleItem;
    private int currentVisibleItemCount;
    private int currentScrollState;
    private int totalItemCount;

    public static ItemStockCountDetailsFragment newInstance() {
        ItemStockCountDetailsFragment fragment = new ItemStockCountDetailsFragment();
        return fragment;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;

        if (context instanceof ItemStockCountDetailsFragmentListener) {
            listener = (ItemStockCountDetailsFragmentListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement ItemInventoryDetailsFragmentListener");
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_item_stock_count_details, container, false);

        Log.d(TAG, "ItemStockCountDetailsFragment CREATED");

        stockCountAdapter = new ItemStockCountListViewAdapter(context);
        inventoryAdapter = new InventoryViewAdapter(context);

        itemStockCountList = new ArrayList<>();
        llItemsExpectedOutput = (LinearLayout) rootView.findViewById(R.id.ItemStockCountDetails_llItemsExpectedOutput);

        lvItemsPhysicalCount = (ListView) rootView.findViewById(R.id.ItemStockCountDetails_lvItemsPhysicalCount);
        lvItemsPhysicalCount.setAdapter(stockCountAdapter);
        lvItemsPhysicalCount.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                listener.onItemStockCountClicked((ItemStockCount) stockCountAdapter.getItem(position));
            }
        });

        lvExpectedOutput = (ListView) rootView.findViewById(R.id.ItemStockCountDetails_lvInventory);
        lvExpectedOutput.setAdapter(inventoryAdapter);

        lvExpectedOutput.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                currentScrollState = scrollState;

                if (totalItemCount == (currentFirstVisibleItem + currentVisibleItemCount))
                    isScrollCompleted();
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                currentFirstVisibleItem = firstVisibleItem;
                currentVisibleItemCount = visibleItemCount;
            }
        });

        bSave = (Button) rootView.findViewById(R.id.ItemStockCountDetails_bSave);
        bSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                List<ItemStockCount> stockCountList = stockCountAdapter.getItemStockCountList();
                listener.onSaveItemStockCount(stockCountList);
                clearList();
            }
        });

        bSyncInventoryFromWeb = (Button) rootView.findViewById(R.id.ItemStockCountDetails_bSyncInventoryFromWeb);
        bSyncInventoryFromWeb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle("Sync Inventory From Web");
                builder.setMessage("Are you sure you want to sync inventory from web?" +
                        "(All inventory data from this device will be replaced by the data from web)");

                builder.setPositiveButton("Sync", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        listener.onSyncInventoryFromWeb();
                    }
                });

                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

                builder.show();

            }
        });

        pbSyncInventoryFromWeb = (ProgressBar) rootView.findViewById(R.id.ItemStockCountDetails_pbSyncInventoryFromWeb);

        //setProductButtons();

        return rootView;
    }

    private void isScrollCompleted() {
        if (this.currentVisibleItemCount > 0 && this.currentScrollState == AbsListView.OnScrollListener.SCROLL_STATE_IDLE) {
            listener.onLoadMoreInventory();
        }
    }

    public void setInventory() {

        List<ItemInventory> itemInventoryList = listener.getItemInventory();

        totalItemCount = 0;

        if (inventoryAdapter != null) {
            int itemPerRowLimit = 2;

            List<InventoryRowItem> inventoryRowItems = new ArrayList<>();

            InventoryRowItem inventoryRowItem = new InventoryRowItem();

            for (ItemInventory itemInventory : itemInventoryList) {

                if (itemPerRowLimit > 0) {
                    switch (itemPerRowLimit) {
                        case 2:
                            inventoryRowItem.setItem1(itemInventory);
                            break;
                        case 1:
                            inventoryRowItem.setItem2(itemInventory);
                            break;
                    }
                } else {
                    totalItemCount++;
                    inventoryRowItems.add(inventoryRowItem);
                    inventoryRowItem = new InventoryRowItem();
                    itemPerRowLimit = 2;
                    inventoryRowItem.setItem1(itemInventory);
                }

                itemPerRowLimit--;

            }

            if (itemInventoryList.size() > 0){
                inventoryRowItems.add(inventoryRowItem);
                totalItemCount++;
            }

            if (inventoryAdapter != null) {
                inventoryAdapter.setInventoryRowItems(inventoryRowItems);
            }
        }
    }

    public void setProductButtons() {

        int buttonPerLinearLayoutCount = 3;

        llItemsExpectedOutput.removeAllViews();
        List<ItemInventory> itemInventoryList = listener.getItemInventory() == null ? new ArrayList<ItemInventory>() : listener.getItemInventory();
        LinearLayout menuRow = createNewMenuRow();

        for (ItemInventory item : itemInventoryList) {
            if (buttonPerLinearLayoutCount != 0) {
                menuRow.addView(createButtonMenu(item));
                buttonPerLinearLayoutCount -= 1;
            } else {
                llItemsExpectedOutput.addView(menuRow);
                buttonPerLinearLayoutCount = 2;
                menuRow = createNewMenuRow();
                menuRow.addView(createButtonMenu(item));
            }
        }

        if (buttonPerLinearLayoutCount != 0) {
            while (buttonPerLinearLayoutCount != 0) {
                menuRow.addView(createButtonMenu(null));
                buttonPerLinearLayoutCount -= 1;
            }
        }

        //Add last menu row created
        if (menuRow != null) {
            llItemsExpectedOutput.addView(menuRow);
        }

    }

    private LinearLayout createNewMenuRow() {

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 0);
        params.weight = 1;
        LinearLayout linearLayout = new LinearLayout(context);
        linearLayout.setLayoutParams(params);
        linearLayout.setOrientation(LinearLayout.HORIZONTAL);

        return linearLayout;

    }

    private Button createButtonMenu(final ItemInventory item) {

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.MATCH_PARENT);
        params.weight = 1;
        Button buttonMenu = new Button(context);
        buttonMenu.setLayoutParams(params);
        buttonMenu.setTextSize(TypedValue.COMPLEX_UNIT_PT, 7);

        if (item == null) {
            buttonMenu.setVisibility(View.INVISIBLE);
        } else {

            double quantity = item.getQuantity() == null ? 0 : item.getQuantity();

            final String name = item.getName() + "(" + quantity + ")";

            buttonMenu.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    listener.onItemClick(item);

                }
            });

            buttonMenu.setText(name);
        }

        return buttonMenu;
    }

    public void addStockCount(ItemStockCount item) {

        itemStockCountList.add(item);
        setItemStockCount(itemStockCountList);

    }

    public void setItemStockCount(List<ItemStockCount> itemStockCountList) {

        if (stockCountAdapter != null) {
            Log.d(TAG, "stockCountAdapter not null");
            stockCountAdapter.setItemStockCountList(itemStockCountList);
        }

    }

    public void clearList() {
        itemStockCountList.clear();
        stockCountAdapter.setItemStockCountList(new ArrayList<ItemStockCount>());
        stockCountAdapter.clearItemStockCountList();
    }

    public void onStartSync(){
        bSyncInventoryFromWeb.setVisibility(View.GONE);
        pbSyncInventoryFromWeb.setVisibility(View.VISIBLE);
    }

    public void onDoneSync(){
        bSyncInventoryFromWeb.setVisibility(View.VISIBLE);
        pbSyncInventoryFromWeb.setVisibility(View.GONE);

        inventoryAdapter.notifyDataSetChanged();
    }

    public interface ItemStockCountDetailsFragmentListener {

        List<ItemInventory> getItemInventory();

        void onItemClick(ItemInventory item);

        void onItemStockCountClicked(ItemStockCount itemStockCount);

        void onSaveItemStockCount(List<ItemStockCount> stockCountList);

        void onLoadMoreInventory();

        void onSyncInventoryFromWeb();

    }


}
