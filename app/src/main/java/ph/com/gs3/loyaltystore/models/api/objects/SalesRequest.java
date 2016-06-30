package ph.com.gs3.loyaltystore.models.api.objects;

import java.text.SimpleDateFormat;
import java.util.Locale;

import ph.com.gs3.loyaltystore.models.sqlite.dao.Sales;

/**
 * Created by Ervinne Sodusta on 2/18/2016.
 */
public class SalesRequest {

    public Long id;
    public String transaction_number;
    public Long store_id;
    public Long customer_id;
    public Float amount;
    public Float total_discount;
    public String transaction_date;
    public Float amount_received;
    public Float change;

    public static SalesRequest fromSales(Sales sales) {

        SalesRequest request = new SalesRequest();

        request.id = sales.getId();
        request.transaction_number = sales.getTransaction_number();
        request.store_id = sales.getStore_id();
        request.customer_id = sales.getCustomer_id();
        request.amount = sales.getAmount();
        request.total_discount = sales.getTotal_discount();
        request.amount_received = sales.getAmount_received();
        request.change = sales.getChange();

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US);

        request.transaction_date = dateFormat.format(sales.getTransaction_date());

        return request;

    }

}
