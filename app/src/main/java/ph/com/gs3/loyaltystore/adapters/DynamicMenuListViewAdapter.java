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
public class DynamicMenuListViewAdapter extends BaseAdapter {

    public static final String TAG = DeliveryProductListViewAdapter.class.getSimpleName();

    private List<Object> productArrayObjects;
    private static final int TYPE_PRODUCT = 0;
    private static final int TYPE_DIVIDER = 1;

    private Context context;

    public DynamicMenuListViewAdapter(Context context) {
        this.productArrayObjects = new ArrayList<>();
        this.context = context;
    }

    public void setProductList(List<Object> productArrayObjects) {
        this.productArrayObjects.clear();
        this.productArrayObjects.addAll(productArrayObjects);
        this.notifyDataSetChanged();

        /*for(Object object : productArrayObjects){
            if(object instanceof Product){
                Log.d(TAG, "instance of Product");
            }else if(object instanceof String){
                Log.d(TAG, "instance of String");
            }else{
                Log.d(TAG, "undefined instance");
            }
        }*/
    }

    @Override
    public int getCount() {
        return productArrayObjects.size();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public Object getItem(int position) {
        return productArrayObjects.get(position);
    }

    @Override
    public int getViewTypeCount() {
        // TYPE_PRODUCT and TYPE_DIVIDER
        return 2;
    }

    @Override
    public int getItemViewType(int position) {
        if (getItem(position) instanceof Product) {
            return TYPE_PRODUCT;
        }

        return TYPE_DIVIDER;
    }

    @Override
    public boolean isEnabled(int position) {
        return (getItemViewType(position) == TYPE_PRODUCT);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        int type = getItemViewType(position);
        View row = convertView;
        if (row == null) {

            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            switch (type) {
                case TYPE_PRODUCT:
                    row = inflater.inflate(R.layout.view_menu_list_item, parent, false);
                    break;
                case TYPE_DIVIDER:
                    row = inflater.inflate(R.layout.header_product_menu_category_list, parent, false);
                    break;
            }
        }

        switch (type) {
            case TYPE_PRODUCT:
                MenuViewHolder viewHolder = new MenuViewHolder(row);
                Product product = (Product) getItem(position);
                viewHolder.tvProductName.setText(product.getName());
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

        final TextView tvProductName;

        public MenuViewHolder(View view) {
            tvProductName = (TextView) view.findViewById(R.id.DynamicMenuList_tvName);
        }

    }

}
