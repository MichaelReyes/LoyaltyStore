package ph.com.gs3.loyaltystore.views;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import ph.com.gs3.loyaltystore.R;

/**
 * Created by Bryan-PC on 05/04/2016.
 */
public class DeliveryProductListView extends LinearLayout {

    private View v;

    public TextView tvName;
    public TextView tvDate;
    public TextView tvQuantity;
    public TextView tvStatus;

    public Button bAccept;
    public Button bReject;

    public LinearLayout llProductDelivery;


    public DeliveryProductListView(Context context) {
        super(context);

        LayoutInflater inflater = LayoutInflater.from(context);
        v = inflater.inflate(R.layout.view_confirm_product_delivery, this, true);

        tvName = (TextView) v.findViewById(R.id.ProductDelivery_tvName);
        tvDate = (TextView) v.findViewById(R.id.ProductDelivery_tvDate);
        tvQuantity = (TextView) v.findViewById(R.id.ProductDelivery_tvQuantity);
        tvStatus = (TextView) v.findViewById(R.id.Expense_tvStatus);

        bAccept = (Button) v.findViewById(R.id.ProductDelivery_bAccept);
        bReject = (Button) v.findViewById(R.id.ProductDelivery_bReject);

        llProductDelivery = (LinearLayout) v.findViewById(R.id.ProductDelivery_llProductDelivery);

    }


}
