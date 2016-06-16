package ph.com.gs3.loyaltystore.models.api.objects;

/**
 * Created by Bryan-PC on 22/03/2016.
 */
public class SyncSalesAPIResponse {

    public Results results;
    public String status;
    public String error;
    public String error_message;


    public static class Results {
        public String request_id;
        public String request_tracking_number;
    }

}
