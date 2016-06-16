package ph.com.gs3.loyaltystore.models.api.objects;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Ervinne Sodusta on 2/29/2016.
 */
public class AcceptRejectProductRequest {

    @SerializedName("request_data")
    public String request_data;
    @SerializedName("form_id")
    public long form_id;
    @SerializedName("request_id")
    public long request_id;
    @SerializedName("action")
    public String action;

}
