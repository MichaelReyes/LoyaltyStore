package ph.com.gs3.loyaltystore.adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

import ph.com.gs3.loyaltystore.LoyaltyStoreApplication;
import ph.com.gs3.loyaltystore.R;
import ph.com.gs3.loyaltystore.globals.Constants;
import ph.com.gs3.loyaltystore.models.sqlite.dao.ItemInventory;
import ph.com.gs3.loyaltystore.models.sqlite.dao.ItemInventoryDao;
import ph.com.gs3.loyaltystore.models.sqlite.dao.ItemReturn;
import ph.com.gs3.loyaltystore.models.sqlite.dao.ItemReturnDao;
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

    public void setItemReturnList(List<ItemReturn> itemReturnList){
        this.itemReturnList.clear();
        this.itemReturnList.addAll(itemReturnList);
        notifyDataSetChanged();
    }

    public void addItemReturn(ItemReturn itemReturn){
        this.itemReturnList.add(itemReturn);
        notifyDataSetChanged();
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
    public View getView(final int position, View convertView, ViewGroup parent) {

        View row = convertView;
        ItemReturnViewHolder viewHolder;

        final ItemReturn itemReturn = (ItemReturn) getItem(position);

        if (row == null) {

            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            row = inflater.inflate(R.layout.view_item_return, parent, false);

            viewHolder = new ItemReturnViewHolder(row);
            row.setTag(viewHolder);
        }

        viewHolder = (ItemReturnViewHolder) row.getTag();

        Constants constants = new Constants();
        DecimalFormat decimalFormat = constants.DECIMAL_FORMAT;

        viewHolder.tvType.setText("Type : " + itemReturn.getType());

        String dateString = itemReturn.getDate_created() != null ? formatter.format(itemReturn.getDate_created()) : "";

        viewHolder.tvDate.setText("Date : " + dateString);
        viewHolder.tvProduct.setText("Name : " + itemReturn.getProduct_name());
        viewHolder.tvQuantityOrAmount.setText("Quantity : " + decimalFormat.format((itemReturn.getQuantity())));

        String remarks = itemReturn.getRemarks() == null ? "" : itemReturn.getRemarks();

        viewHolder.tvRemarks.setText("Remarks : " + remarks);
        viewHolder.tvStatus.setText("Status : " + itemReturn.getStatus());

        viewHolder.bDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case DialogInterface.BUTTON_POSITIVE:
                                //Yes button clicked

                                ItemReturnDao itemReturnDao
                                        = LoyaltyStoreApplication.getInstance().getSession().getItemReturnDao();

                                ItemInventoryDao itemInventoryDao
                                        = LoyaltyStoreApplication.getSession().getItemInventoryDao();

                                List<ItemInventory> itemInventoryList
                                        = itemInventoryDao
                                        .queryBuilder()
                                        .where(
                                                ItemInventoryDao.Properties.Name.eq(
                                                        itemReturn.getProduct_name()
                                                )
                                        )
                                        .list();

                                for(ItemInventory itemInventory : itemInventoryList){

                                    itemInventory.setQuantity(itemInventory.getQuantity() + itemReturn.getQuantity());
                                      itemInventoryDao.insertOrReplace(itemInventory);

                                }

                                itemReturnDao.delete((ItemReturn) getItem(position));

                                itemReturnList.remove(position);

                                notifyDataSetChanged();

                                break;

                            case DialogInterface.BUTTON_NEGATIVE:
                                //No button clicked
                                break;
                        }
                    }
                };

                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setMessage("Are you sure you want to delete this item?").setPositiveButton("Yes", dialogClickListener)
                        .setNegativeButton("No", dialogClickListener).show();
            }
        });

        viewHolder.bDelete.setVisibility(View.VISIBLE);

        if (itemReturn.getIs_synced()) {
            viewHolder.bDelete.setVisibility(View.INVISIBLE);
        }

        return row;
    }

    private static class ItemReturnViewHolder {

        final TextView tvType;
        final TextView tvDate;
        final TextView tvProduct;
        final TextView tvQuantityOrAmount;
        final TextView tvRemarks;
        final TextView tvStatus;
        final Button bDelete;

        public ItemReturnViewHolder(View view) {
            tvType = (TextView) view.findViewById(R.id.ItemReturn_tvType);
            tvDate = (TextView) view.findViewById(R.id.ItemReturn_tvDate);
            tvProduct = (TextView) view.findViewById(R.id.ItemReturn_tvProduct);
            tvQuantityOrAmount = (TextView) view.findViewById(R.id.ItemReturn_tvQuantityOrAmount);
            tvRemarks = (TextView) view.findViewById(R.id.ItemReturn_tvRemarks);
            tvStatus = (TextView) view.findViewById(R.id.ItemReturn_tvStatus);
            bDelete = (Button) view.findViewById(R.id.ItemReturn_bDelete);
        }

    }
}
