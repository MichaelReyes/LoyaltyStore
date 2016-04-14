package ph.com.gs3.loyaltystore.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

import ph.com.gs3.loyaltystore.R;
import ph.com.gs3.loyaltystore.adapters.ItemStockCountListViewAdapter;
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

    private ItemStockCountDetailsFragmentListener listener;

    private ItemStockCountListViewAdapter adapter;

    private Button bSave;

    private List<ItemStockCount> itemStockCountList;

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
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_item_stock_count_details, container, false);

        Log.d(TAG,"ItemStockCountDetailsFragment CREATED");

        adapter = new ItemStockCountListViewAdapter(context);
        itemStockCountList = new ArrayList<>();
        llItemsExpectedOutput = (LinearLayout) rootView.findViewById(R.id.ItemStockCountDetails_llItemsExpectedOutput);
        lvItemsPhysicalCount = (ListView) rootView.findViewById(R.id.ItemStockCountDetails_lvItemsPhysicalCount);
        lvItemsPhysicalCount.setAdapter(adapter);
        lvItemsPhysicalCount.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                listener.onItemStockCountClicked((ItemStockCount) adapter.getItem(position));
            }
        });
        bSave = (Button) rootView.findViewById(R.id.ItemStockCountDetails_bSave);
        bSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                List<ItemStockCount> stockCountList = adapter.getItemStockCountList();
                listener.onSave(stockCountList);
                clearList();
            }
        });
        setProductButtons();

        return rootView;
    }

    public void setProductButtons() {

        int buttonPerLinearLayoutCount = 3;

        llItemsExpectedOutput.removeAllViews();
        List<ItemInventory> itemInventoryList = listener.getItemInventory();
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

                    listener.onItemClicked(item);

                }
            });

            buttonMenu.setText(name);
        }

        return buttonMenu;
    }

    public void addStock(ItemStockCount item) {

        itemStockCountList.add(item);
        setItemStockCount(itemStockCountList);
    }

    public void setItemStockCount(List<ItemStockCount> itemStockCountList) {

        if (adapter != null) {
            Log.d(TAG, "adapter not null");
            adapter.setItemStockCountList(itemStockCountList);
        }

    }

    public void clearList(){
        adapter.clearItemStockCountList();
    }

    public interface ItemStockCountDetailsFragmentListener {

        List<ItemInventory> getItemInventory();

        void onItemClicked(ItemInventory item);

        void onItemStockCountClicked(ItemStockCount itemStockCount);

        void onSave(List<ItemStockCount> stockCountList);

    }


}
