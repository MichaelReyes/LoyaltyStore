package ph.com.gs3.loyaltystore.globals;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Locale;

/**
 * Created by Ervinne Sodusta on 8/17/2015.
 */
public class Constants {

    public static final int APP_SERVICE_PORT = 3001;
    public static final String APP_SERVICE_NAME = "_ph.com.gs3.loyalty.service";
    public static final String SERVER_ADDRESS = "http://192.168.0.130";
    public static final DecimalFormat DECIMAL_FORMAT = new DecimalFormat("#,###,##0.00");
    public static final SimpleDateFormat SIMPLE_DATE_FORMAT = new SimpleDateFormat(
            "yyyy-MM-dd HH:mm:ss", Locale.ENGLISH);

}
