package ph.com.gs3.loyaltystore.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;

import ph.com.gs3.loyaltystore.R;
import ph.com.gs3.loyaltystore.adapters.objects.MenuRowItem;
import ph.com.gs3.loyaltystore.models.sqlite.dao.Product;

/**
 * Created by Bryan-PC on 23/05/2016.
 */
public class DynamicMenuButtonListViewAdapter extends BaseAdapter {

    public static final String TAG = DynamicMenuButtonListViewAdapter.class.getSimpleName();

    private DynamicMenuButtonListViewAdapterListener listener;

    private ArrayList<Object> menuObjectArray;

    private static final int TYPE_MENU_ROW_ITEM = 0;
    private static final int TYPE_DIVIDER = 1;

    private Context context;

    public DynamicMenuButtonListViewAdapter(Context context) {
        this.context = context;
        this.menuObjectArray = new ArrayList<>();
        if (context instanceof DynamicMenuButtonListViewAdapterListener) {
            listener = (DynamicMenuButtonListViewAdapterListener) context;
        } else {
            throw new RuntimeException(context.getClass().getSimpleName() + " must implement DynamicMenuButtonListViewAdapterListener");
        }

    }

    public void setMenuRow(ArrayList<Object> menuObjectArray) {
        this.menuObjectArray.clear();
        this.menuObjectArray.addAll(menuObjectArray);
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return menuObjectArray.size();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public Object getItem(int position) {
        return menuObjectArray.get(position);
    }

    @Override
    public int getViewTypeCount() {
        // TYPE_MENU_ROW_ITEM and TYPE_DIVIDER
        return 2;
    }

    @Override
    public int getItemViewType(int position) {
        if (getItem(position) instanceof String) {
            return TYPE_DIVIDER;
        }

        return TYPE_MENU_ROW_ITEM;
    }

    @Override
    public boolean isEnabled(int position) {
        return (getItemViewType(position) == TYPE_MENU_ROW_ITEM);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View row = convertView;

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        int type = getItemViewType(position);
        if (row == null) {
            switch (type) {
                case TYPE_MENU_ROW_ITEM:
                    row = inflater.inflate(R.layout.view_menu_buttons_item, parent, false);
                    break;
                case TYPE_DIVIDER:
                    row = inflater.inflate(R.layout.header_product_menu_category_list, parent, false);
                    break;
            }
        }

        switch (type) {
            case TYPE_MENU_ROW_ITEM:

                MenuViewHolder viewHolder = new MenuViewHolder(row);

                final MenuRowItem menuRowItem = (MenuRowItem) menuObjectArray.get(position);

                viewHolder.bMenuItem1.setVisibility(View.VISIBLE);
                viewHolder.bMenuItem2.setVisibility(View.VISIBLE);
                viewHolder.bMenuItem3.setVisibility(View.VISIBLE);

                if (menuRowItem.getProduct1() != null && !"".equals(menuRowItem.getProduct1().getName())) {
                    viewHolder.bMenuItem1.setText(menuRowItem.getProduct1().getName());
                    viewHolder.bMenuItem1.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            listener.onMenuItemClicked(menuRowItem.getProduct1());
                        }
                    });
                } else {
                    viewHolder.bMenuItem1.setVisibility(View.INVISIBLE);
                }

                if (menuRowItem.getProduct2() != null && !"".equals(menuRowItem.getProduct2().getName())) {
                    viewHolder.bMenuItem2.setText(menuRowItem.getProduct2().getName());
                    viewHolder.bMenuItem2.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            listener.onMenuItemClicked(menuRowItem.getProduct2());
                        }
                    });
                } else {
                    viewHolder.bMenuItem2.setVisibility(View.INVISIBLE);
                }

                if (menuRowItem.getProduct3() != null && !"".equals(menuRowItem.getProduct3().getName())) {
                    viewHolder.bMenuItem3.setText(menuRowItem.getProduct3().getName());
                    viewHolder.bMenuItem3.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            listener.onMenuItemClicked(menuRowItem.getProduct3());
                        }
                    });
                } else {
                    viewHolder.bMenuItem3.setVisibility(View.INVISIBLE);
                }
                break;
            case TYPE_DIVIDER:
                TextView title = (TextView) row.findViewById(R.id.Menu_header);
                String titleString = (String) getItem(position);
                title.setText(titleString);
                break;
        }

        return row;
    }

    private static class MenuViewHolder {

        final Button bMenuItem1;
        final Button bMenuItem2;
        final Button bMenuItem3;

        public MenuViewHolder(View view) {
            bMenuItem1 = (Button) view.findViewById(R.id.DynamicMenuButtonsItem_bMenuItem1);
            bMenuItem2 = (Button) view.findViewById(R.id.DynamicMenuButtonsItem_bMenuItem2);
            bMenuItem3 = (Button) view.findViewById(R.id.DynamicMenuButtonsItem_bMenuItem3);
        }

    }

    public interface DynamicMenuButtonListViewAdapterListener {

        void onMenuItemClicked(Product product);

    }
}
