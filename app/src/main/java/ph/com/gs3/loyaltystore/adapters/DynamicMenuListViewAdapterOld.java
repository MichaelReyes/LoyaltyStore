package ph.com.gs3.loyaltystore.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import ph.com.gs3.loyaltystore.R;
import ph.com.gs3.loyaltystore.models.sqlite.dao.Product;

/**
 * Created by Bryan-PC on 10/05/2016.
 */
public class DynamicMenuListViewAdapterOld extends BaseAdapter {

    public static final String TAG = DeliveryProductListViewAdapter.class.getSimpleName();

    private Context context;
    private List<Product> productList;

    public DynamicMenuListViewAdapterOld(Context context) {
        this.context = context;
        this.productList = new ArrayList<>();

    }

    public void setProductList(List<Product> productList) {
        this.productList.clear();
        this.productList.addAll(productList);
        this.notifyDataSetChanged();
    }

    public void clearMenuItemRowList(){
        this.productList.clear();
        this.notifyDataSetChanged();
    }

    public void addMenuItemRow(Product menuItemRow){
        this.productList.add(menuItemRow);
        this.notifyDataSetChanged();
    }

    public List<Product> getProductList() {

        return productList;

    }

    @Override
    public int getCount() {
        return productList.size();
    }

    @Override
    public Object getItem(int position) {
        return productList.get(position);
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
            row = inflater.inflate(R.layout.view_menu_list_item, parent, false);

            viewHolder = new MenuViewHolder(row);
            row.setTag(viewHolder);
        }

        viewHolder = (MenuViewHolder) row.getTag();

        Product product = productList.get(position);

        viewHolder.tvProductName.setText(product.getName());

        return row;
    }


    private static class MenuViewHolder {

        final TextView tvProductName;

        public MenuViewHolder(View view) {
            tvProductName = (TextView) view.findViewById(R.id.DynamicMenuList_tvName);
        }

    }


}
