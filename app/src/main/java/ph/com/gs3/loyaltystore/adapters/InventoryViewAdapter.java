package ph.com.gs3.loyaltystore.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;

import java.util.ArrayList;
import java.util.List;

import ph.com.gs3.loyaltystore.R;
import ph.com.gs3.loyaltystore.adapters.objects.InventoryRowItem;
import ph.com.gs3.loyaltystore.models.sqlite.dao.ItemInventory;

/**
 * Created by Bryan-PC on 10/05/2016.
 */
public class InventoryViewAdapter extends BaseAdapter {

    public static final String TAG = InventoryViewAdapter.class.getSimpleName();

    private InventoryViewAdapterListener listener;

    private Context context;
    private List<InventoryRowItem> inventoryRowItems;

    public InventoryViewAdapter(Context context) {
        this.context = context;
        this.inventoryRowItems = new ArrayList<>();

        if (context instanceof InventoryViewAdapterListener) {
            listener = (InventoryViewAdapterListener) context;
        } else {
            throw new RuntimeException(context.getClass().getSimpleName() + " must implement DynamicMenuButtonListViewAdapterListener");
        }

    }

    public void setInventoryRowItems(List<InventoryRowItem> inventoryRowItems) {
        this.inventoryRowItems.clear();
        this.inventoryRowItems.addAll(inventoryRowItems);
        this.notifyDataSetChanged();
    }

    public void clearMenuItemRowList() {
        this.inventoryRowItems.clear();
        this.notifyDataSetChanged();
    }

    public void addMenuItemRow(InventoryRowItem inventoryRowItem) {
        this.inventoryRowItems.add(inventoryRowItem);
        this.notifyDataSetChanged();
    }

    public List<InventoryRowItem> getInventoryRowItems() {

        return inventoryRowItems;

    }

    @Override
    public int getCount() {
        return inventoryRowItems.size();
    }

    @Override
    public Object getItem(int position) {
        return inventoryRowItems.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View row = convertView;
        MenuViewHolder viewHolder;

        if (row == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            row = inflater.inflate(R.layout.view_item_inventory_list_item, parent, false);

            viewHolder = new MenuViewHolder(row);
            row.setTag(viewHolder);
        }

        viewHolder = (MenuViewHolder) row.getTag();

        final InventoryRowItem inventoryRowItem = inventoryRowItems.get(position);

        viewHolder.bItem1.setVisibility(View.VISIBLE);
        viewHolder.bItem1.setBackgroundResource(android.R.drawable.btn_default);
        viewHolder.bItem2.setVisibility(View.VISIBLE);
        viewHolder.bItem2.setBackgroundResource(android.R.drawable.btn_default);

        if (inventoryRowItem.getItem1() != null && !"".equals(inventoryRowItem.getItem1().getName())) {

            double quantity1 = inventoryRowItem.getItem1().getQuantity() == null ? 0 : inventoryRowItem.getItem1().getQuantity();

            if(quantity1 < 5){
                /*
                viewHolder.bItem1.setBackgroundColor(Color.RED);
                viewHolder.bItem1.setTextColor(Color.WHITE);
                */
            }

            viewHolder.bItem1.setText(inventoryRowItem.getItem1().getName() + " (" + quantity1 + ")");
            viewHolder.bItem1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onItemInventoryClicked(inventoryRowItem.getItem1());
                }
            });
        } else {
            viewHolder.bItem1.setVisibility(View.INVISIBLE);
        }

        if (inventoryRowItem.getItem2() != null && !"".equals(inventoryRowItem.getItem2().getName())) {

            double quantity2 = inventoryRowItem.getItem2().getQuantity() == null ? 0 : inventoryRowItem.getItem2().getQuantity();

            if(quantity2 < 5){
                /*
                viewHolder.bItem2.setBackgroundColor(Color.RED);
                viewHolder.bItem2.setTextColor(Color.WHITE);
                */
            }

            viewHolder.bItem2.setText(inventoryRowItem.getItem2().getName() + " (" + quantity2 + ")");
            viewHolder.bItem2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onItemInventoryClicked(inventoryRowItem.getItem2());
                }
            });
        } else {
            viewHolder.bItem2.setVisibility(View.INVISIBLE);
        }


        return row;
    }


    private static class MenuViewHolder {

        final Button bItem1;
        final Button bItem2;

        public MenuViewHolder(View view) {
            bItem1 = (Button) view.findViewById(R.id.ItemInventoryDetails_bItem1);
            bItem2 = (Button) view.findViewById(R.id.ItemInventoryDetails_bItem2);
        }

    }

    public interface InventoryViewAdapterListener {

        void onItemInventoryClicked(ItemInventory itemInventory);

    }
}
