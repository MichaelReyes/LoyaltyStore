package ph.com.gs3.loyaltystore.adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import ph.com.gs3.loyaltystore.R;
import ph.com.gs3.loyaltystore.globals.Constants;
import ph.com.gs3.loyaltystore.models.sqlite.dao.ItemStockCount;

/**
 * Created by Bryan-PC on 12/04/2016.
 */
public class ItemStockCountListViewAdapter extends BaseAdapter {

    private Context context;
    private List<ItemStockCount> itemStockCountList;


    public ItemStockCountListViewAdapter(Context context) {
        this.context = context;
        this.itemStockCountList = new ArrayList<>();

    }

    public void setItemStockCountList(List<ItemStockCount> itemStockCountList) {
        this.itemStockCountList.clear();
        this.itemStockCountList.addAll(itemStockCountList);
        this.notifyDataSetChanged();
    }

    public void clearItemStockCountList(){
        this.itemStockCountList.clear();
        this.notifyDataSetChanged();
    }

    public List<ItemStockCount> getItemStockCountList() {

        return itemStockCountList;

    }

    @Override
    public int getCount() {
        return itemStockCountList.size();
    }

    @Override
    public Object getItem(int position) {
        return itemStockCountList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        View row = convertView;
        DeliveryViewHolder viewHolder;

        final ItemStockCount item = (ItemStockCount) getItem(position);

        if (row == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            row = inflater.inflate(R.layout.view_item_stock_count, parent, false);

            viewHolder = new DeliveryViewHolder(row);
            row.setTag(viewHolder);
        }

        viewHolder = (DeliveryViewHolder) row.getTag();

        viewHolder.tvDate.setVisibility(View.VISIBLE);
        viewHolder.tvRemarks.setVisibility(View.VISIBLE);
        viewHolder.bRemove.setVisibility(View.GONE);

        viewHolder.llItemStockCount.setBackgroundColor(Color.TRANSPARENT);

        viewHolder.tvName.setText(item.getName());
        viewHolder.tvExpectedOutput.setText(String.valueOf(item.getExpectedQuantity()));
        viewHolder.tvPhysicalCount.setText(String.valueOf(item.getQuantity()));
        viewHolder.tvRemarks.setText(item.getRemarks());

        if(!"".equals(item.getRemarks().trim()) && item.getDate_counted() == null){

            viewHolder.llItemStockCount.setBackgroundColor(Color.GRAY);

        }

        if(item.getDate_counted() == null){
            viewHolder.tvDate.setVisibility(View.GONE);
            viewHolder.tvRemarks.setVisibility(View.GONE);
            viewHolder.bRemove.setVisibility(View.VISIBLE);
        }else{
            SimpleDateFormat formatter = Constants.SIMPLE_DATE_TIME_FORMAT;
            viewHolder.tvDate.setText(formatter.format(item.getDate_counted()));
        }


        viewHolder.bRemove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle(item.getName());
                builder.setMessage("Are you sure you want to remove this record?");

                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        itemStockCountList.remove(item);
                        notifyDataSetChanged();
                    }
                });

                builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

                builder.show();

            }
        });

        return row;
    }

    private static class DeliveryViewHolder {

        final TextView tvDate;
        final TextView tvName;
        final TextView tvExpectedOutput;
        final TextView tvPhysicalCount;
        final LinearLayout llItemStockCount;
        final TextView tvRemarks;
        final Button bRemove;

        public DeliveryViewHolder(View view) {
            tvDate = (TextView) view.findViewById(R.id.ItemStockCount_tvDate);
            tvName = (TextView) view.findViewById(R.id.ItemStockCount_tvName);
            tvExpectedOutput = (TextView) view.findViewById(R.id.ItemStockCount_tvExpectedOutput);
            tvPhysicalCount = (TextView) view.findViewById(R.id.ItemStockCount_tvPhysicalCount);
            llItemStockCount = (LinearLayout) view.findViewById(R.id.ItemStockCount_llItemStockCount);
            tvRemarks = (TextView) view.findViewById(R.id.ItemStockCount_tvRemarks);
            bRemove = (Button) view.findViewById(R.id.ItemStockCount_bRemove);
        }

    }
}
