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
import ph.com.gs3.loyaltystore.adapters.objects.MenuRowItem;
import ph.com.gs3.loyaltystore.models.sqlite.dao.Product;

/**
 * Created by Bryan-PC on 10/05/2016.
 */
public class DynamicMenuButtonListViewAdapterOld extends BaseAdapter {

    public static final String TAG = DeliveryProductListViewAdapter.class.getSimpleName();

    private DynamicMenuButtonListViewAdapterListenerOld listener;

    private Context context;
    private List<MenuRowItem> menuRowItemList;

    public DynamicMenuButtonListViewAdapterOld(Context context) {
        this.context = context;
        this.menuRowItemList = new ArrayList<>();

        if (context instanceof DynamicMenuButtonListViewAdapterListenerOld) {
            listener = (DynamicMenuButtonListViewAdapterListenerOld) context;
        } else {
            throw new RuntimeException(context.getClass().getSimpleName() + " must implement DynamicMenuButtonListViewAdapterListener");
        }

    }

    public void setMenuRowItemList(List<MenuRowItem> menuRowItemList) {
        this.menuRowItemList.clear();
        this.menuRowItemList.addAll(menuRowItemList);
        this.notifyDataSetChanged();
    }

    public void clearMenuItemRowList(){
        this.menuRowItemList.clear();
        this.notifyDataSetChanged();
    }

    public void addMenuItemRow(MenuRowItem menuRowItem){
        this.menuRowItemList.add(menuRowItem);
        this.notifyDataSetChanged();
    }

    public List<MenuRowItem> getMenuRowItemList() {

        return menuRowItemList;

    }

    @Override
    public int getCount() {
        return menuRowItemList.size();
    }

    @Override
    public Object getItem(int position) {
        return menuRowItemList.get(position);
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
            row = inflater.inflate(R.layout.view_menu_buttons_item, parent, false);

            viewHolder = new MenuViewHolder(row);
            row.setTag(viewHolder);
        }

        viewHolder = (MenuViewHolder) row.getTag();

        final MenuRowItem menuRowItem = menuRowItemList.get(position);

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
        }else{
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
        }else{
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
        }else{
            viewHolder.bMenuItem3.setVisibility(View.INVISIBLE);
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

    public interface DynamicMenuButtonListViewAdapterListenerOld {

        void onMenuItemClicked(Product product);

    }
}
