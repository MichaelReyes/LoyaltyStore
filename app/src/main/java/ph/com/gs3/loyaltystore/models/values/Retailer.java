package ph.com.gs3.loyaltystore.models.values;

import android.content.Context;
import android.content.SharedPreferences;
import android.provider.Settings;

import java.io.Serializable;

/**
 * Created by Ervinne Sodusta on 8/17/2015.
 */
public class Retailer implements Serializable {

    public static final String TAG = Retailer.class.getSimpleName();

    private String deviceId;
    private String storeName;
    private int servicePortNumber;

    public static Retailer getDeviceRetailerFromSharedPreferences(Context context) {
        Retailer retailer = new Retailer();

        SharedPreferences settings = context.getSharedPreferences(TAG, Context.MODE_PRIVATE);

        retailer.deviceId = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
        retailer.storeName = settings.getString("STORE_NAME", "");
        retailer.servicePortNumber = settings.getInt("SERVICE_PORT_NUMBER", 3001);

        return retailer;
    }

    @Override
    public String toString() {
        return storeName + ":Service Port = " + servicePortNumber;
    }

    public void save(Context context) {

        SharedPreferences settings = context.getSharedPreferences(TAG, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = settings.edit();

        editor.putString("STORE_NAME", storeName);
        editor.putInt("SERVICE_PORT_NUMBER", servicePortNumber);

        editor.commit();
    }

    public DeviceInfo getDeviceInfo() {
        DeviceInfo deviceInfo = new DeviceInfo();

        deviceInfo.setOwnerDisplayName(storeName);
        deviceInfo.setType(DeviceInfo.Type.RETAILER);

        return deviceInfo;
    }

    //<editor-fold desc="Getters & Setters">

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }


    public String getStoreName() {
        return storeName;
    }

    public void setStoreName(String storeName) {
        this.storeName = storeName;
    }

    public int getServicePortNumber() {
        return servicePortNumber;
    }

    public void setServicePortNumber(int servicePortNumber) {
        this.servicePortNumber = servicePortNumber;
    }
    //</editor-fold>
}
