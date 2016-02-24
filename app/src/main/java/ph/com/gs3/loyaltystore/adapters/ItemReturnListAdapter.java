package ph.com.gs3.loyaltystore.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

import ph.com.gs3.loyaltystore.LoyaltyStoreApplication;
import ph.com.gs3.loyaltystore.R;
import ph.com.gs3.loyaltystore.models.sqlite.dao.ItemReturn;
import ph.com.gs3.loyaltystore.models.sqlite.dao.ProductDao;

/**
 * Created by Michael Reyes on 8/17/2015.
 */
public class ItemReturnListAdapter extends BaseAdapter {

    private Context context;
    private List<ItemReturn> itemReturnList;
    private ProductDao productDao;

    private static final SimpleDateFormat formatter = new SimpleDateFormat(
            "yyyy-MM-dd HH:mm:ss", Locale.ENGLISH);

    public ItemReturnListAdapter(Context context, List<ItemReturn> itemReturnList) {
        this.context = context;
        this.itemReturnList = itemReturnList;

        this.productDao = LoyaltyStoreApplication.getInstance().getSession().getProductDao();

    }

    @Override
    public int getCount() {
        return itemReturnList.size();
    }

    @Override
    public Object getItem(int position) {
        return itemReturnList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View row = convertView;
        ItemReturnViewHolder viewHolder;

        ItemReturn itemReturn = (ItemReturn) getItem(position);

        if (row == null) {

            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            row = inflater.inflate(R.layout.view_item_return, parent, false);

            viewHolder = new ItemReturnViewHolder(row);
            row.setTag(viewHolder);
        }

        viewHolder = (ItemReturnViewHolder) row.getTag();

        viewHolder.tvItem.setText(itemReturn.getItem());
        viewHolder.tvProduct.setText(itemReturn.getProduct_name());
        viewHolder.tvQuantityOrAmount.setText(String.valueOf(itemReturn.getQuantity()));
        viewHolder.tvRemarks.setText(itemReturn.getRemarks() == null ? "" : itemReturn.getRemarks());

        if(!itemReturn.getItem().toUpperCase().equals("SPOILAGE")){

            viewHolder.llProduct.setVisibility(View.GONE);

        }

        return row;
    }

    private static class ItemReturnViewHolder {

        final TextView tvItem;
        final TextView tvProduct;
        final TextView tvQuantityOrAmount;
        final TextView tvRemarks;
        final LinearLayout llProduct;

        public ItemReturnViewHolder(View view) {
            tvItem = (TextView) view.findViewById(R.id.ITR_tvItem);
            tvProduct = (TextView) view.findViewById(R.id.ITR_tvProduct);
            tvQuantityOrAmount = (TextView) view.findViewById(R.id.ITR_tvQuantityOrAmount);
            tvRemarks = (TextView) view.findViewById(R.id.ITR_tvRemarks);
            llProduct = (LinearLayout) view.findViewById(R.id.ITR_llProductView);
        }

    }
}
